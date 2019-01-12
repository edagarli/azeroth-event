package com.dfire.mmt.eventbus.command;


import com.dfire.mmt.eventbus.event.ApplicationEvent;
import com.dfire.mmt.eventbus.event.ApplicationEventListenerHelper;

/**
 * 事件对象工具类用于，用于数据暂存对象
 */
public class CommandEvent {

    ApplicationEvent applicationEvent;

    ApplicationEventListenerHelper applicationEventListenerHelper;

    public ApplicationEventListenerHelper getApplicationEventListenerHelper() {
        return applicationEventListenerHelper;
    }

    public void setApplicationEventListenerHelper(ApplicationEventListenerHelper applicationEventListenerHelper) {
        this.applicationEventListenerHelper = applicationEventListenerHelper;
    }

    public ApplicationEvent getApplicationEvent() {
        return applicationEvent;
    }

    public void setApplicationEvent(ApplicationEvent applicationEvent) {
        this.applicationEvent = applicationEvent;
    }

}
