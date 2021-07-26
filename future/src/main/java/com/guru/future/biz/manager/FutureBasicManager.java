package com.guru.future.biz.manager;

import com.guru.future.domain.FutureBasicDO;
import com.guru.future.mapper.FutureBasicDAO;
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
    private FutureBasicDAO futureBasicsDAO;

    private Map<String, FutureBasicDO> FUTURE_BASIC_MAP = new HashMap<>();

    public Map<String, FutureBasicDO> getBasicMap() {
        if (FUTURE_BASIC_MAP.size() > 0) {
            return FUTURE_BASIC_MAP;
        }
        List<FutureBasicDO> futureBasicDOList = futureBasicsDAO.selectByQuery(null);
        return futureBasicDOList.stream().collect(Collectors.toMap(FutureBasicDO::getCode, Function.identity()));
    }

    public List<String> getAllCodes() {
        List<FutureBasicDO> futureBasicDOList = getAll();
        return futureBasicDOList.stream().map(FutureBasicDO::getCode).collect(Collectors.toList());
    }

    public List<FutureBasicDO> getAll() {
        return futureBasicsDAO.selectByQuery(null);
    }

    public Boolean updateBasic(FutureBasicDO updateBasicDO){
        return futureBasicsDAO.updateByPrimaryKeySelective(updateBasicDO) > 0;
    }
}
