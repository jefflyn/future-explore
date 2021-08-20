package com.guru.future.biz.manager;

import com.guru.future.common.enums.DailyCollectType;
import com.guru.future.domain.FutureDailyCollectDO;
import com.guru.future.mapper.FutureDailyCollectDAO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author j
 */
@Component
public class FutureDailyCollectManager {
    private List<String> collectCodes;

    private DailyCollectType collectType = DailyCollectType.COLLECT_TIMED;

    @Resource
    private FutureSinaManager futureSinaManager;

    @Resource
    private FutureDailyCollectDAO futureDailyCollectDAO;

    @Transactional(rollbackFor = Exception.class)
    public Boolean addDailyCollect(FutureDailyCollectDO futureDailyCollectDO) {
        return futureDailyCollectDAO.insertSelective(futureDailyCollectDO) > 0;
    }

    public DailyCollectType getCollectType() {
        return collectType;
    }

    public void setCollectType(DailyCollectType collectType) {
        this.collectType = collectType;
    }

    public FutureSinaManager getFutureSinaManager() {
        return futureSinaManager;
    }

    public List<String> getCollectCodes() {
        return collectCodes;
    }

    public void setCollectCodes(List<String> collectCodes) {
        this.collectCodes = collectCodes;
    }
}
