package com.mole.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mole.health.pojo.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Cyanix-0721
 * @description 针对表【admin(管理员表)】的数据库操作Mapper
 * @createDate 2024-10-15 15:40:59
 * @Entity com.mole.health.pojo.Admin
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

}




