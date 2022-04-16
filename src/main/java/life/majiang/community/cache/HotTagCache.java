package life.majiang.community.cache;

import life.majiang.community.dto.HotTagDTO;

import java.util.*;

public class HotTagCache {
    private HotTagCache(){}
    private static List<String> hotTagList = new ArrayList<>();
    public static List<String> getHotTagList() {
        return hotTagList;
    }

    public static void updateHotTagList(Map<String, Integer> tagMap){
        int max = 10;
        PriorityQueue<HotTagDTO> priorityQueue = new PriorityQueue<>(max);
        tagMap.forEach((k, v) ->{
            HotTagDTO hotTagDTO = new HotTagDTO();
            hotTagDTO.setName(k);
            hotTagDTO.setPriority(v);
            if(priorityQueue.size() < max){
                // queue.size < 3，直接放入
                priorityQueue.offer(hotTagDTO);
            } else {
                HotTagDTO minHotTagDTO = priorityQueue.peek();
                if(hotTagDTO.compareTo(minHotTagDTO) > 0) {
                    // 有更高级别的tag，替换最小级别的tag
                    priorityQueue.poll();
                    priorityQueue.offer(hotTagDTO);
                }
            }
        });
        hotTagList.clear();
        while (!priorityQueue.isEmpty()){
            hotTagList.add(0, priorityQueue.poll().getName());
        }
    }
}
