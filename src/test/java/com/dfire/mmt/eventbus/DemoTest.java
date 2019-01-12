package com.dfire.mmt.eventbus;

import com.dfire.mmt.eventbus.bean.EventSource;
import org.awaitility.Awaitility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author : lurou
 * Email: lurou@2dfire.com
 * github: http://github.com/edagarli
 * Date: 2019/1/11
 * Time: 10:23
 * Desc: 时间demo测试
 */

@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class DemoTest {

    @Resource
    EventBus eventBus;

    /** 扫描监听包位置(默认全局包括jar包) */
    private final static String SCAN_PACKAGE = "com.dfire.mmt.eventbus.listener";

    /** 环队列大小 */
    private final static int CON_BUFFER_SIZE = 2048;

    /** 异步线程数 */
    private final static int ASYNC_THREAD_NUM = 32;


    @Test
    public void test() {
        // 全局开启异步
        eventBus.async(CON_BUFFER_SIZE, ASYNC_THREAD_NUM);
        // 设置扫描jar包，默认不扫描
        eventBus.scanJar();
        // 设置默认扫描的包名，默认全扫描
        eventBus.scanPackage(SCAN_PACKAGE);
        eventBus.start();

        eventBus.publish("123", new EventSource("test"));

        eventBus.publish("123", new EventSource("test111111"));

        Awaitility.await().atMost(2, TimeUnit.MINUTES).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return eventBus.stop();
            }
        });

    }

}
