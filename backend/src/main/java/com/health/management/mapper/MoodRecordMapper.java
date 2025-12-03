package com.health.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.management.entity.MoodRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 情绪记录Mapper
 */
@Mapper
public interface MoodRecordMapper extends BaseMapper<MoodRecord> {
}
