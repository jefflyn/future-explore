package com.guru.future.biz.manager;

import com.guru.future.common.utils.DateUtil;
import com.guru.future.domain.FutureLogDO;
import com.guru.future.mapper.FutureLogDAO;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author j
 */
@Component
public class FutureLogManager {
    private static Set<String> LOG_CODES_CACHE = new HashSet<>();

    @Resource
    private FutureLogDAO futureLogDAO;

    public List<String> getLogCodes() {
        if (!CollectionUtils.isEmpty(LOG_CODES_CACHE)) {
            return new ArrayList<>(LOG_CODES_CACHE);
        }
        // get from db
        List<String> codes = futureLogDAO.selectCodesByTradeDate(DateUtil.currentTradeDate());
        LOG_CODES_CACHE.addAll(codes);
        return codes;
    }

    public Boolean addFutureLog(FutureLogDO futureLogDO) {
        LOG_CODES_CACHE.add(futureLogDO.getCode());
        return futureLogDAO.insertSelective(futureLogDO) > 0;
    }

    public Boolean deleteLogByType(String code, String tradeDate, String type) {
        FutureLogDO futureLogDO = new FutureLogDO();
        futureLogDO.setTradeDate(tradeDate);
        futureLogDO.setCode(code);
        futureLogDO.setType(type);
        return futureLogDAO.delete(futureLogDO) > 0;
    }
}
