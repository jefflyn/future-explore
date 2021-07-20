package com.guru.future.biz.manager;

import com.guru.future.common.entity.query.FutureDailyQuery;
import com.guru.future.domain.FutureDailyDO;
import com.guru.future.mapper.FutureDailyDAO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author j
 */
@Component
public class FutureDailyManager {
    @Resource
    private FutureDailyDAO futureDailyDAO;

    @Transactional
    public Boolean addFutureDaily(FutureDailyDO futureDailyDO) {
        return futureDailyDAO.insertSelective(futureDailyDO) > 0;
    }

    @Transactional
    public Boolean updateFutureDaily(FutureDailyDO futureDailyDO) {
        return futureDailyDAO.updateByPrimaryKeySelective(futureDailyDO) > 0;
    }

    public Map<String, FutureDailyDO> getFutureDailyMap(String tradeDate, List<String> codes) {
        FutureDailyQuery query = new FutureDailyQuery();
        query.setTradeDate(tradeDate);
        query.setCodes(codes);
        List<FutureDailyDO> futureDailyDOList = futureDailyDAO.selectByQuery(query);
        return futureDailyDOList.stream().collect(Collectors.toMap(FutureDailyDO::getCode, Function.identity()));
    }
}
