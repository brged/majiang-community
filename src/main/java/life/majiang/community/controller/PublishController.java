package life.majiang.community.controller;

import life.majiang.community.cache.TagCache;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.dto.TagDTO;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.model.Question;
import life.majiang.community.model.User;
import life.majiang.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    QuestionService questionService;

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        QuestionDTO question = questionService.getById(id);
        // 页面数据回显
        model.addAttribute("title", question.getTitle());
        model.addAttribute("description", question.getDescription());
        model.addAttribute("tag", question.getTag());
        model.addAttribute("tags", TagCache.get());
        // 标识 id 为更新而不是新增
        model.addAttribute("id", question.getId());
        return "publish";
    }

    @GetMapping("/publish")
    public String publish(Model model){
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(@RequestParam("title") String title,
                            @RequestParam("description") String description,
                            @RequestParam("tag") String tag,
                            @RequestParam(value="id", required = false) Long id,
                            HttpServletRequest servletRequest,
                            Model model){
        // 页面数据回显
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
        model.addAttribute("tags", TagCache.get());
        // 用户未登录
        User user = (User) servletRequest.getSession().getAttribute("user");
        if(user == null){
            model.addAttribute("error", "用户未登录");
            return "publish";
        }

        // 数据校验
        if(title==null || title.trim().equals("")){
            model.addAttribute("error", "标题不能为空");
            return "publish";
        }
        if(description==null || description.trim().equals("")){
            model.addAttribute("error", "问题补充不能为空");
            return "publish";
        }
        if(tag==null || tag.equals("")){
            model.addAttribute("error", "标签不能为空");
            return "publish";
        }

        String invalidTag = TagCache.filterInvalid(tag);
        if(StringUtils.isNotBlank(invalidTag)){
            model.addAttribute("error", "输入非法标签："+invalidTag);
            return "publish";
        }

        //加入 creator 校验，当前登录用户仅可修改自己发布的问题
        if(id !=null){
            QuestionDTO questionDTO = questionService.getById(id);
            // 修改的帖子用户 与 当前用户不符
            if(questionDTO.getCreator() != user.getId()){
                model.addAttribute("error", "非法用户操作");
                return "publish";
            }
        }

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setGmtCreated(System.currentTimeMillis());
        question.setGmtModified(question.getGmtCreated());
        question.setCreator(user.getId());
        question.setId(id);
        // id为null则新增，不为null则修改
        questionService.createOrUpdate(question);
        return "redirect:/";
    }
}
