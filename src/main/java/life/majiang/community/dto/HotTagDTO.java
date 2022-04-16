package life.majiang.community.dto;

import lombok.Data;

@Data
public class HotTagDTO implements Comparable<HotTagDTO> {
    private String name;
    private Integer priority;

    @Override
    public int compareTo(HotTagDTO o) {
        return this.getPriority() - o.getPriority();
    }
}
