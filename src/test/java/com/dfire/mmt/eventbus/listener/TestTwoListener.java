package com.dfire.mmt.eventbus.listener;

import com.dfire.mmt.eventbus.Listener;
import com.dfire.mmt.eventbus.bean.EventSource;
import com.dfire.mmt.eventbus.event.ApplicationEventListener;
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
@Listener(priority = 1, tag = "123", enableAsync = true)
public class TestTwoListener implements ApplicationEventListener<EventSource> {

    @Override
    public void onApplicationEvent(EventSource event) {
        System.out.println("i am two====>>>" + event);
    }

}

