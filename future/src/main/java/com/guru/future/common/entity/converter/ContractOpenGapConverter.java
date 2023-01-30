package com.guru.future.common.entity.converter;

import com.guru.future.common.entity.dto.ContractOpenGapDTO;
import com.guru.future.common.entity.dao.OpenGapDO;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class ContractOpenGapConverter {

    public static OpenGapDO convert2OpenGapDO(ContractOpenGapDTO contractOpenGapDTO) {
        OpenGapDO openGapDO = new OpenGapDO();
        BeanUtils.copyProperties(contractOpenGapDTO, openGapDO);
        return openGapDO;
    }

    public static List<OpenGapDO> convert2OpenGapDOList(List<ContractOpenGapDTO> contractOpenGapDTOList) {
        List<OpenGapDO> openGapDOList = new ArrayList<>();
        for (ContractOpenGapDTO contractOpenGapDTO : contractOpenGapDTOList) {
            openGapDOList.add(convert2OpenGapDO(contractOpenGapDTO));
        }
        return openGapDOList;
    }
}
