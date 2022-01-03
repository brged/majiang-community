package life.majiang.community.controller;

import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.model.User;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private QuestionService questionService;

    @RequestMapping("/profile/{action}")
    public String getProfile(@PathVariable("action")String action,
                             Model model,
                             HttpServletRequest request,
                             @RequestParam(name = "page", defaultValue = "1") Integer page,
                             @RequestParam(name = "size", defaultValue = "3") Integer size){
        // 判断用户是否是登录状态
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        // 判断点击的页面
        if("questions".equals(action)){
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
            // 查询列表信息 (指定用户id)
            PaginationDTO pagination = questionService.list(user.getId(), page, size);
            model.addAttribute("pagination", pagination);
        }
        else if("replies".equals(action)){
            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "最新回复");
        }

        return "profile";
    }
}
