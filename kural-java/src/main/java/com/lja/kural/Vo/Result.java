package com.lja.kural.Vo;

public class Result<T> {
    private String code;    //状态码
    private String msg;     //状态信息
    private T data;         //具体数据
    public Result()
    {

    }

    public Result(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    public static Result success()
    {
        Result result = new Result<>();
        result.setCode("200");
        result.setMsg("success");
        return result;
    }
    public static <T> Result<T> success(String msg){
        Result<T> result = new Result<>();
        result.setCode("200");
        result.setMsg(msg);
        return result;
    }
    public static <T> Result<T> success(T data){
        Result<T> result = new Result<>(data);
        result.setCode("200");
        result.setMsg("success");
        return result;
    }
    public static <T> Result<T> success(String msg,T data){
        Result<T> result = new Result<>(data);
        result.setCode("200");
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
    public static Result error(String code, String msg)
    {
        Result result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
    public static <T> Result<T> error(String code, String msg, T data){
        Result<T> result = new Result<>(data);
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}