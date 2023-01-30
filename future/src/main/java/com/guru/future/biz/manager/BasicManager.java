package com.guru.future.biz.manager;

import cn.hutool.core.util.ObjectUtil;
import com.guru.future.common.entity.dao.FutureBasicDO;
import com.guru.future.mapper.BasicDAO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author j
 */
@Component
public class BasicManager {
    @Resource
    private BasicDAO futureBasicsDAO;

    private Map<String, FutureBasicDO> FUTURE_BASIC_MAP = new HashMap<>();

    public FutureBasicDO getBasicBySymbol(String symbol) {
        FutureBasicDO futureBasicDO = FUTURE_BASIC_MAP.get(symbol);
        return ObjectUtil.defaultIfNull(futureBasicDO, futureBasicsDAO.selectBySymbol(symbol));
    }

    public Map<String, FutureBasicDO> getBasicMap() {
        return getBasicMap(false);
    }

    public Map<String, FutureBasicDO> getBasicMap(Boolean refresh) {
        if (FUTURE_BASIC_MAP.size() > 0 && Boolean.FALSE.equals(refresh)) {
            return FUTURE_BASIC_MAP;
        }
        List<FutureBasicDO> futureBasicDOList = futureBasicsDAO.selectByQuery(null);
        FUTURE_BASIC_MAP = futureBasicDOList.stream().collect(Collectors.toMap(FutureBasicDO::getSymbol, Function.identity()));
        return FUTURE_BASIC_MAP;
    }

    /**
     * deleted = 0
     *
     * @return
     */
    public List<FutureBasicDO> getAll() {
        return futureBasicsDAO.selectByQuery(null);
    }

}
