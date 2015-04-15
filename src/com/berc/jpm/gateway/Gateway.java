package com.berc.jpm.gateway;

import com.berc.jpm.message.Message;

/**
 * Created by Rastislav Bertusek on 13.4.2015.
 */
public interface Gateway {
    public void send(Message msg);
}
