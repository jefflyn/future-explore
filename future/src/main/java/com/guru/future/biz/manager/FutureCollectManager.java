package com.guru.future.biz.manager;

import com.guru.future.common.enums.CollectType;
import com.guru.future.domain.FutureCollectDO;
import com.guru.future.mapper.FutureCollectDAO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author j
 */
@Component
public class FutureCollectManager {
    private List<String> collectCodes;

    private CollectType collectType = CollectType.COLLECT_TIMED;

    @Resource
    private FutureSinaManager futureSinaManager;

    @Resource
    private FutureCollectDAO futureDailyCollectDAO;

    public FutureCollectDO getLastDailyByCode(String code) {
        return futureDailyCollectDAO.selectLastByCode(code);
    }

    public Boolean addDailyCollect(FutureCollectDO futureCollectDO) {
        return futureDailyCollectDAO.insertSelective(futureCollectDO) > 0;
    }

    public List<FutureCollectDO> getCurrentDateDaily() {
        return futureDailyCollectDAO.selectByCurrentDate();
    }

    public CollectType getCollectType() {
        return collectType;
    }

    public void setCollectType(CollectType collectType) {
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
