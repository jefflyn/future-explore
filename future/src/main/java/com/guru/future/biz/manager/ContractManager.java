package com.guru.future.biz.manager;

import cn.hutool.core.bean.BeanUtil;
import com.guru.future.common.entity.dao.ContractDO;
import com.guru.future.common.entity.dao.FutureBasicDO;
import com.guru.future.common.entity.domain.Contract;
import com.guru.future.mapper.ContractDAO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author j
 */
@Component
public class ContractManager {
    @Resource
    private ContractDAO contractDAO;

    private Map<String, FutureBasicDO> FUTURE_BASIC_MAP = new HashMap<>();

    public List<Contract> getAllContract() {
        List<ContractDO> contractDOList = contractDAO.selectByQuery(null);
        return contractDOList.stream().map(e -> BeanUtil.toBean(e, Contract.class)).collect(Collectors.toList());
    }

    public List<String> getContractCodes() {
        List<String> codes = new ArrayList<>();
        for (Contract contract : getAllContract()) {
            codes.add(contract.getCode());
            codes.add(contract.getSymbol());
        }
        return codes;
    }
}
