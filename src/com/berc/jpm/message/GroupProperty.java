package com.berc.jpm.message;

/**
 * Created by rasto on 14.4.2015.
 * POJO for canceled and terminated group properties
 */
public class GroupProperty {
    private Boolean canceled;
    private Boolean terminated;
    private Integer groupId;

    public Boolean getCanceled() {
        return canceled;
    }
    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }
    public Boolean getTerminated() {
        return terminated;
    }
    public void setTerminated(Boolean terminated) {
        this.terminated = terminated;
    }
    public Integer getGroupId() {
        return groupId;
    }
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public GroupProperty(Integer groupId, Boolean canceled, Boolean terminated) {
        this.groupId = groupId;
        this.canceled = canceled;
        this.terminated = terminated;
    }
}
