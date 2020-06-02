package com.github.edagarli.eventbus;

import com.github.edagarli.eventbus.channel.Channel;
import com.github.edagarli.eventbus.command.CommandEvent;
import com.github.edagarli.eventbus.command.CommandEventExceptionHandler;
import com.github.edagarli.eventbus.thread.EventThreadFactory;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author edagarli(卤肉)
 * Email: lizhi@edagarli.com
 * github: http://github.com/edagarli
 * Date: 2017/12/14
 * Time: 23:40
 * Desc: 异步事件处理分发器
 */
public class Dispatcher {

    private final EventBus eventBus;

    /**
     * 线程池维护线程所允许的空闲时间
     */
    private final static int TIME_KEEP_ALIVE = 0;

    /**
     * 并发消费的Disruptor Big Ring
     * <p>
     * 事件被消费的顺序是不确定的
     */
    private static Disruptor<CommandEvent> conDisruptor;

    /**
     * 并发消费 RingBuffer
     */
    private RingBuffer<CommandEvent> conRingBuffer;

    private final static int DEFAULT_ZERO_VALUE = 0;

    private final static int DEFAULT_CON_BUFFER_SIZE = 1024;

    private final static int DEFAULT_ASYNC_THREADS = 8;

    private volatile boolean running;

    protected Dispatcher(EventBus eventBus) {
        this.eventBus = eventBus;
        this.running = false;
    }

    /**
     * 分发器start
     * <pre>
     *   构建事件异步处理队列disruptor，初始化并发消费线程
     * </pre>
     * @return Dispatcher
     * @since 1.0.0
     */
    public synchronized Dispatcher start() {
        if (!this.running) {
            this.running = true;
            int conBufferSize = eventBus.getConBufferSize();
            int asyncThreads = eventBus.getAsyncThreads();
            final Channel channel = eventBus.getChannel();
            //DEFAULT_CON_BUFFER_SIZE 默认size 1024
            conBufferSize = conBufferSize > DEFAULT_ZERO_VALUE ? conBufferSize : DEFAULT_CON_BUFFER_SIZE;
            //DEFAULT async threads 默认 8
            asyncThreads = asyncThreads > DEFAULT_ZERO_VALUE ? asyncThreads : DEFAULT_ASYNC_THREADS;
            //队列的并发消费线程
            ThreadPoolExecutor treadPool = new ThreadPoolExecutor(asyncThreads, asyncThreads, TIME_KEEP_ALIVE,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(asyncThreads),
                new EventThreadFactory("EventBus-concurrency-ConsumerPool-"), new DiscardPolicy());

            //构建环状队列
            conDisruptor = new Disruptor<>(
                () -> new CommandEvent(), conBufferSize, treadPool, ProducerType.MULTI, new LiteBlockingWaitStrategy()
            );

            WorkHandler[] handlers = new WorkHandler[asyncThreads];
            Arrays.fill(handlers, (WorkHandler<CommandEvent>)commandEvent ->
                channel.handle(commandEvent.getEventListenerDomain(), commandEvent.getApplicationEvent()));

            conDisruptor.handleEventsWithWorkerPool(handlers);
            conDisruptor.handleExceptionsWith(new CommandEventExceptionHandler<>(
                "commandBus-Concurrency-Disruptor"));
            conRingBuffer = conDisruptor.start();
            return this;
        }
        return this;
    }

    /**
     * 分发器stop
     *
     * @return Dispatcher
     * @since 1.0.0
     */
    public synchronized void stop() {
        this.running = false;
        if (null != conDisruptor) {
            conDisruptor.shutdown();
            conDisruptor = null;
        }
    }

    public RingBuffer<CommandEvent> getConRingBuffer() {
        return conRingBuffer;
    }

}
