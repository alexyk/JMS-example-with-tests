package com.berc.jpm.message;

import java.io.Serializable;

/**
 * Created by ray on 15. 4. 2015.
 * event indicating cancellation of messages with groupId
 */
public class GroupCancelMessage implements Serializable {
    private Integer groupId;

    public Integer getGroupId() {
        return groupId;
    }
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}
