package io.github.open.toolkit.commons.model;

import lombok.AllArgsConstructor;

/**
 * 返回通用结果类
 *
 * @author TZJ
 */
@AllArgsConstructor
public class Result<T> {

    public static final Integer OK = 200;
    public static final Integer ERROR = 500;

    public Integer code;
    public String errorMessage;
    public T data;

    /**
     * 返回通用成功结果
     */
    public static <T> Result<T> success(T data) {

        return new Result<T>(OK, null, data);
    }

    /**
     * 返回通用失败结果
     */
    public static <T> Result<T> error(String errorMessage) {

        return new Result<T>(ERROR, errorMessage, null);
    }

    /**
     * 返回通用带返回值失败结果
     */
    public static <T> Result<T> error(String errorMessage, T data) {
        return new Result<>(ERROR, errorMessage, data);
    }

    public static <T> Result<T> success() {
        return new Result<>(OK, null, null);
    }
}
