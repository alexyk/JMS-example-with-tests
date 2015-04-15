package com.berc.jpm.scheduler;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.*;

/**
 * Created by ray on 14. 4. 2015.
 * used for receiving of new messages for scheduler
 */
@Stateless
public class JpmNewMessageReceiver {
    @Resource(mappedName = "jms/JPMNewMessageConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/JPMNewMessageQueue")
    private Queue queue;

    public void startReceiveMessages(MessageListener messageListener) {
        Connection connection;
        MessageConsumer messageConsumer;
        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            messageConsumer = session.createConsumer(queue);
            messageConsumer.setMessageListener(messageListener);
            connection.start();

            System.out.println("JpmNewMessageReceiver communication started.");
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

