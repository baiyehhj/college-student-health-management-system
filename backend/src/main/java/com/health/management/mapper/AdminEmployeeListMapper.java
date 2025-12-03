package com.health.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.management.entity.AdminEmployeeList;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员工号预设表Mapper接口
 */
@Mapper
public interface AdminEmployeeListMapper extends BaseMapper<AdminEmployeeList> {
}
