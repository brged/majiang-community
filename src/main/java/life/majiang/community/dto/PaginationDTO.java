package life.majiang.community.dto;

import life.majiang.community.model.Question;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO {
    // 数据列表
    private List<QuestionDTO> questions;
    // 是否显示上一页按钮
    private Boolean showPrevious;
    // 是否显示跳转首页按钮
    private Boolean showFirst;
    private Boolean showNext;
    private Boolean showEnd;
    // 当前页码
    private Integer page;
    // 要显示在页面上的页码
    private List<Integer> pages=new ArrayList<>();
    // 总页数
    private Integer totalPage;

    public void setPagination(Integer totalCount, Integer page, Integer size) {
        // 计算总页数
        // 判断每页条数是否超出总记录数, 每页限额50条
        size = size > 50 ? 50 : size;
        size = totalCount>0 && size>totalCount ? totalCount: size;

        Integer totalPage=totalCount/size;
        if(totalCount%size!=0){
            totalPage+=1;
        }
        this.totalPage=totalPage;

        // 页面边界软限制
//        if(page > totalPage)
//            page=totalPage;
//        if(page < 1)
//            page =1;

        // 保存当前页码
        this.page=page;
        // 计算要显示的页码  显示当前页面前后各3页按钮
        for(int i=-3; i<=3; i++){
            Integer tempPage=page+i;
            if(tempPage>0 && tempPage<=totalPage){
                pages.add(tempPage);
            }
        }
        // 当为第一页时不展示上一页按钮
        if(page<=1)
            showPrevious=false;
        else
            showPrevious=true;
        // 当为最后一页时不展示下一页按钮
        if(page>=totalPage)
            showNext=false;
        else
            showNext=true;
        // 但页码列表中有第一页时不展示跳转首页按钮
        showFirst = pages.contains(1) ? false : true;
        // 但页码列表中有最后一页时不展示跳转尾页按钮
        showEnd = pages.contains(totalPage) ? false : true;

    }
}
