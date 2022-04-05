package life.majiang.community.cache;

import life.majiang.community.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagCache {
    public static List<TagDTO> get(){
        List<TagDTO> tagDTOS = new ArrayList<>();

        TagDTO program = new TagDTO();
        program.setCategoryName("开发语言");
        program.setTags(Arrays.asList("Java", "C", "JS"));
        tagDTOS.add(program);

        TagDTO framework = new TagDTO();
        framework.setCategoryName("平台框架");
        framework.setTags(Arrays.asList("Spring", "Mybatis"));
        tagDTOS.add(framework);

        TagDTO server = new TagDTO();
        server.setCategoryName("服务器");
        server.setTags(Arrays.asList("Linux", "Docker", "Tomcat"));
        tagDTOS.add(server);

        TagDTO database = new TagDTO();
        database.setCategoryName("数据库");
        database.setTags(Arrays.asList("MySQL", "Oracle", "sqlite", "h2"));
        tagDTOS.add(database);

        TagDTO devTools = new TagDTO();
        devTools.setCategoryName("开发工具");
        devTools.setTags(Arrays.asList("IDE", "git", "svn", "maven"));
        tagDTOS.add(devTools);

        return tagDTOS;
    }

    public static String filterInvalid(String tags){
        String[] splitTags = StringUtils.split(tags, ",");
        // 所有tag
        List<TagDTO> tagDTOS = get();
        List<String> allTags = tagDTOS.stream().flatMap(tag -> tag.getTags().stream()).collect(Collectors.toList());
        // 不存在的tag
        String invalidTagStr = Arrays.stream(splitTags).filter(t -> !allTags.contains(t)).collect(Collectors.joining(","));
        return invalidTagStr;
    }
}
