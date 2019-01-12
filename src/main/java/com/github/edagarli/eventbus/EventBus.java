package com.github.edagarli.eventbus;

import com.github.edagarli.eventbus.bean.ApplicationEventListenerDomain;
import com.github.edagarli.eventbus.channel.MemoryChannel;
import com.github.edagarli.eventbus.command.CommandBus;
import com.github.edagarli.eventbus.commons.Constants;
import com.github.edagarli.eventbus.event.ApplicationEventListener;
import com.github.edagarli.eventbus.event.ApplicationEventType;
import com.github.edagarli.eventbus.event.BaseApplicationEvent;
import com.github.edagarli.eventbus.utils.ClassUtil;
import com.github.edagarli.eventbus.utils.CommonMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author edagarli(卤肉)
 * Email: lizhi@edagarli.com
 * github: http://github.com/edagarli
 * Date: 2017/12/14
 * Time: 23:40
 * Desc: 注解标记需要扫描的监听器
 * <pre>
 *             (1) 消息的发布入口；
 *             (2) 订阅者的注册(采用扫描注解自动注册);
 *             (3) 事件们与订阅者们的结构组装;
 *             (4) 重试服务开关(待实现)
 *             (5) 消息处理异步分发器启停开关
 *         </pre>
 */
public class EventBus implements ApplicationContextAware {
    private static Logger logger = LoggerFactory.getLogger(EventBus.class);
    /**
     * 通道
     */
    private Channel channel;
    /**
     * 重复key的map，使用监听的type，取出所有的监听器
     */
    private static CommonMultimap<ApplicationEventType, ApplicationEventListenerDomain> map = null;
    /**
     * 默认不扫描jar包
     */
    private boolean scanJar = false;
    /**
     * 默认扫描所有的包
     */
    private String scanPackage = "";
    /**
     * 默认不开启全局异步
     */
    private boolean enableAsync = false;
    /**
     * 队列大小
     */
    private int conBufferSize;
    /**
     * 异步线程大小
     */
    private int asyncThreads;
    /**
     * 是否已经启动
     */
    private AtomicBoolean started;
    /**
     * 分发器
     */
    private Dispatcher dispatcher;

    private ApplicationContext applicationContext;

    /**
     * 构造EventBus
     */
    public EventBus() {
        this(new MemoryChannel(), false, "");
    }

    /**
     * 构造EventBus
     *
     * @param scanJar     是否扫描jar
     * @param scanPackage 扫描的包名
     */
    public EventBus(boolean scanJar, String scanPackage) {
        this(new MemoryChannel(), scanJar, scanPackage);
    }

    /**
     * 构造EventBus
     *
     * @param channel 事件处理通道
     */
    public EventBus(Channel channel) {
        this(channel, false, "");
    }

    /**
     * 构造EventBus
     *
     * @param channel     事件处理通道
     * @param scanJar     是否扫描Jar
     * @param scanPackage 扫描的jar包位置
     */
    public EventBus(Channel channel, boolean scanJar, String scanPackage) {
        this.channel = channel;
        this.scanJar = scanJar;
        this.scanPackage = scanPackage;
        this.started = new AtomicBoolean(false);
    }

    /**
     * 注册事件通道
     *
     * @return EventBus
     * @since 1.0.0
     */
    public EventBus registerChannel() {
        return registerChannel(new MemoryChannel());
    }

    /**
     * 注册事件通道
     *
     * @param channel 事件处理通道
     * @return EventBus
     * @since 1.0.0
     */
    public EventBus registerChannel(Channel channel) {
        this.channel = channel;
        return this;
    }

    /**
     * 开启全局异步
     *
     * @return EventBus
     * @since 1.0.0
     */
    public EventBus async() {
        return async(0, 0);
    }

    /**
     * 开启全局异步
     *
     * @param conBufferSize bufferSize大小 默认1024 2的幂次方
     * @param asyncThreads  线程池的大小，不传或小于1时默认为8
     * @return EventBus
     * @since 1.0.0
     */
    public EventBus async(int conBufferSize, int asyncThreads) {
        this.enableAsync = true;
        setAsyncThreads(asyncThreads);
        setConBufferSize(conBufferSize);
        return this;
    }

    /**
     * 从jar包中搜索监听器
     *
     * @return EventBus
     * @since 1.0.0
     */
    public EventBus scanJar() {
        this.scanJar = true;
        return this;
    }

    /**
     * 指定扫描的包
     *
     * @param scanPackage 指定扫描的包
     * @return EventBus
     * @since 1.0.0
     */
    public EventBus scanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
        return this;
    }

    /**
     * eventBus启动
     *
     * @return
     * @since 1.0.0
     */
    public synchronized boolean start() {
        if (started.get()) {
            logger.error("Event Bus is Running!");
            return false;
        }
        registerListener();
        if (enableAsync) {
            if (dispatcher != null) {
                dispatcher.stop();
            }
            dispatcher = new Dispatcher(this).start();
            CommandBus.init(enableAsync, map, dispatcher.getConRingBuffer());
        } else {
            CommandBus.init(enableAsync, map);
        }
        started.set(true);
        logger.info("Event Bus started!");
        return true;
    }

    /**
     * eventBus优雅停机
     *
     * @return
     * @since 1.0.0
     */
    public synchronized boolean stop() {
        //确保新消息无法发送
        started.set(false);

        if (null != map) {
            map.clear();
            map = null;
        }
        if (dispatcher != null) {
            dispatcher.stop();
        }

        logger.info("Event Bus Terminated !");
        return true;
    }

    /**
     * 发布事件
     *
     * @param event 事件
     * @return
     * @since 1.0.0
     */
    public boolean publish(final BaseApplicationEvent event) {
        publish(ApplicationEventType.DEFAULT_TAG, event);
        return true;
    }

    /**
     * 发布事件
     *
     * @param tag   事件标签
     * @param event 事件
     * @return
     * @since 1.0.0
     */
    public boolean publish(final String tag, final BaseApplicationEvent event) {
        if (started.get()) {
            if (event == null) {
                return false;
            }
            if (channel == null) {
                channel = new MemoryChannel();
            }
            channel.publish(tag, event);
        }
        return true;
    }

    /**
     * 注册订阅者[需要在event-bus启动之前]
     * <p>
     * 订阅者包括：1：包含有@Listener注解方法的类。2:实现ApplicationEventListener接口(待实现)
     *
     * @since 1.0.0
     */
    private synchronized void registerListener() {
        if (null != map) {
            return;
        }
        // 扫描注解
        Set<Class<?>> clazzSet = ClassUtil.scanPackageByAnnotation(scanPackage, scanJar, Listener.class);
        if (clazzSet.isEmpty()) {
            logger.error(Constants.Logger.APP_EXCEPTION + "Listener is empty! Please check it!");
        }
        List<Class<? extends ApplicationEventListener>> allListeners = new ArrayList<>();
        // 装载所有 {@code ApplicationEventListener} 的子类
        Class superClass;
        for (Class<?> clazz : clazzSet) {
            superClass = ApplicationEventListener.class;
            if (superClass.isAssignableFrom(clazz) && !superClass.equals(clazz)) {
                allListeners.add((Class<? extends ApplicationEventListener>) clazz);
            }
        }
        if (allListeners.isEmpty()) {
            logger.error(Constants.Logger.APP_EXCEPTION + "Listener is empty! Please check @Listener is right?");
        }
        // 监听器排序
        sortListeners(allListeners);
        // 重复key的map，使用监听的type，取出所有的监听器
        map = new CommonMultimap<>();
        Type type;
        ApplicationEventListener listener;
        for (Class<? extends ApplicationEventListener> clazz : allListeners) {
            // 获取监听器上的泛型信息
            type = ((ParameterizedType) clazz.getGenericInterfaces()[0]).getActualTypeArguments()[0];
            // 实例化监听器(改成用spring管理注解)
            listener = applicationContext.getBean(clazz);
            // 监听器上的注解
            Listener annotation = clazz.getAnnotation(Listener.class);
            String tag = annotation.tag();
            ApplicationEventType applicationEventType = new ApplicationEventType(tag, type);
            map.put(applicationEventType, new ApplicationEventListenerDomain(listener, annotation.enableAsync()));
            if (logger.isDebugEnabled()) {
                logger.debug(Constants.Logger.APP_MESSAGE + clazz + " init~");
            }
        }
    }

    /**
     * 对所有的监听器进行排序
     */
    private void sortListeners(List<Class<? extends ApplicationEventListener>> listeners) {
        Collections.sort(listeners, new Comparator<Class<? extends ApplicationEventListener>>() {

            @Override
            public int compare(Class<? extends ApplicationEventListener> o1,
                               Class<? extends ApplicationEventListener> o2) {

                int x = o1.getAnnotation(Listener.class).priority();
                int y = o2.getAnnotation(Listener.class).priority();
                return (x < y) ? -1 : ((x == y) ? 0 : 1);
            }
        });
    }

    public Channel getChannel() {
        return channel;
    }

    public int getConBufferSize() {
        return conBufferSize;
    }

    public void setConBufferSize(int conBufferSize) {
        this.conBufferSize = conBufferSize;
    }

    public int getAsyncThreads() {
        return asyncThreads;
    }

    public void setAsyncThreads(int asyncThreads) {
        this.asyncThreads = asyncThreads;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
