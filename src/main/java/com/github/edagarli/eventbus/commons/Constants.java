package com.github.edagarli.eventbus.commons;

public class Constants {
    /**
     * 私有构造器
     */
    private Constants() {
        throw new IllegalAccessError();
    }


    /**
     * 日志信息
     */
    public static class Logger {
        public static final String EXCEPTION = "Exception:";
        public static final String MESSAGE = " ";

        private Logger() {
            throw new IllegalAccessError();
        }
    }

}
