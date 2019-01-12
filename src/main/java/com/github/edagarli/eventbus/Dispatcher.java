package com.github.edagarli.eventbus;

import com.github.edagarli.eventbus.command.CommandEvent;
import com.github.edagarli.eventbus.command.CommandEventExceptionHandler;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author edagarli(卤肉)
 *         Email: lizhi@edagarli.com
 *         github: http://github.com/edagarli
 *         Date: 2017/12/14
 *         Time: 23:40
 *         Desc: 异步事件处理分发器
 */
public class Dispatcher {

    private final EventBus eventBus;

    private static ExecutorService pool = null;
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

    private volatile boolean running;


    protected Dispatcher(EventBus eventBus) {
        this.eventBus = eventBus;
        this.running = false;
    }

    /**
     * 分发器start
     *
     * @return Dispatcher
     * @since 1.0.0
     */
    public synchronized Dispatcher start() {
        if (!this.running) {
            this.running = true;
            int conBufferSize = eventBus.getConBufferSize();
            int asyncThreads = eventBus.getAsyncThreads();
            final Channel channel = eventBus.getChannel();
            conBufferSize = conBufferSize > 0 ? conBufferSize : 1024;
            asyncThreads = asyncThreads > 0 ? asyncThreads : 8;
            pool = Executors.newFixedThreadPool(asyncThreads, new ThreadFactory() {
                final AtomicInteger seq = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "EventBus-concurrency-Consumer-" + seq.getAndIncrement());
                }
            });
            conDisruptor = new Disruptor<>(
                    new EventFactory<CommandEvent>() {
                        @Override
                        public CommandEvent newInstance() {
                            return new CommandEvent();
                        }
                    }, conBufferSize, pool, ProducerType.MULTI, new LiteBlockingWaitStrategy()
            );
            WorkHandler[] handlers = new WorkHandler[asyncThreads];
            Arrays.fill(handlers, new WorkHandler<CommandEvent>() {

                @Override
                public void onEvent(CommandEvent commandEvent) throws Exception {
                    channel.handle(commandEvent.getApplicationEventListenerHelper(), commandEvent.getApplicationEvent());
                }
            });
            conDisruptor.handleEventsWithWorkerPool(handlers);
            conDisruptor.handleExceptionsWith(new CommandEventExceptionHandler<>("commandBus-Concurrency-Disruptor"));
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
