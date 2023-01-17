package com.ccc.proj.common;

/**
 * 基于ThreadLocal封装工具类，用来保存和获取当前登录用户id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal();

    /**
     * 设置id值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取id值
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
