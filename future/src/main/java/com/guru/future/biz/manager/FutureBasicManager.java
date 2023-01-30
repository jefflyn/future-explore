package com.guru.future.biz.manager;

import cn.hutool.core.util.ObjectUtil;
import com.guru.future.common.entity.dao.FutureBasicDO;
import com.guru.future.mapper.BasicDAO;
import org.springframework.scheduling.annotation.Async;
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
public class FutureBasicManager {
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

    public List<String> getAllCodes() {
        List<FutureBasicDO> futureBasicDOList = getAll();
        return futureBasicDOList.stream().map(FutureBasicDO::getSymbol).collect(Collectors.toList());
    }

    /**
     * deleted = 0
     *
     * @return
     */
    public List<FutureBasicDO> getAll() {
        return futureBasicsDAO.selectByQuery(null);
    }

    public Boolean updateBasic(FutureBasicDO updateBasicDO) {
        if (futureBasicsDAO.updateByCodeSelective(updateBasicDO) > 0) {
            refreshBasicCacheMap();
        }
        return true;
    }

    @Async
    public void refreshBasicCacheMap() {
        List<FutureBasicDO> futureBasicDOList = futureBasicsDAO.selectByQuery(null);
        FUTURE_BASIC_MAP = futureBasicDOList.stream().collect(Collectors.toMap(FutureBasicDO::getSymbol, Function.identity()));
    }
}
