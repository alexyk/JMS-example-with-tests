package com.berc.jpm.message;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;

/**
 * Created by  Rastislav Bertusek on 13.4.2015.
 * implementation of Message interface with attributes used by scheduler
 */
public class JpmMessage implements Message, Serializable {

    /** mesage Id */
    private Long Id;
    /** group Id */
    private Integer groupId;
    /** signaling termination message */
    private Boolean terminationMessage = false;

    public Long getId() {
        return Id;
    }
    public void setId(Long id) {
        Id = id;
    }
    public Integer getGroupId() {
        return groupId;
    }
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
    public Boolean getTerminationMessage() {
        return terminationMessage;
    }
    public void setTerminationMessage(Boolean terminationMessage) {
        this.terminationMessage = terminationMessage;
    }

    /**
     * construction after construction and CDI init
     */
    @PostConstruct
    private void postConstruct() {
        System.out.println("post JpmMessage");
    }

    /**
     * message processing in resource completed
     */
    @Override
    public void completed() {
        System.out.println("completed message: " + getId());
        try {
            JpmMessageCompletedSender sender = (JpmMessageCompletedSender) new InitialContext().lookup("java:global/jpm/JpmMessageCompletedSender");
            sender.produceMessage(this);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
