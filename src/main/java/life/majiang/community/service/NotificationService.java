package life.majiang.community.service;

import life.majiang.community.dto.NotificationDTO;
import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.enums.NotificationStatusEnum;
import life.majiang.community.enums.NotificationTypeEnum;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeErrorException;
import life.majiang.community.mapper.NotificationMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Long id, Integer page, Integer size) {
        // 查询总页数 (指定用户id)
        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andReceiverEqualTo(id);
        Integer totalCount = (int) notificationMapper.countByExample(example);
        // 判断每页条数是否超出总记录数, 每页限额50条
        size = size > 50 ? 50 : size;
        size = totalCount>0 && size>totalCount ? totalCount: size;
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
        NotificationExample example1 = new NotificationExample();
        example1.createCriteria()
                .andReceiverEqualTo(id);
        example1.setOrderByClause("GMT_CREATE desc");
        List<Notification> notificationList = notificationMapper.selectByExampleWithRowbounds(example1, new RowBounds(offset, size));
        List<NotificationDTO> notificationDTOS=new ArrayList<>();
        if (notificationList.size()>0){
            for (Notification notification : notificationList) {
                NotificationDTO notificationDTO = new NotificationDTO();
                BeanUtils.copyProperties(notification, notificationDTO);
                notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
                notificationDTOS.add(notificationDTO);
            }
        }
        paginationDTO.setData(notificationDTOS);
        return paginationDTO;
    }

    public Long unreadCount(Long id) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(id)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(notificationExample);
    }

    @Transactional
    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if(notification == null){
            throw new CustomizeErrorException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if(!Objects.equals(notification.getReceiver(), user.getId())){
            // 通知接受者与用户不相等
            throw new CustomizeErrorException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }
        // 将通知更新为已读
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKeySelective(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
