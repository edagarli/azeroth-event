package com.github.edagarli.eventbus.command;

import com.github.edagarli.eventbus.Constants;
import com.github.edagarli.eventbus.event.ApplicationEvent;
import com.github.edagarli.eventbus.event.ApplicationEventListenerHelper;
import com.github.edagarli.eventbus.event.ApplicationEventType;
import com.github.edagarli.eventbus.utils.CommonMultimap;
import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.RingBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;


/**
 * 事件工具类
 *
 */
public class CommandBus {

    private static RingBuffer<CommandEvent> conRingBuffer;

    private static CommonMultimap<ApplicationEventType, ApplicationEventListenerHelper> map;

    private static boolean enableAsync;

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandBus.class);

    /**
     * 构造器
     */
    private CommandBus() {

    }

    /**
     * 初始化事件发生器
     *
     * @param enableAsync   是否异步
     * @param map           注册器存储map
     * @param conRingBuffer 队列操作
     * @return EventBus
     * @since 1.0.0
     */
    public static void init(boolean enableAsync, CommonMultimap<ApplicationEventType, ApplicationEventListenerHelper> map, RingBuffer<CommandEvent> conRingBuffer) {
        CommandBus.conRingBuffer = conRingBuffer;
        CommandBus.map = map;
        CommandBus.enableAsync = enableAsync;
    }

    /**
     * 初始化事件发生器
     *
     * @param enableAsync 是否异步
     * @param map         注册器存储map
     * @return EventBus
     * @since 1.0.0
     */
    public static void init(boolean enableAsync, CommonMultimap<ApplicationEventType, ApplicationEventListenerHelper> map) {
        CommandBus.map = map;
        CommandBus.enableAsync = enableAsync;
    }

    /**
     * 发布事件
     *
     * @param helper 处理器
     * @param event  事件
     * @since 1.0.0
     */
    private static boolean publish(final ApplicationEventListenerHelper helper, final ApplicationEvent event) {
        try {
            long seq = conRingBuffer.tryNext();
            //the remaining capacity of the buffer < the size of the buffer * 0.2
            //队列超出阀值得时候 取消队列并发消费 日志输出提示告警
            if (conRingBuffer.remainingCapacity() < conRingBuffer.getBufferSize() * 0.2) {
                enableAsync = false;
                LOGGER.warn(Constants.Logger.APP_MESSAGE + "commandBus consume warn message, remainingCapacity size:" + conRingBuffer.remainingCapacity() + ",conRingBuffer size:" + conRingBuffer.getBufferSize());
            }
            CommandEvent commandEvent = conRingBuffer.get(seq);
            commandEvent.setApplicationEvent(event);
            commandEvent.setApplicationEventListenerHelper(helper);
            conRingBuffer.publish(seq);
        } catch (InsufficientCapacityException e) {
            LOGGER.error(Constants.Logger.APP_EXCEPTION + "conRingBuffer too late to consume error message,you may increase conBufferSize/asyncThreads " + e.toString());
            return false;
        }
        return true;
    }

    /**
     * 发布事件
     *
     * @param applicationEventType 事件封装
     * @param event                事件
     * @since 1.0.0
     */
    public static void publish(final ApplicationEventType applicationEventType, final ApplicationEvent event) {
        Collection<ApplicationEventListenerHelper> listenerList = map.get(applicationEventType);
        if (listenerList != null && !listenerList.isEmpty()) {
            for (final ApplicationEventListenerHelper helper : listenerList) {
                if (enableAsync && helper.enableAsync) {
                    publish(helper, event);
                } else {
                    handle(helper, event);
                }
            }
        }
    }

    /**
     * 处理事件
     *
     * @param helper 事件处理器
     * @param event  事件
     * @since 1.0.0
     */
    public static void handle(final ApplicationEventListenerHelper helper, final ApplicationEvent event) {
        try {
            helper.listener.onApplicationEvent(event);
        } catch (Exception e) {
            LOGGER.error(Constants.Logger.APP_EXCEPTION + "commandBus handle event error message" + e.toString());
        }
    }

}
