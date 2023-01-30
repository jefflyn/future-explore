package com.guru.future.biz.manager;

import com.guru.future.common.entity.query.FutureDailyQuery;
import com.guru.future.common.entity.dao.TsFutureDailyDO;
import com.guru.future.mapper.TsDailyDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author j
 */
@Component
@Slf4j
public class TsFutureDailyManager {
    @Resource
    private TsDailyDAO tsDailyDAO;

    public Boolean batchAddFutureDaily(List<TsFutureDailyDO> tsFutureDailyDOList) {
        for (TsFutureDailyDO tsFutureDailyDO : tsFutureDailyDOList) {
            try {
                tsDailyDAO.insert(tsFutureDailyDO);
            } catch (Exception e) {
                log.info("insert row error, please retry! tsCode={}, tradeDate={}", tsFutureDailyDO.getTsCode(), tsFutureDailyDO.getTradeDate());
            }
        }
        return true;
    }

    public List<TsFutureDailyDO> getByTsCode(String tsCode, String startDate, String endDate) {
        FutureDailyQuery futureDailyQuery = new FutureDailyQuery();
        futureDailyQuery.setCode(tsCode);
        futureDailyQuery.setStartDate(startDate);
        futureDailyQuery.setEndDate(endDate);
        return tsDailyDAO.selectByQuery(futureDailyQuery);
    }
}
