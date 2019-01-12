package com.github.edagarli.eventbus;


import com.github.edagarli.eventbus.event.ApplicationEvent;
import com.github.edagarli.eventbus.event.ApplicationEventListenerHelper;

/**
 * @author edagarli(卤肉)
 *         Email: lizhi@edagarli.com
 *         github: http://github.com/edagarli
 *         Date: 2017/12/14
 *         Time: 23:40
 *         Desc: 事件通道接口
 */
public interface Channel {

    /**
     * helper handle event for async
     *
     * @param helper 事件处理器
     * @param event  事件
     * @return
     */
    void handle(final ApplicationEventListenerHelper helper, final ApplicationEvent event);


    /**
     * eventBus publish event
     *
     * @param tag   事件标签
     * @param event 事件
     * @return
     */
    void publish(final String tag, final ApplicationEvent event);


}
