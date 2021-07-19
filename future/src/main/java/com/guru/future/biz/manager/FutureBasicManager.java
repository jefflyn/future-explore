package com.guru.future.biz.manager;

import com.guru.future.domain.FutureBasicDO;
import com.guru.future.mapper.FutureBasicDAO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jeff
 */
@Component
public class FutureBasicManager {
    @Resource
    private FutureBasicDAO futureBasicsDAO;

    public List<String> getAllCodes() {
        List<FutureBasicDO> futureBasicDOList = getAll();
        return futureBasicDOList.stream().map(FutureBasicDO::getCode).collect(Collectors.toList());
    }

    public List<FutureBasicDO> getAll() {
        return futureBasicsDAO.selectByQuery(null);
    }
}
