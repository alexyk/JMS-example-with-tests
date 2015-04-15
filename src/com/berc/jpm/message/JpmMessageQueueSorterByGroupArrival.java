package com.berc.jpm.message;

import javax.ejb.Stateless;
import java.util.List;

/**
 * Created by Ratsislav Bertusek on 14.4.2015.
 * sorting algorithm for scheduler queue. Sort by message arrival group.
 */
@Stateless @Prior
public class JpmMessageQueueSorterByGroupArrival implements JpmMessageQueueSorter {

    @Override
    public void insertMessageToQueue(List<JpmMessage> messageQueue, JpmMessage message) {
        boolean insertedItem = false;
        boolean foundGroupId = false;
        for(int i = 0; i < messageQueue.size(); i++) {
            JpmMessage sortedMessage = messageQueue.get(i);
            if (sortedMessage.getGroupId().equals(message.getGroupId())) {
                // detected same group
                foundGroupId = true;
            }
            // insert item to the end of group, if group was found
            if (!sortedMessage.getGroupId().equals(message.getGroupId()) && foundGroupId) {
                messageQueue.add(i, message);
                insertedItem = true;
                break;
            }
        }
        // not found group, so add item to the end of queue
        if (!insertedItem) {
            messageQueue.add(message);
        }
    }
}
