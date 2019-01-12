package com.github.edagarli.eventbus.command;

import com.github.edagarli.eventbus.commons.Constants;
import com.lmax.disruptor.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author edagarli(卤肉)
 * Email: lizhi@edagarli.com
 * github: http://github.com/edagarli
 * Date: 2017/12/14
 * Time: 23:40
 * Desc:
 */
public class CommandEventExceptionHandler<E extends CommandEvent> implements ExceptionHandler<E> {

    private final String disruptor;

    private static Logger logger = LoggerFactory.getLogger(CommandEventExceptionHandler.class);

    /**
     * 构造器
     *
     * @param disruptor 分发器
     */
    public CommandEventExceptionHandler(String disruptor) {
        this.disruptor = disruptor;
    }

    /**
     * handle异常
     *
     * @param ex    异常
     * @param event 事件
     * @since 1.0.0
     */
    @Override
    public void handleEventException(Throwable ex, long sequence, E event) {
        logger.error(Constants.Logger.APP_EXCEPTION + "[{}] Event Exception:{},event:{}", disruptor, ex, event);
    }

    /**
     * start异常
     *
     * @param ex 异常
     * @since 1.0.0
     */
    @Override
    public void handleOnStartException(Throwable ex) {
        logger.error(Constants.Logger.APP_EXCEPTION + "[{}] on start Exception:{}", disruptor, ex);
    }

    /**
     * shutdown异常
     *
     * @param ex 异常
     * @since 1.0.0
     */
    @Override
    public void handleOnShutdownException(Throwable ex) {
        logger.error(Constants.Logger.APP_EXCEPTION + "[{}] on shutdown Exception :", disruptor, ex);
    }
}
