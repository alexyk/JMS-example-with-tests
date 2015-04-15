package com.berc.jpm.test;

import javax.ejb.Remote;

/**
 * Created by Rastislav Bertusek on 14. 4. 2015.
 * interface for Remote CDI lookup. Used for hooking up from appclient console remote app
 */
@Remote
public interface SenderTest {
    public void testProduceMessages();
}
