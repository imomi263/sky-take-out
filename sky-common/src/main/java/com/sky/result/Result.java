package com.sky.result;

import lombok.Data;

import java.io.Serializable;

// 后端统一返回结果
// @param <T>
@Data
public class Result<T> implements Serializable {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.setCode(0);
        result.msg = null;
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.data=data;
        result.code=0;
        return result;
    }
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<T>();
        result.msg = msg;
        result.code=1;
        return result;
    }
}
