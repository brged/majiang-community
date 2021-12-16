package life.majiang.community.mapper;

import life.majiang.community.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Insert("insert into user (name, account_id, token, gmt_created, gmt_modified) values (#{name}, #{accountId}, #{token}, #{gmtCreated}, #{gmtModified})")
    void insert(User user);

    @Select("Select * from user where token = #{token}")
    User getByToken(String token);
}
