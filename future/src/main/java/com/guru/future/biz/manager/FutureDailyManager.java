package com.guru.future.biz.manager;

import com.guru.future.common.entity.query.FutureDailyQuery;
import com.guru.future.domain.FutureDailyDO;
import com.guru.future.domain.TsFutureDailyDO;
import com.guru.future.mapper.FutureDailyDAO;
import com.guru.future.mapper.TsFutureDailyDAO;
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
    private FutureDailyDAO futureDailyDAO;
    @Resource
    private TsFutureDailyDAO tsfutureDailyDAO;

    @Transactional(rollbackFor = Exception.class)
    public Boolean addFutureDaily(FutureDailyDO futureDailyDO) {
        return futureDailyDAO.insertSelective(futureDailyDO) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateFutureDaily(FutureDailyDO futureDailyDO) {
        return futureDailyDAO.updateByCodeTradeDateSelective(futureDailyDO) > 0;
    }

    public FutureDailyDO getFutureDaily(String tradeDate, String code) {
        FutureDailyQuery query = new FutureDailyQuery();
        query.setTradeDate(tradeDate);
        query.setCode(code);
        List<FutureDailyDO> futureDailyDOList = futureDailyDAO.selectByQuery(query);
        if (CollectionUtils.isEmpty(futureDailyDOList)) {
            return null;
        }
        return futureDailyDOList.get(0);
    }

    public Map<String, FutureDailyDO> getFutureDailyMap(String tradeDate, List<String> codes) {
        List<FutureDailyDO> futureDailyDOList = new ArrayList<>();
        FutureDailyQuery futureDailyQuery = new FutureDailyQuery();
        futureDailyQuery.setCodes(codes);
        futureDailyQuery.setStartDate(tradeDate);
        futureDailyQuery.setEndDate(tradeDate);
        List<TsFutureDailyDO> tsFutureDailyDOList = tsfutureDailyDAO.selectByQuery(futureDailyQuery);
        for (TsFutureDailyDO tsFutureDailyDO : tsFutureDailyDOList) {
            FutureDailyDO futureDailyDO = new FutureDailyDO();
            futureDailyDO.setTradeDate(tradeDate);
            String tsCode = tsFutureDailyDO.getTsCode();
            String code = tsCode.substring(0, tsCode.indexOf("."));
            futureDailyDO.setCode(code);
            futureDailyDO.setClose(tsFutureDailyDO.getClose());
            futureDailyDO.setSettle(tsFutureDailyDO.getSettle());
            futureDailyDO.setOpen(tsFutureDailyDO.getOpen());
            futureDailyDO.setHigh(tsFutureDailyDO.getHigh());
            futureDailyDO.setLow(tsFutureDailyDO.getLow());
            futureDailyDO.setPreClose(tsFutureDailyDO.getPreClose());
            futureDailyDO.setPreSettle(tsFutureDailyDO.getPreSettle());
            futureDailyDOList.add(futureDailyDO);
        }
        return futureDailyDOList.stream().collect(Collectors.toMap(FutureDailyDO::getCode, Function.identity()));
    }
}
