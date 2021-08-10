package com.guru.future.biz.manager;

import com.guru.future.domain.OpenGapDO;
import com.guru.future.mapper.OpenGapDAO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author j
 */
@Component
public class OpenGapManager {
    @Resource
    private OpenGapDAO openGapDAO;

    @Transactional
    public Boolean addOpenGapLog(OpenGapDO openGapDO) {
        return openGapDAO.insertSelective(openGapDO) > 0;
    }

    @Transactional
    public Boolean batchAddOpenGapLog(List<OpenGapDO> openGapDOList) {
        return openGapDAO.insertBatch(openGapDOList) > 0;
    }
}
