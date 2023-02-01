package com.guru.future.biz.manager;

import com.guru.future.common.entity.query.FutureDailyQuery;
import com.guru.future.common.entity.dao.TradeDailyDO;
import com.guru.future.common.entity.dao.TsFutureDailyDO;
import com.guru.future.mapper.DailyMapper;
import com.guru.future.mapper.TsDailyDAO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    @Resource
    private TsDailyDAO tsfutureDailyDAO;

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
        List<TradeDailyDO> tradeDailyDOList = new ArrayList<>();
        FutureDailyQuery futureDailyQuery = new FutureDailyQuery();
        futureDailyQuery.setCodes(codes);
        futureDailyQuery.setStartDate(tradeDate);
        futureDailyQuery.setEndDate(tradeDate);
        List<TsFutureDailyDO> tsFutureDailyDOList = tsfutureDailyDAO.selectByQuery(futureDailyQuery);
        for (TsFutureDailyDO tsFutureDailyDO : tsFutureDailyDOList) {
            TradeDailyDO tradeDailyDO = new TradeDailyDO();
            tradeDailyDO.setTradeDate(tradeDate);
            String tsCode = tsFutureDailyDO.getTsCode();
            String code = tsCode.substring(0, tsCode.indexOf("."));
            tradeDailyDO.setCode(code);
            tradeDailyDO.setClose(tsFutureDailyDO.getClose());
            tradeDailyDO.setSettle(tsFutureDailyDO.getSettle());
            tradeDailyDO.setOpen(tsFutureDailyDO.getOpen());
            tradeDailyDO.setHigh(tsFutureDailyDO.getHigh());
            tradeDailyDO.setLow(tsFutureDailyDO.getLow());
            tradeDailyDO.setPreClose(tsFutureDailyDO.getPreClose());
            tradeDailyDO.setPreSettle(tsFutureDailyDO.getPreSettle());
            tradeDailyDOList.add(tradeDailyDO);
        }
        return tradeDailyDOList.stream().collect(Collectors.toMap(TradeDailyDO::getCode, Function.identity()));
    }
}
