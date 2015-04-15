package com.berc.jpm.scheduler;

import com.berc.jpm.utils.ConfigReader;
import com.berc.jpm.gateway.Gateway;
import com.berc.jpm.message.*;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Created by rasto on 13.4.2015.
 * implementation of resource scheduler. Receives, sends messages and handles queue
 */
@Stateless
@Singleton
public class JpmResourceScheduler implements ResourceScheduler {

    // semaphore for handling number of resources
    private Semaphore availableResources;

    // configurations from config.properties
    private Integer numberOfResources;
    private Boolean groupsCanBeCancelled;
    private Boolean groupsCanBeTerminated;

    // canceled and terminated group conditions
    Map<Integer, GroupProperty> groupProperties = new HashMap<>();

    // message queue
    List<JpmMessage> jpmMessageQueue = new ArrayList<>();

    @Inject @Prior
    private JpmMessageQueueSorter jpmMessageQueueSorter;

    @Inject
    private JpmMessageCompletedReceiver messageCompletedReceiver;

    @Inject
    private JpmNewMessageReceiver newMessageReceiver;

    @Inject
    private ConfigReader configReader;

    @Inject
    private Gateway jpmGateway;

    public List<JpmMessage> getJpmMessageQueue() {
        return jpmMessageQueue;
    }

    public void setJpmMessageQueue(List<JpmMessage> jpmMessageQueue) {
        this.jpmMessageQueue = jpmMessageQueue;
    }

    /**
     * called after constructin and CDI init
     */
    @PostConstruct
    private void postConstruct() {
        System.out.println("post JpmResourceScheduler");

        // read configuration from config.properties
        numberOfResources = Integer.parseInt(configReader.readProperty("resources", "1"));
        groupsCanBeCancelled = Boolean.parseBoolean(configReader.readProperty("groups.canbecanceled", "false"));
        groupsCanBeTerminated = Boolean.parseBoolean(configReader.readProperty("groups.canbeterminated", "false"));

        // init new messages receiving handling
        newMessageReceiver.startReceiveMessages(new MessageListener() {
            @Override
            public void onMessage(javax.jms.Message message) {
                ObjectMessage objectMessage = (ObjectMessage) message;
                try {
                    if (objectMessage.getObject() instanceof JpmMessage) {
                        // received new message
                        JpmMessage jpmMessage = (JpmMessage) objectMessage.getObject();
                        System.out.print("received new message: " + jpmMessage.getId() + " (" + jpmMessage.getGroupId() + ")");
                        if (groupProperties.containsKey(jpmMessage.getGroupId())) {
                            // group has cancel or termination conditions
                            GroupProperty groupProperty = groupProperties.get(jpmMessage.getGroupId());
                            if (groupProperty.getTerminated()) {
                                // group was terminated, we throw exception
                                System.out.print("throwing exception for unexpected message: " + jpmMessage.getId() + " (" + jpmMessage.getGroupId() + ")");
                                throw new RuntimeException("Unexpected message with terminated group Id: " + groupProperty.getGroupId());
                            }
                            if (groupProperty.getCanceled()) {
                                // group is canceled, we ignore received new message
                                System.out.print("dropping canceled message: " + jpmMessage.getId() + " (" + jpmMessage.getGroupId() + ")");
                                return;
                            }
                        }
                        if (jpmMessage.getTerminationMessage()) {
                            // new message is termination message. That means, no more messages will be received from this group
                            GroupProperty groupProperty = groupProperties.containsKey(jpmMessage.getGroupId()) ?
                                    groupProperties.get(jpmMessage.getGroupId()) : new GroupProperty(jpmMessage.getGroupId(), false, false);
                            groupProperty.setTerminated(true);
                            groupProperties.put(jpmMessage.getGroupId(), groupProperty);
                        }
                        // handle new message
                        JpmResourceScheduler.this.handleNewMessage(jpmMessage);
                    } else {
                        // received cancelation group message
                        GroupCancelMessage groupCancelMessage = (GroupCancelMessage) objectMessage.getObject();
                        System.out.print("received cancellation message for group: " + groupCancelMessage.getGroupId());
                        GroupProperty groupProperty = groupProperties.containsKey(groupCancelMessage.getGroupId()) ?
                                groupProperties.get(groupCancelMessage.getGroupId()) : new GroupProperty(groupCancelMessage.getGroupId(), false, false);
                        groupProperty.setCanceled(true);
                        groupProperties.put(groupCancelMessage.getGroupId(), groupProperty);
                        // remove all messages from canceled group
                        JpmResourceScheduler.this.removeAllMessagesWithGroupId(groupCancelMessage.getGroupId());
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

        // init completed messages receiving handling
        messageCompletedReceiver.startReceiveMessages(new MessageListener() {
            @Override
            public void onMessage(javax.jms.Message message) {
                TextMessage textMessage = (TextMessage) message;
                try {
                    System.out.print("received completed message: " + textMessage.getText());
                    JpmResourceScheduler.this.handleMessageCompeted(textMessage.getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

        this.availableResources = new Semaphore(numberOfResources, true);
        System.out.println("start with number of resources: " + numberOfResources);
    }

    /**
     * remove all canceled messages for group groupId
     * @param groupId
     */
    synchronized private void removeAllMessagesWithGroupId(Integer groupId) {
        boolean found = true;
        while(found) {
            found = false;
            for(JpmMessage message : getJpmMessageQueue()) {
                if (message.getGroupId().equals(groupId)) {
                    getJpmMessageQueue().remove(message);
                    found = true;
                    break;
                }
            }
        }
    }

    /**
     * handle completed message processing from Gateway
     * @param message
     */
    synchronized private void handleMessageCompeted(String message) {
        this.availableResources.release();
        readMessageFromQueueAndSendToGateway();
    }

    /**
     * handle new message
     * @param message
     */
    synchronized private void handleNewMessage(JpmMessage message) {
        jpmMessageQueueSorter.insertMessageToQueue(getJpmMessageQueue(), message);
        StringBuilder builder = new StringBuilder();
        builder.append("after inserting message: " + message.getId() + " (" + message.getGroupId() + "): ");
        for(JpmMessage m : getJpmMessageQueue()) {
            builder.append(m.getId() + "[" + m.getGroupId() + "], ");
        }
        System.out.println(builder.toString());
        readMessageFromQueueAndSendToGateway();
    }

    /**
     * process message queue and send to Gateway if is free resource
     */
    private void readMessageFromQueueAndSendToGateway() {
        if (getJpmMessageQueue().size() > 0 && this.availableResources.tryAcquire()) {
            JpmMessage message = getJpmMessageQueue().get(0);
            getJpmMessageQueue().remove(0);
            System.out.println("sending gateway message: " + message.getId() + " (" + message.getGroupId() + ")");
            jpmGateway.send(message);
        }
    }

    /**
     * start of scheduler. Is empty, implementatino of interface
     */
    public void startScheduler() {};
}