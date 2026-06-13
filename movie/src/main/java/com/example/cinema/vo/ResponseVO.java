package com.example.cinema.vo;

/**
 * 统一响应对象
 * 
 * ✅ 修复：添加泛型支持，提高类型安全
 * 
 * @author fjj
 * @date 2019/3/12 5:14 PM
 */
public class ResponseVO<T> {

    /**
     * 调用是否成功
     */
    private Boolean success;

    /**
     * 返回的提示信息
     */
    private String message;

    /**
     * 内容 ✅ 使用泛型
     */
    private T content;

    public static <T> ResponseVO<T> buildSuccess() {
        ResponseVO<T> response = new ResponseVO<>();
        response.setSuccess(true);
        return response;
    }

    public static <T> ResponseVO<T> buildSuccess(T content) {
        ResponseVO<T> response = new ResponseVO<>();
        response.setContent(content);
        response.setSuccess(true);
        return response;
    }

    public static <T> ResponseVO<T> buildFailure(String message) {
        ResponseVO<T> response = new ResponseVO<>();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
