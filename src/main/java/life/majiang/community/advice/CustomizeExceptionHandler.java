package life.majiang.community.advice;

import com.alibaba.fastjson.JSON;
import life.majiang.community.dto.ResultDTO;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@ControllerAdvice
public class CustomizeExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ModelAndView handle(HttpServletRequest request, Throwable ex, Model model, HttpServletResponse response) {

        ResultDTO resultDTO;
        if (ex instanceof CustomizeErrorException) {
            resultDTO =  ResultDTO.errorOf((CustomizeErrorException) ex);
        } else {
            resultDTO =  ResultDTO.errorOf(CustomizeErrorCode.SYS_ERROR);
        }

        if("application/json".equals(request.getContentType())) {
            // 返回json对象
            try {
                response.setContentType("application/json");
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            // 跳转错误页
            model.addAttribute("message", resultDTO.getMessage());
            return new ModelAndView("error");
        }
    }

    /*private HttpStatus getType(HttpServletRequest request) {
        Integer code = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus status = HttpStatus.resolve(code);
        return (status != null) ? status : HttpStatus.INTERNAL_SERVER_ERROR;
    }*/
}
