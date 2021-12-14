package life.majiang.community.provider;

import com.alibaba.fastjson.JSON;
import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {

    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try {
            try (Response response = client.newCall(request).execute()) {
                String responseString = response.body().string();
                System.out.println(responseString);
                String token = responseString.split("&")[0].split("=")[1];
                return token;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public GithubUser getUserInfo(String accessToken){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.github.com/user")
                .addHeader("Authorization", "token "+ accessToken)
                .build();

        try {
            try (Response response = client.newCall(request).execute()) {
                String responseString = response.body().string();
                GithubUser githubUser = JSON.parseObject(responseString, GithubUser.class);
                return githubUser;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
