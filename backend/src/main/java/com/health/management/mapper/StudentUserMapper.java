package com.health.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.management.entity.StudentUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生用户Mapper
 */
@Mapper
public interface StudentUserMapper extends BaseMapper<StudentUser> {
}
