package life.majiang.community.controller;

import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    UserMapper userMapper;

    @GetMapping("/")
    public String hello(Model model, HttpServletRequest request){
        // 如果session中没有user对象，避免服务器重启后session中的user信息丢失
        if (request.getSession().getAttribute("user")==null) {
            Cookie[] cookies = request.getCookies();
            String token=null;
            for (Cookie cookie : cookies) {
                // 判断cookie中是否有token，如果有则说明已经登录了
                if (cookie.getName().equals("token")){
                    token=cookie.getValue();
                }
            }
            if (token != null) {
                // 重新从数据库中将用户信息查找出来
                User user= userMapper.getByToken(token);
                System.out.println(user);
                request.getSession().setAttribute("user", user);
            }

        }
        return "index";
    }
}
