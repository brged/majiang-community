package life.majiang.community.controller;

import life.majiang.community.dto.NotificationDTO;
import life.majiang.community.enums.NotificationTypeEnum;
import life.majiang.community.model.User;
import life.majiang.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @RequestMapping("notification/{id}")
    public String notification(@PathVariable("id") Long id, HttpServletRequest request){
        // 判断用户是否是登录状态
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        NotificationDTO notificationDTO =  notificationService.read(id, user);
        if(NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType()
                || NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()){
            return "redirect:/question/"+notificationDTO.getOuterId();
        } else {
            return "redirect:/";
        }
    }
}
