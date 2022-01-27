package com.guru.future.biz.manager;

import com.guru.future.domain.TsFutureDailyDO;
import com.guru.future.mapper.TsFutureDailyDAO;
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
    private TsFutureDailyDAO tsFutureDailyDAO;

    public Boolean batchAddFutureBasic(List<TsFutureDailyDO> tsFutureDailyDOList) {
        for (TsFutureDailyDO tsFutureDailyDO : tsFutureDailyDOList) {
            try {
                tsFutureDailyDAO.insert(tsFutureDailyDO);
            } catch (Exception e) {
                log.info("insert row error:", e);
            }
        }
        return true;
    }
}
