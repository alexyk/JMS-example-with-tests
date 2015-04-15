package com.berc.jpm.scheduler;

import javax.ejb.Local;
import javax.ejb.Remote;

/**
 * Created by ray on 14. 4. 2015.
 * abstraction interface for ResourceScheduler
 */
@Remote
public interface ResourceScheduler {
    void startScheduler();
}
