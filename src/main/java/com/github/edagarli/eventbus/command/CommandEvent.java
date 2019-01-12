package com.github.edagarli.eventbus.command;


import com.github.edagarli.eventbus.bean.ApplicationEventListenerDomain;
import com.github.edagarli.eventbus.event.BaseApplicationEvent;

/**
 * 事件对象工具类用于，用于数据暂存对象
 *
 * @author edagarli
 */
public class CommandEvent {

    private BaseApplicationEvent applicationEvent;

    private ApplicationEventListenerDomain applicationEventListenerDomain;

    public ApplicationEventListenerDomain getApplicationEventListenerDomain() {
        return applicationEventListenerDomain;
    }

    public void setApplicationEventListenerDomain(ApplicationEventListenerDomain applicationEventListenerDomain) {
        this.applicationEventListenerDomain = applicationEventListenerDomain;
    }

    public BaseApplicationEvent getApplicationEvent() {
        return applicationEvent;
    }

    public void setApplicationEvent(BaseApplicationEvent applicationEvent) {
        this.applicationEvent = applicationEvent;
    }

}
