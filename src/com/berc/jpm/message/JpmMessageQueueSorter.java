package com.berc.jpm.message;

import java.util.List;

/**
 * Created by Rastislav Bertusek on 14.4.2015.
 * interface for scheduler queue sorting algorithms
 */
public interface JpmMessageQueueSorter {
    public void insertMessageToQueue(List<JpmMessage> messageQueue, JpmMessage message);
}
