package com.health.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.management.entity.HealthExamination;
import org.apache.ibatis.annotations.Mapper;

/**
 * 体检报告Mapper
 */
@Mapper
public interface HealthExaminationMapper extends BaseMapper<HealthExamination> {
}
