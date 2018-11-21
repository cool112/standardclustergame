package com.garow.logserver.generator.mapper;

import com.garow.logserver.generator.entity.CommonLog;
import com.garow.logserver.generator.entity.CommonLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CommonLogMapper {
    int deleteByExample(CommonLogExample example);

    int insert(CommonLog record);

    int insertSelective(CommonLog record);

    List<CommonLog> selectByExample(CommonLogExample example);

    int updateByExampleSelective(@Param("record") CommonLog record, @Param("example") CommonLogExample example);

    int updateByExample(@Param("record") CommonLog record, @Param("example") CommonLogExample example);
    
    int insertBatch(@Param("logList") List<CommonLog> logList);
}