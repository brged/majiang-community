package life.majiang.community.controller;

import life.majiang.community.dto.CommentCreateDTO;
import life.majiang.community.dto.CommentDTO;
import life.majiang.community.dto.ResultDTO;
import life.majiang.community.enums.CommentTypeEnum;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.model.Comment;
import life.majiang.community.model.User;
import life.majiang.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    @ResponseBody
    public Object post(@RequestBody CommentCreateDTO commentDTO,
                       HttpServletRequest request){
        //判断用户登录
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        if (commentDTO==null || StringUtils.isBlank(commentDTO.getContent())) {
            return  ResultDTO.errorOf(CustomizeErrorCode.COMMENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setParentId(commentDTO.getParentId());// 需要存在parent id，提示
        comment.setParentType(commentDTO.getParentType());
        comment.setContent(commentDTO.getContent());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());
        comment.setLikeCount(1L);
        comment.setCommenter(user.getId());// 需要校验登录
        System.out.println(comment);
        commentService.insert(comment, user);
        return ResultDTO.ok();
    }

    @RequestMapping(value = "/comment/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO comments(@PathVariable("id") Long id){
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }
}
