package com.berc.jpm.gateway;

import com.berc.jpm.message.Message;

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import java.util.Random;

/**
 * Created by Rastislav Bertusek on 13.4.2015.
 */

/**
 * implementation of Gateway interface. Expencive resource
 */
@Stateless
@Singleton
@Asynchronous
public class JpmGateway implements Gateway {

    @Override
    public void send(Message msg) {
        runExtensiveWork(msg);
    }

    /**
     * simulation of extensive work
     * @param message processed message
     */
    private void runExtensiveWork(Message message) {
        try {
            Random randomGenerator = new Random();
            Thread.sleep(randomGenerator.nextInt(2)*100 + 100); //100 - 300 milliseconds sleep
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        // completed message processing
        message.completed();
    }
}
