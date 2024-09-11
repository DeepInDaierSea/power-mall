package com.zkh.model;

import com.zkh.constant.BusinessEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("项目统一响应结果对象")
public class Result<T> implements Serializable {
    @ApiModelProperty("状态码")
    private Integer code = 200;
    @ApiModelProperty("消息")
    private String msg = "success";
    @ApiModelProperty("数据")
    private T data = null;


    /**
     * 操作成功的方法
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(T data){
        Result result = new Result<>();
        result.setCode(200);
        result.setMsg("success");
        result.setData(data);
        return result;
    }
//    public static <T> Result<T> success(BusinessEnum businessEnum, T data){
//        Result result = new Result<>();
//        result.setCode(businessEnum.getCode());
//        result.setMsg(businessEnum.getMsg());
//        result.setData(data);
//        return result;
//    }

    public static <V> Result<V> fail(V data){
        Result result = new Result();
        result.setCode(500);
        result.setMsg("fail");
        result.setData(data);
        return result;
    }
    public static <V> Result<V> fail(BusinessEnum businessEnum){
        Result result = new Result();
        result.setCode(businessEnum.getCode());
        result.setMsg(businessEnum.getMsg());
        result.setData(null);
        return result;
    }
    public static <V> Result<V> fail(BusinessEnum businessEnum, V data){
        Result result = new Result();
        result.setCode(businessEnum.getCode());
        result.setMsg(businessEnum.getMsg());
        result.setData(data);
        return result;
    }
}
