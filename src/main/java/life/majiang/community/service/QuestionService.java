package life.majiang.community.service;

import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.Question;
import life.majiang.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    QuestionMapper questionMapper;

    public PaginationDTO list(Integer page, Integer size) {
        // 查询总页数
        Integer totalCount = questionMapper.count();
        // 判断每页条数是否超出总记录数, 每页限额50条
        size = size > 50 ? 50 : size;
        size = size>totalCount ? totalCount: size;
        // 分页对象
        PaginationDTO paginationDTO = new PaginationDTO();
        paginationDTO.setPagination(totalCount, page, size);
        // 分页边界软限制
        if(page > paginationDTO.getTotalPage())
            page=paginationDTO.getTotalPage();
        if(page < 1)
            page =1;
        Integer offset=size*(page-1);
        List<Question> questionList = questionMapper.list(offset, size);
        List<QuestionDTO> questionDTOList=new ArrayList<>();
        for (Question question : questionList) {
            // 通过question的creator来获取user对象
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            // 加入根据creator获取到的user对象
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        return paginationDTO;
    }

    public PaginationDTO list(Integer id, Integer page, Integer size) {
        // 查询总页数 (指定用户id)
        Integer totalCount = questionMapper.countByUserId(id);
        // 判断每页条数是否超出总记录数, 每页限额50条
        size = size > 50 ? 50 : size;
        size = size>totalCount ? totalCount: size;
        // 分页对象
        PaginationDTO paginationDTO = new PaginationDTO();
        paginationDTO.setPagination(totalCount, page, size);
        // 分页边界软限制，先判断totalCount可能会为0
        if(page > paginationDTO.getTotalPage())
            page=paginationDTO.getTotalPage();
        if(page < 1)
            page =1;
        Integer offset=size*(page-1);
        // 指定用户id
        List<Question> questionList = questionMapper.listByUserId(id, offset, size);
        List<QuestionDTO> questionDTOList=new ArrayList<>();
        for (Question question : questionList) {
            // 通过question的creator来获取user对象
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            // 加入根据creator获取到的user对象
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        return paginationDTO;
    }
}
