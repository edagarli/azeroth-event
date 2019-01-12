package com.dfire.mmt.eventbus.channel;

import com.dfire.mmt.eventbus.command.CommandBus;
import com.dfire.mmt.eventbus.event.ApplicationEvent;
import com.dfire.mmt.eventbus.event.ApplicationEventListenerHelper;
import com.dfire.mmt.eventbus.event.ApplicationEventType;

/**
 * @author edagarli(卤肉)
 *         Email: lizhi@edagarli.com
 *         github: http://github.com/edagarli
 *         Date: 2017/12/14
 *         Time: 23:40
 *         Desc: 内存事件处理器
 */
public class MemoryChannel extends AbstractChannel {


    @Override
    public void handle(ApplicationEventListenerHelper helper, ApplicationEvent event) {
        CommandBus.handle(helper, event);
    }

    @Override
    public void publish(String tag, ApplicationEvent event) {
        ApplicationEventType applicationEventType = new ApplicationEventType(tag, event.getClass());
        CommandBus.publish(applicationEventType, event);
    }


}
