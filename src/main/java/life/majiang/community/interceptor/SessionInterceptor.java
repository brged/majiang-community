package life.majiang.community.interceptor;

import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.model.UserExample;
import life.majiang.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = (User) request.getSession().getAttribute("user");
        // 如果session中没有user对象，避免服务器重启后session中的user信息丢失
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
                    UserExample userExample = new UserExample();
                    userExample.createCriteria()
                            .andTokenEqualTo(token);
                    List<User> users = userMapper.selectByExample(userExample);
                    if (users.size() != 0) {
                        user = users.get(0);
                        request.getSession().setAttribute("user", user);
                        // System.out.println(users);
                    }
                }

            }
        }
        if(user!=null){
            Long unreadCount = notificationService.unreadCount(user.getId());
            request.getSession().setAttribute("unreadCount", unreadCount);
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
