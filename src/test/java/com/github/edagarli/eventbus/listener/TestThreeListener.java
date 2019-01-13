package com.github.edagarli.eventbus.listener;

import com.github.edagarli.eventbus.bean.EventSource;
import com.github.edagarli.eventbus.event.ApplicationEventListener;
import org.springframework.stereotype.Component;

/**
 * @author : lurou
 * Email: lurou@2dfire.com
 * github: http://github.com/edagarli
 * Date: 2019/1/11
 * Time: 11:53
 * Desc:
 */
@Component
@Listener(priority = 1, tag = "test", enableAsync = true)
public class TestThreeListener implements ApplicationEventListener<EventSource> {

    @Override
    public void onApplicationEvent(EventSource event) {
        System.out.println("i am three====>>>" + event);
    }

}

