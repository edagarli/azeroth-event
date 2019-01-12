package com.github.edagarli.eventbus.bean;


import com.github.edagarli.eventbus.event.ApplicationEventListener;

/**
 * @author edagarli(卤肉)
 * Email: lizhi@edagarli.com
 * github: http://github.com/edagarli
 * Date: 2017/12/14
 * Time: 23:40
 * Desc: 监听器工具类用于，用于数据暂存对象
 */
public class ApplicationEventListenerDomain {

    /**
     * 监听器
     */
    public final ApplicationEventListener listener;
    /**
     * 是否异步
     */
    public final boolean enableAsync;

    /**
     * 构造器
     *
     * @param listener
     * @param enableAsync
     * @since 1.0.0
     */
    public ApplicationEventListenerDomain(ApplicationEventListener listener, boolean enableAsync) {
        this.listener = listener;
        this.enableAsync = enableAsync;
    }

}
