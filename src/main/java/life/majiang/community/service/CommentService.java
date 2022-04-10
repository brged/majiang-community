package life.majiang.community.service;

import life.majiang.community.dto.CommentDTO;
import life.majiang.community.enums.CommentTypeEnum;
import life.majiang.community.enums.NotificationStatusEnum;
import life.majiang.community.enums.NotificationTypeEnum;
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

    @Autowired
    NotificationMapper notificationMapper;

    @Transactional
    public void insert(Comment comment, User commenter) {
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
            // 回复问题
            Question parentQuestion = questionMapper.selectByPrimaryKey(parentComment.getParentId());
            if (parentQuestion == null) {
                // 原问题不存在
                throw new CustomizeErrorException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            comment.setCommentCount(0);
            commentMapper.insert(comment);
            // 原评论增加评论数
            parentComment.setCommentCount(1);
            commentExtMapper.incCommentCount(parentComment);
            if(comment.getCommenter() != parentComment.getCommenter())
                // 创建回复通知 自己不给自己通知
                createNotify(comment, parentComment.getCommenter(), commenter.getName(), parentComment.getContent(), NotificationTypeEnum.REPLY_COMMENT, parentQuestion.getId());

        } else {
            // 回复问题
            Question parentQuestion = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (parentQuestion == null) {
                // 原问题不存在
                throw new CustomizeErrorException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            comment.setCommentCount(0);
            commentMapper.insert(comment);
            // 原问题增加评论数
            parentQuestion.setCommentCount(1);
            questionExtMapper.incComment(parentQuestion);
            if(comment.getCommenter() != parentQuestion.getCreator())
                // 创建回复通知 自己不给自己通知
                createNotify(comment, parentQuestion.getCreator(), commenter.getName(), parentQuestion.getTitle(), NotificationTypeEnum.REPLY_QUESTION, parentQuestion.getId());
        }
    }


    private void createNotify(Comment comment, Long receiver, String notifierName, String outerTitle, NotificationTypeEnum notificationType, Long outerId) {
        // 回复通知
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        // 回复的类型，对原问题评论 或 原评论回复
        notification.setType(notificationType.getType());
        // 回复或评论所在的 问题(question) id
        notification.setOuterId(outerId);
        // 原回复或原评论的用户id，即接收通知的人
        notification.setNotifier(comment.getCommenter());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        // 创建该条评论的人 用户名 （用于缓存）
        notification.setNotifierName(notifierName);
        // 回复所在问题的标题 （用于缓存）
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
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
