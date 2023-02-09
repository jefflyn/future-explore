package com.guru.future.biz.manager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.guru.future.common.entity.dao.FutureBasicDO;
import com.guru.future.common.entity.domain.Basic;
import com.guru.future.mapper.BasicMapper;
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
    private BasicMapper futureBasicsDAO;

    private Map<String, Basic> FUTURE_BASIC_MAP = new HashMap<>();

    public Basic getBasicBySymbol(String symbol) {
        Basic basic = FUTURE_BASIC_MAP.get(symbol);
        return ObjectUtil.defaultIfNull(basic, BeanUtil.toBean(futureBasicsDAO.selectBySymbol(symbol), Basic.class));
    }

    public Map<String, Basic> getBasicMap() {
        return getBasicMap(false);
    }

    public Map<String, Basic> getBasicMap(Boolean refresh) {
        if (FUTURE_BASIC_MAP.size() > 0 && Boolean.FALSE.equals(refresh)) {
            return FUTURE_BASIC_MAP;
        }
        List<FutureBasicDO> futureBasicDOList = futureBasicsDAO.selectByQuery(null);

        FUTURE_BASIC_MAP = futureBasicDOList.stream().map(
                e -> BeanUtil.toBean(e, Basic.class)
        ).collect(Collectors.toMap(Basic::getSymbol, Function.identity()));
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
