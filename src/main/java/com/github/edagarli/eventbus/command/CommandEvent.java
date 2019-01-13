package com.github.edagarli.eventbus.command;


import com.github.edagarli.eventbus.bean.EventListenerDomain;
import com.github.edagarli.eventbus.event.BaseApplicationEvent;

/**
 * 事件对象工具类用于，用于数据暂存对象
 *
 * @author edagarli
 */
public class CommandEvent {

    private BaseApplicationEvent applicationEvent;

    private EventListenerDomain eventListenerDomain;

    public EventListenerDomain getEventListenerDomain() {
        return eventListenerDomain;
    }

    public void setEventListenerDomain(EventListenerDomain eventListenerDomain) {
        this.eventListenerDomain = eventListenerDomain;
    }

    public BaseApplicationEvent getApplicationEvent() {
        return applicationEvent;
    }

    public void setApplicationEvent(BaseApplicationEvent applicationEvent) {
        this.applicationEvent = applicationEvent;
    }

}
