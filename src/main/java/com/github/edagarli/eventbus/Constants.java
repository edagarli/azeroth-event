package com.github.edagarli.eventbus;

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
        public static final String APP_EXCEPTION = "APP> Exception:";
        public static final String APP_MESSAGE = "APP> ";

        private Logger() {
            throw new IllegalAccessError();
        }
    }

}
