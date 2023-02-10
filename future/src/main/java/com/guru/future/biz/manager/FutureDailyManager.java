package com.guru.future.biz.manager;

import com.guru.future.common.entity.dao.TradeDailyDO;
import com.guru.future.common.entity.query.FutureDailyQuery;
import com.guru.future.mapper.DailyMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
    private DailyMapper dailyMapper;

    @Transactional(rollbackFor = Exception.class)
    public Boolean addFutureDaily(TradeDailyDO tradeDailyDO) {
        return dailyMapper.insertSelective(tradeDailyDO) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateFutureDaily(TradeDailyDO tradeDailyDO) {
        return dailyMapper.updateByCodeTradeDateSelective(tradeDailyDO) > 0;
    }

    public TradeDailyDO getFutureDaily(String tradeDate, String code) {
        FutureDailyQuery query = new FutureDailyQuery();
        query.setTradeDate(tradeDate);
        query.setCode(code);
        List<TradeDailyDO> tradeDailyDOList = dailyMapper.selectByQuery(query);
        if (CollectionUtils.isEmpty(tradeDailyDOList)) {
            return null;
        }
        return tradeDailyDOList.get(0);
    }

    public Map<String, TradeDailyDO> getFutureDailyMap(String tradeDate, List<String> codes) {
        FutureDailyQuery futureDailyQuery = new FutureDailyQuery();
        futureDailyQuery.setCodes(codes);
        futureDailyQuery.setTradeDate(tradeDate);
        List<TradeDailyDO> dailyDOList = dailyMapper.selectByQuery(futureDailyQuery);
        return dailyDOList.stream().collect(Collectors.toMap(TradeDailyDO::getCode, Function.identity()));
    }
}
