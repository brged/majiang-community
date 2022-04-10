package life.majiang.community.dto;

import life.majiang.community.model.User;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Integer status;
    private Long notifier;
    private String notifierName;
    private Long outerId;
    private String outerTitle;
    private Integer type;
    private String typeName;
}
