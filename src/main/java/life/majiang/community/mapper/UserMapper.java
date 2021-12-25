package life.majiang.community.mapper;

import life.majiang.community.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Insert("insert into user (name, account_id, token, gmt_created, gmt_modified, avatar_url) values (#{name}, #{accountId}, #{token}, #{gmtCreated}, #{gmtModified}, #{avatarUrl})")
    void insert(User user);

    @Select("select * from user where token = #{token}")
    User getByToken(String token);

    @Select("select * from user where id = #{id}")
    User findById(@Param("id") Integer id);
}
