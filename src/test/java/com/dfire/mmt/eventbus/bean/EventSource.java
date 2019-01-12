package com.dfire.mmt.eventbus.bean;

import com.dfire.mmt.eventbus.event.ApplicationEvent;

/**
 * @author : lurou
 * Email: lurou@2dfire.com
 * github: http://github.com/edagarli
 * Date: 2019/1/11
 * Time: 11:26
 * Desc:
 */

public class EventSource extends ApplicationEvent {

    public EventSource(Object source) {
        super(source);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
