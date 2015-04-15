package com.berc.jpm.test;

import com.berc.jpm.message.GroupCancelMessage;
import com.berc.jpm.message.JpmMessage;
import org.junit.Test;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.jms.*;
import java.io.Serializable;
import java.util.Random;

/**
 * created Rastislav Bertusek
 * main class for function testing of the system
 */
@Stateless
@Singleton
public class JpmSenderTest implements SenderTest, Serializable {
    @Resource(mappedName = "jms/JPMNewMessageConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/JPMNewMessageQueue")
    private Queue queue;

    /**
     * produces testing messages for scheduler
     */
    @Test
    public void testProduceMessages() {
        MessageProducer messageProducer;
        ObjectMessage objectMessage;
        JpmMessage jpmMessage;
        long index = 0;
        try {
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            messageProducer = session.createProducer(queue);
            objectMessage = session.createObjectMessage();

            // testing of sorting algorithms
            long i = 0;
            Random random = new Random();
            for(; i < 50; i++) {
                jpmMessage = new JpmMessage();
                jpmMessage.setId(i);
                jpmMessage.setGroupId(random.nextInt(10));
                objectMessage.setObject(jpmMessage);
                System.out.println("sending new message: " + jpmMessage.getId());
                messageProducer.send(objectMessage);
            }

            // testing of cancel group message
            Thread.sleep(500);
            GroupCancelMessage groupCancelMessage = new GroupCancelMessage();
            groupCancelMessage.setGroupId(0);
            objectMessage.setObject(groupCancelMessage);
            System.out.println("sending cancellation group: " + groupCancelMessage.getGroupId());
            messageProducer.send(objectMessage);

            // testing of ignoring cancel group
            for(; i < 100; i++) {
                jpmMessage = new JpmMessage();
                jpmMessage.setId(i);
                jpmMessage.setGroupId(random.nextInt(10));
                objectMessage.setObject(jpmMessage);
                System.out.println("sending new message: " + jpmMessage.getId());
                messageProducer.send(objectMessage);
            }

            // testing of termination message
            Thread.sleep(500);
            jpmMessage = new JpmMessage();
            jpmMessage.setId(i);
            jpmMessage.setGroupId(1);
            jpmMessage.setTerminationMessage(true);
            objectMessage.setObject(jpmMessage);
            System.out.println("sending termination message: " + jpmMessage.getId());
            messageProducer.send(objectMessage);

            // testing of throwing exception for terminated message group
            for(; i < 150; i++) {
                jpmMessage = new JpmMessage();
                jpmMessage.setId(i);
                jpmMessage.setGroupId(random.nextInt(10));
                objectMessage.setObject(jpmMessage);
                System.out.println("sending new message: " + jpmMessage.getId());
                messageProducer.send(objectMessage);
            }

            messageProducer.close();
            session.close();
            connection.close();
        }
        catch (JMSException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}