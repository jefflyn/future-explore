package com.guru.future.biz.manager;

import com.guru.future.domain.FutureDailyCollectDO;
import com.guru.future.mapper.FutureDailyCollectDAO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author j
 */
@Component
public class FutureDailyCollectManager {
    @Resource
    private FutureDailyCollectDAO futureDailyCollectDAO;

    @Transactional(rollbackFor = Exception.class)
    public Boolean addDailyCollect(FutureDailyCollectDO futureDailyCollectDO) {
        return futureDailyCollectDAO.insertSelective(futureDailyCollectDO) > 0;
    }

}
