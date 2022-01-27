package com.guru.future.biz.manager;

import com.guru.future.domain.TsFutureContractDO;
import com.guru.future.mapper.TsFutureContractDAO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author j
 */
@Component
public class TsFutureBasicManager {
    @Resource
    private TsFutureContractDAO tsFutureContractDAO;

    public Boolean batchAddFutureBasic(List<TsFutureContractDO> tsFutureContractDOList) {
        for (TsFutureContractDO tsFutureContractDO : tsFutureContractDOList) {
            tsFutureContractDAO.insertSelective(tsFutureContractDO);
        }
        return true;
    }
}
