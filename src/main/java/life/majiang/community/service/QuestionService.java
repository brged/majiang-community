package life.majiang.community.service;

import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.dto.QuestionQueryDTO;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeErrorException;
import life.majiang.community.mapper.QuestionExtMapper;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.Question;
import life.majiang.community.model.QuestionExample;
import life.majiang.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    QuestionMapper questionMapper;
    @Autowired
    QuestionExtMapper questionExtMapper;

    public PaginationDTO list(String search, String tag, Integer page, Integer size) {
        if(StringUtils.isNotBlank(search)){
            String[] searchArr = StringUtils.split(search, " ");
            search = Arrays.stream(searchArr).collect(Collectors.joining("|"));
        }
        if(StringUtils.isNotBlank(tag)){
            String[] tagArr = StringUtils.split(tag, ",");
            tag = Arrays.stream(tagArr).collect(Collectors.joining("|"));
        }
        // 查询总页数
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        questionQueryDTO.setTag(tag);
        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);
        // 判断每页条数是否超出总记录数, 每页限额50条
        size = size > 50 ? 50 : size;
        size = totalCount>0 && size>totalCount ? totalCount: size;
        // 分页对象
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO();
        paginationDTO.setPagination(totalCount, page, size);
        // 分页边界软限制
        if(page > paginationDTO.getTotalPage())
            page=paginationDTO.getTotalPage();
        if(page < 1)
            page =1;
        Integer offset=size*(page-1);
        questionQueryDTO.setSize(size);
        questionQueryDTO.setOffset(offset);
        List<Question> questionList = questionExtMapper.selectBySearch(questionQueryDTO);
        List<QuestionDTO> questionDTOList=new ArrayList<>();
        for (Question question : questionList) {
            // 通过question的creator来获取user对象
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            // 加入根据creator获取到的user对象
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    public PaginationDTO list(Long id, Integer page, Integer size) {
        // 查询总页数 (指定用户id)
        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(id);
        Integer totalCount = (int) questionMapper.countByExample(example);
        // 判断每页条数是否超出总记录数, 每页限额50条
        size = size > 50 ? 50 : size;
        size = totalCount>0 && size>totalCount ? totalCount: size;
        // 分页对象
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO();
        paginationDTO.setPagination(totalCount, page, size);
        // 分页边界软限制，先判断totalCount可能会为0
        if(page > paginationDTO.getTotalPage())
            page=paginationDTO.getTotalPage();
        if(page < 1)
            page =1;
        Integer offset=size*(page-1);
        // 指定用户id
        QuestionExample example1 = new QuestionExample();
        example1.createCriteria()
                .andCreatorEqualTo(id);
        List<Question> questionList = questionMapper.selectByExampleWithBLOBsWithRowbounds(example1, new RowBounds(offset, size));
        List<QuestionDTO> questionDTOList=new ArrayList<>();
        for (Question question : questionList) {
            // 通过question的creator来获取user对象
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            // 加入根据creator获取到的user对象
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if(null == question){
            throw new CustomizeErrorException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId()==null){
            // add
            question.setGmtCreated(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreated());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insertSelective(question);
        } else {
            // update
            question.setGmtModified(System.currentTimeMillis());
            int updated = questionMapper.updateByPrimaryKeySelective(question);
            if(updated == 0){
                throw new CustomizeErrorException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO questionDTO) {
        if(questionDTO.getTag()==null || StringUtils.isBlank(questionDTO.getTag())){
            return new ArrayList<>();
        }
        Question question = new Question();
        question.setId(questionDTO.getId());
        String[] tagArr = StringUtils.split(questionDTO.getTag(), ",");
        String regexpTag = Arrays.stream(tagArr).collect(Collectors.joining("|"));
        question.setTag(regexpTag);
        List<Question> relatedQuestion = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = relatedQuestion.stream().map(q -> {
            QuestionDTO relatedQDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, relatedQDTO);
            return relatedQDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }
}
