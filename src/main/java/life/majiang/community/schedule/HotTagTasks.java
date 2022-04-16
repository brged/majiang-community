package life.majiang.community.schedule;

import life.majiang.community.cache.HotTagCache;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.model.Question;
import life.majiang.community.model.QuestionExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class HotTagTasks {

    @Autowired
    private QuestionMapper questionMapper;

//    @Scheduled(fixedRate = 5000)
//    @Scheduled(cron = "0 0 */3 * * * ")
    @Scheduled(fixedRate = 1000*60*60*3)
    public void hotTagSchedule(){
        log.info("The time is now {}", new Date());
        int offset = 0;
        int limit = 20;
        List<Question> questionList = new ArrayList<>();
        Map<String, Integer> tagMap = new HashMap<>();
        while (offset==0 || questionList.size()==limit){
            QuestionExample questionExample = new QuestionExample();
            questionExample.setOrderByClause("gmt_created desc");
            questionList = questionMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, limit));
            for (Question question : questionList){
                String[] tags = StringUtils.split(question.getTag(), ",");
                for (String tag : tags){
                    Integer tagPriority = tagMap.getOrDefault(tag, 0);
                    tagMap.put(tag, tagPriority + 5 + question.getCommentCount());
                }
            }
            offset += limit;
        }
        // 更新tag排名
        HotTagCache.updateHotTagList(tagMap);
    }
}
