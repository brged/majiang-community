package life.majiang.community.service;

import life.majiang.community.dto.CommentDTO;
import life.majiang.community.enums.CommentTypeEnum;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeErrorException;
import life.majiang.community.mapper.*;
import life.majiang.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    QuestionMapper questionMapper;
    @Autowired
    QuestionExtMapper questionExtMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    CommentExtMapper commentExtMapper;

    @Transactional
    public void insert(Comment comment) {
        if(comment.getParentId()==null || comment.getParentId()==0)
            // parent id 不存在
            throw new CustomizeErrorException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);

        CommentTypeEnum commentTypeEnum = CommentTypeEnum.lookUp(comment.getParentType());
        if(comment.getParentType()==null || commentTypeEnum ==null)
            // parent type 不存在
            throw new CustomizeErrorException(CustomizeErrorCode.TYPE_PARAM_NOT_FOUND);

        if(commentTypeEnum == CommentTypeEnum.COMMENT){
            // 回复评论
            Comment parentComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (parentComment == null) {
                // 原评论不存在
                throw new CustomizeErrorException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);
            // 原评论增加评论数
            parentComment.setCommentCount(1);
            commentExtMapper.incCommentCount(parentComment);
        } else {
            // 回复问题
            Question parentQuestion = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (parentQuestion == null) {
                // 原问题不存在
                throw new CustomizeErrorException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            // 原问题增加评论数
            parentQuestion.setCommentCount(1);
            questionExtMapper.incComment(parentQuestion);
        }
    }

    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        CommentExample commentExample = new CommentExample();
        // 查询问题的评论
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andParentTypeEqualTo(type.type);
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if(comments.size()==0) return new ArrayList<>();
        // 评论对应的用户id
        List<Long> commenters = comments.stream().map(comment -> comment.getCommenter()).distinct().collect(Collectors.toList());
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(commenters);
        // 获取评论用户
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        List<CommentDTO> commentDTOs = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            // commentDTO添加用户信息
            commentDTO.setUser(userMap.get(comment.getCommenter()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOs;
    }
}
