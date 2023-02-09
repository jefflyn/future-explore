package com.guru.future.biz.manager;

import cn.hutool.core.bean.BeanUtil;
import com.guru.future.common.entity.dao.ContractDO;
import com.guru.future.common.entity.domain.Contract;
import com.guru.future.mapper.ContractMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author j
 */
@Component
public class ContractManager {
    @Resource
    private ContractMapper contractMapper;

    public Map<String, Contract> getContractMap() {
        return getAllContract().stream().collect(Collectors.toMap(Contract::getCode, Function.identity()));
    }

    public List<Contract> getAllContract() {
        List<ContractDO> contractDOList = contractMapper.selectByQuery(null);
        return contractDOList.stream().map(e -> BeanUtil.toBean(e, Contract.class)).collect(Collectors.toList());
    }

    public List<String> getAllContractCodes() {
        List<String> codes = new ArrayList<>();
        for (Contract contract : getAllContract()) {
            codes.add(contract.getCode());
            codes.add(contract.getSymbol());
        }
        return codes;
    }

    public List<String> getContractCodes() {
        List<String> codes = new ArrayList<>();
        for (Contract contract : getAllContract()) {
            codes.add(contract.getCode());
        }
        return codes;
    }

    public List<String> getContractTsCodes() {
        List<String> codes = new ArrayList<>();
        for (Contract contract : getAllContract()) {
            codes.add(contract.getTsCode());
        }
        return codes;
    }

    public void updateContract(ContractDO contractDO) {
        contractMapper.updateByPrimaryKeySelective(contractDO);
    }
}
