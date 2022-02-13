package life.majiang.community.advice;

import life.majiang.community.exception.CustomizeErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CustomizeExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handle(HttpServletRequest request, Throwable ex, Model model) {
        if(ex instanceof CustomizeErrorException){
            model.addAttribute("message", ex.getMessage());
        } else {
            model.addAttribute("message", "服务冒烟了，要不然稍后再试试？");
        }
        return new ModelAndView("error");
    }

    /*private HttpStatus getStatus(HttpServletRequest request) {
        Integer code = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus status = HttpStatus.resolve(code);
        return (status != null) ? status : HttpStatus.INTERNAL_SERVER_ERROR;
    }*/
}
