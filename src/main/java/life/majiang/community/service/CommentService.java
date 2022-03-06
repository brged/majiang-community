package life.majiang.community.service;

import life.majiang.community.enums.CommentTypeEnum;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeErrorException;
import life.majiang.community.mapper.CommentMapper;
import life.majiang.community.mapper.QuestionExtMapper;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.model.Comment;
import life.majiang.community.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    QuestionMapper questionMapper;
    @Autowired
    QuestionExtMapper questionExtMapper;

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
}
