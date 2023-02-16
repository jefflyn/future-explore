package com.guru.future.biz.manager;

import cn.hutool.core.bean.BeanUtil;
import com.guru.future.common.entity.dao.NDayStatDO;
import com.guru.future.common.entity.domain.NDayStat;
import com.guru.future.mapper.NDayStatMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author j
 */
@Component
public class StatManager {
    @Resource
    private NDayStatMapper nDayStatMapper;

    @Cacheable(cacheManager = "hour1CacheManager", value = "nDateStat", key = "#code", unless = "#result==null")
    public NDayStat getDayStat(String code) {
        NDayStatDO nDayStatDO = nDayStatMapper.selectAll().stream().collect(Collectors.toMap(NDayStatDO::getCode, Function.identity())).get(code);
        if (nDayStatDO != null) {
            return BeanUtil.toBean(nDayStatDO, NDayStat.class);
        }
        return null;
    }

}
