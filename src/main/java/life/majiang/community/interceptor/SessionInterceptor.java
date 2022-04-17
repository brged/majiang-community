package life.majiang.community.interceptor;

import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.Ad;
import life.majiang.community.model.User;
import life.majiang.community.model.UserExample;
import life.majiang.community.service.AdService;
import life.majiang.community.service.NotificationService;
import life.majiang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private AdService adService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession httpSession = request.getSession();
        // 添加广告列表
        if(httpSession.getAttribute("ads") == null)
            httpSession.setAttribute("adPosMap", adService.posMap());

        // 如果session中没有user对象，避免服务器重启后session中的user信息丢失
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                // cookies为null说明是第一次访问本系统

                String token = null;
                for (Cookie cookie : cookies) {
                    // 判断cookie中是否有token，如果有则说明已经登录了
                    if (cookie.getName().equals("token")) {
                        token = cookie.getValue();
                        break;
                    }
                }
                if (token != null) {
                    // 重新从数据库中将用户信息查找出来
                    List<User> dbUsers = userService.getByToken(token);
                    if (dbUsers.size() > 0) {
                        user = dbUsers.get(0);
                        httpSession.setAttribute("user", user);
                    }
                }

            }
        }
        if(user!=null){
            Long unreadCount = notificationService.unreadCount(user.getId());
            httpSession.setAttribute("unreadCount", unreadCount);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

    }
}
