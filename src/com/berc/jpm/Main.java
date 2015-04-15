package com.berc.jpm;

import com.berc.jpm.scheduler.ResourceScheduler;
import com.berc.jpm.test.SenderTest;

import javax.naming.InitialContext;
import java.util.Properties;

/**
 * created Rastislav Bertusek
 * main class for run from console
 */
public class Main {

    /**
     * default setting for localhost CDI recognition
     */
    private static class Util {

        private Properties getInitProperties() {
            Properties result = new Properties();

            // We need to tell the context where and how to look
            result.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
            result.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
            result.setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");

            // Should not be necessary for local test (default values), but currently is
            result.setProperty("org.omg.CORBA.ORBInitialHost", "localhost");
            result.setProperty("org.omg.CORBA.ORBInitialPort", "3700");

            return result;
        }
    }

    /**
     * main start point of program. Run from glassfish console: appclient.bat -client jpm.jar
     * @param a
     * @throws Exception
     */
    public static void main(String[] a) throws Exception {
        Properties initProperties = new Util().getInitProperties();

        ResourceScheduler service = null;
        service = (ResourceScheduler) new InitialContext(initProperties).lookup("java:global/jpm/JpmResourceScheduler");
        service.startScheduler();

        SenderTest sender = (SenderTest) new InitialContext(initProperties).lookup("java:global/jpm/JpmSenderTest");
        sender.testProduceMessages();
    }
}