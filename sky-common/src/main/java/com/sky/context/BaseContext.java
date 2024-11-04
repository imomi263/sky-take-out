package com.sky.context;

public class BaseContext {

    // 提供了线程局部变量
    public static ThreadLocal<Integer> threadLocal=new ThreadLocal<>();

    public static void setCurrentId(Integer id) {
        threadLocal.set(id);
    }

    public static Integer getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }
}
