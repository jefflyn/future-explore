package com.guru.future.biz.manager;

import com.guru.future.domain.FutureLogDO;
import com.guru.future.mapper.FutureLogDAO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author j
 */
@Component
public class FutureLogManager {
    @Resource
    private FutureLogDAO futureLogDAO;

    public Boolean addFutureLog(FutureLogDO futureLogDO) {
        return futureLogDAO.insertSelective(futureLogDO) > 0;
    }

}
