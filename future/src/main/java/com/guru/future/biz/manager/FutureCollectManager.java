package com.guru.future.biz.manager;

import com.guru.future.biz.manager.remote.FutureSinaManager;
import com.guru.future.common.entity.query.FutureDailyQuery;
import com.guru.future.common.enums.CollectType;
import com.guru.future.common.entity.dao.FutureCollectDO;
import com.guru.future.mapper.CollectDAO;
import org.springframework.stereotype.Component;

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
    private CollectDAO futureDailyCollectDAO;

    public FutureCollectDO getLastDailyByCode(String code) {
        return futureDailyCollectDAO.selectLastByCode(code);
    }

    public Boolean addDailyCollect(FutureCollectDO futureCollectDO) {
        return futureDailyCollectDAO.insertSelective(futureCollectDO) > 0;
    }

    public List<FutureCollectDO> getDailyCollect(String tradeDate, List<String> codes) {
        FutureDailyQuery dailyQuery = new FutureDailyQuery();
        dailyQuery.setTradeDate(tradeDate);
        dailyQuery.setCodes(codes);
        return futureDailyCollectDAO.selectByQuery(dailyQuery);
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
