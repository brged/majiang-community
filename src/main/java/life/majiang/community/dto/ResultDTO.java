package life.majiang.community.dto;

import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeErrorException;
import life.majiang.community.exception.ICustomizeErrorCode;
import lombok.Data;

@Data
public class ResultDTO {
    private Integer code;
    private String message;

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
}
