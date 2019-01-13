package com.github.edagarli.eventbus.listener;

import com.github.edagarli.eventbus.EventBus;
import com.github.edagarli.eventbus.bean.EventListenerDomain;
import com.github.edagarli.eventbus.commons.Constants;
import com.github.edagarli.eventbus.event.ApplicationEventListener;
import com.github.edagarli.eventbus.event.ApplicationEventType;
import com.github.edagarli.eventbus.utils.ClassUtil;
import com.github.edagarli.eventbus.utils.CommonMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author : lurou
 * Email: lurou@2dfire.com
 * github: http://github.com/edagarli
 * Date: 2019/1/13
 * Time: 14:56
 * Desc:
 */
public class ListenerRegister{

    private static Logger logger = LoggerFactory.getLogger(ListenerRegister.class);

    private EventBus eventBus;

    public ListenerRegister(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * 注册订阅者[需要在event-bus启动之前]
     * <p>
     * 订阅者包括：1：包含有@Listener注解方法的类。2:实现ApplicationEventListener接口
     * </p>
     */
    public synchronized CommonMultimap<ApplicationEventType, EventListenerDomain> registerListener() {
        // 扫描注解
        Set<Class<?>> clazzSet = ClassUtil.scanPackageByAnnotation(eventBus.getScanPackage(), eventBus.isScanJar(), Listener.class);

        if (clazzSet.isEmpty()) {
            logger.error(Constants.Logger.EXCEPTION + "Listener is empty! Please check it!");
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
            logger.error(Constants.Logger.EXCEPTION + "Listener is empty! Please check @Listener is right?");
        }
        // 监听器排序
        sortListeners(allListeners);
        // 重复key的map，使用监听的type，取出所有的监听器
        CommonMultimap<ApplicationEventType, EventListenerDomain> map = new CommonMultimap<>();
        Type type;
        ApplicationEventListener listener;

        for (Class<? extends ApplicationEventListener> clazz : allListeners) {
            // 获取监听器上的泛型信息
            type = ((ParameterizedType) clazz.getGenericInterfaces()[0]).getActualTypeArguments()[0];
            // 实例化监听器(改成用spring管理注解)
            listener = eventBus.getApplicationContext().getBean(clazz);
            // 监听器上的注解
            Listener annotation = clazz.getAnnotation(Listener.class);
            String tag = annotation.tag();
            ApplicationEventType applicationEventType = new ApplicationEventType(tag, type);
            map.put(applicationEventType, new EventListenerDomain(listener, annotation.enableAsync()));
            if (logger.isDebugEnabled()) {
                logger.debug(Constants.Logger.MESSAGE + clazz + " init~");
            }
        }
        return map;
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
}
