package com.health.management.common;

import lombok.Data;

/**
 * 统一返回结果类
 */
@Data
public class Result {
    
    private Integer code;
    private String message;
    private Object data;
    
    public Result() {}
    
    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public Result(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    /**
     * 成功返回
     */
    public static Result success() {
        return new Result(200, "操作成功");
    }
    
    /**
     * 成功返回(带消息)
     */
    public static Result success(String message) {
        return new Result(200, message);
    }
    
    /**
     * 成功返回(带数据)
     */
    public static Result success(Object data) {
        return new Result(200, "操作成功", data);
    }
    
    /**
     * 成功返回(带消息和数据)
     */
    public static Result success(String message, Object data) {
        return new Result(200, message, data);
    }
    
    /**
     * 失败返回
     */
    public static Result error() {
        return new Result(500, "操作失败");
    }
    
    /**
     * 失败返回(带消息)
     */
    public static Result error(String message) {
        return new Result(500, message);
    }
    
    /**
     * 失败返回(自定义状态码)
     */
    public static Result error(Integer code, String message) {
        return new Result(code, message);
    }
    
    /**
     * 未授权
     */
    public static Result unauthorized() {
        return new Result(401, "未授权,请先登录");
    }
    
    /**
     * 禁止访问
     */
    public static Result forbidden() {
        return new Result(403, "禁止访问");
    }
}
