package com.sky.context;

public class BaseContext {

    // 提供了线程局部变量
    public static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }
}
