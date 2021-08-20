package com.guru.future.biz.manager;

import com.guru.future.common.utils.DateUtil;
import com.guru.future.domain.FutureLogDO;
import com.guru.future.mapper.FutureLogDAO;
import org.springframework.stereotype.Component;

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
        if (LOG_CODES_CACHE.size() > 0) {
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

}
