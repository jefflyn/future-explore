package com.guru.future.biz.manager;

import com.guru.future.common.entity.query.FutureBasicQuery;
import com.guru.future.domain.TsFutureContractDO;
import com.guru.future.mapper.TsFutureContractDAO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author j
 */
@Component
public class TsFutureBasicManager {
    @Resource
    private TsFutureContractDAO tsFutureContractDAO;

    public Boolean batchAddFutureBasic(List<TsFutureContractDO> tsFutureContractDOList) {
        for (TsFutureContractDO tsFutureContractDO : tsFutureContractDOList) {
            tsFutureContractDAO.insert(tsFutureContractDO);
        }
        return true;
    }

    public TsFutureContractDO getByTsCode(String tsCode) {
        return tsFutureContractDAO.selectByPrimaryKey(tsCode);
    }

    public List<TsFutureContractDO> getTsCodeByType(List<String> tsCodes) {
        FutureBasicQuery query = new FutureBasicQuery();
        query.setCodes(tsCodes);
        return tsFutureContractDAO.selectByQuery(query);
    }

    public List<String> getTsCodeByType(Integer contractType) {
        FutureBasicQuery query = new FutureBasicQuery();
        query.setType(String.valueOf(contractType));
        return tsFutureContractDAO.selectByQuery(query).stream().map(TsFutureContractDO::getTsCode).collect(Collectors.toList());
    }
}
