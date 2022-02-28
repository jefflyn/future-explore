package com.guru.future.biz.manager;

import com.guru.future.domain.TsFutureHoldingDO;
import com.guru.future.mapper.TsFutureHoldingDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author j
 */
@Component
@Slf4j
public class TsFutureHoldingManager {
    @Resource
    private TsFutureHoldingDAO tsFutureHoldingDAO;

    public Boolean batchAddFutureHolding(List<TsFutureHoldingDO> tsFutureHoldingDOList) {
        for (TsFutureHoldingDO tsFutureHoldingDO : tsFutureHoldingDOList) {
            try {
                tsFutureHoldingDAO.insertSelective(tsFutureHoldingDO);
            } catch (Exception e) {
                log.info("insert holding row error, please retry! code={}, tradeDate={}",
                        tsFutureHoldingDO.getSymbol(), tsFutureHoldingDO.getTradeDate());
            }
        }
        return true;
    }
}
