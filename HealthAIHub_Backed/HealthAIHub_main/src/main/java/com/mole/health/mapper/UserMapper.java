package com.mole.health.mapper;

import com.mole.health.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Cyanix-0721
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2024-10-15 15:40:59
* @Entity com.mole.health.pojo.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




