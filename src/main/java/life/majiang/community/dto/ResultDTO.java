package life.majiang.community.dto;

import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeErrorException;
import life.majiang.community.exception.ICustomizeErrorCode;
import lombok.Data;

@Data
public class ResultDTO <T> {
    private Integer code;
    private String message;
    private T data;

    public static ResultDTO valueOf(Integer code, String message){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(code);
        resultDTO.setMessage(message);
        return resultDTO;
    }
    public static ResultDTO errorOf(ICustomizeErrorCode customizeErrorCode){
        return ResultDTO.valueOf(customizeErrorCode.getCode(), customizeErrorCode.getMessage());
    }
    public static ResultDTO errorOf(CustomizeErrorException customizeErrorException){
        return ResultDTO.valueOf(customizeErrorException.getCode(), customizeErrorException.getMessage());
    }

    public static ResultDTO ok(){
        return ResultDTO.valueOf(200, "请求成功");
    }
    public static <T> ResultDTO okOf(T t){
        ResultDTO resultDTO = ResultDTO.valueOf(200, "请求成功");
        resultDTO.setData(t);
        return resultDTO;
    }
}
