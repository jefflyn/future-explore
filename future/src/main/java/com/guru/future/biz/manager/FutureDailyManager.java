package com.guru.future.biz.manager;

import com.guru.future.common.entity.query.FutureDailyQuery;
import com.guru.future.domain.FutureDailyDO;
import com.guru.future.mapper.FutureDailyDAO;
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
    private FutureDailyDAO futureDailyDAO;

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
        FutureDailyQuery query = new FutureDailyQuery();
        query.setTradeDate(tradeDate);
        query.setCodes(codes);
        List<FutureDailyDO> futureDailyDOList = futureDailyDAO.selectByQuery(query);
        return futureDailyDOList.stream().collect(Collectors.toMap(FutureDailyDO::getCode, Function.identity()));
    }
}
