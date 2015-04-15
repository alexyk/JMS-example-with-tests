package com.berc.jpm.message;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.*;

/**
 * Created by Rastislav Bertusek on 14.4.2015.
 * sender for completed message event
 */
@Stateless
public class JpmMessageCompletedSender {
    @Resource(mappedName = "jms/JPMMessageCompletedConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/JPMMessageCompletedQueue")
    private Queue queue;

    /**
     * send event about completed message with message Id
     * @param message completed message
     */
    public void produceMessage(JpmMessage message) {
        MessageProducer messageProducer;
        TextMessage textMessage;
        try {
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            messageProducer = session.createProducer(queue);
            textMessage = session.createTextMessage();

            textMessage.setText(message.getId().toString());
            System.out.println("sending completed message: " + textMessage.getText());
            messageProducer.send(textMessage);

            messageProducer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
