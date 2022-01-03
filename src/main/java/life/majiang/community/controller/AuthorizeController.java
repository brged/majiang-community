package life.majiang.community.controller;

import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GithubUser;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.oauth.client.id}")
    private String clientId;
    @Value("${github.oauth.client.secret}")
    private String clientSecret;
    @Value("${github.oauth.redirect.uri}")
    private String redirectUri;

    @Autowired private UserMapper userMapper;

    // github oauth 回调url
    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam("state") String state,
                           HttpServletResponse servletResponse){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        // 根据github回调返回的临时code, 获取access token
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        // 利用access token获取user信息
        GithubUser userInfo = githubProvider.getUserInfo(accessToken);
        if(userInfo != null && userInfo.getLogin() != null) {
            System.out.println(userInfo);
            // 如果数据库中有用户的accoutId存在，说明之前有登录成功，则将用户信息取出
            User user = userMapper.getByAccountId(String.valueOf(userInfo.getId()));
            if(user == null) {
                //登录成功，获取登录信息，插入数据库中 (首次登录)
                user = new User();
                user.setName(userInfo.getLogin());
                user.setAccountId(String.valueOf(userInfo.getId()));
                user.setToken(UUID.randomUUID().toString());
                user.setGmtCreated(System.currentTimeMillis());
                user.setGmtModified(user.getGmtCreated());
                // 头像 url
                user.setAvatarUrl(userInfo.getAvatar_url());
                userMapper.insert(user);
            }
            // user将之后由token来获取，此处并不需要使用session设置user
//            servletRequest.getSession().setAttribute("user", user);
            // 同时将token保存到cookie用于检测user登录状态
            servletResponse.addCookie(new Cookie("token", user.getToken()));
            return "redirect:/";
        } else {
            return "redirect:/";
        }
    }
}
