package com.health.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.management.entity.SleepRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 睡眠记录Mapper
 */
@Mapper
public interface SleepRecordMapper extends BaseMapper<SleepRecord> {
}
