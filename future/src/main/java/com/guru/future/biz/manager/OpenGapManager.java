package com.guru.future.biz.manager;

import com.guru.future.domain.OpenGapDO;
import com.guru.future.mapper.OpenGapDAO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * @author j
 */
@Component
public class OpenGapManager {
    private static Map<String, Boolean> OPEN_GAP_MAP = new HashMap<>();

    @Resource
    private OpenGapDAO openGapDAO;

    @Transactional
    public Boolean addOpenGapLog(OpenGapDO openGapDO) {
        return openGapDAO.insertSelective(openGapDO) > 0;
    }

    @Transactional
    public Boolean batchAddOpenGapLog(List<OpenGapDO> openGapDOList) {
        ListIterator<OpenGapDO> openGapIterator = openGapDOList.listIterator();
        while (openGapIterator.hasNext()) {
            OpenGapDO openGapDO = openGapIterator.next();
            String key = openGapDO.getCode() + openGapDO.getTradeDate();
            if (OPEN_GAP_MAP.get(key) != null) {
                openGapIterator.remove();
            }
        }
        if (!CollectionUtils.isEmpty(openGapDOList)) {
            if (openGapDAO.insertBatch(openGapDOList) > 0) {
                for (OpenGapDO openGapDO : openGapDOList) {
                    String key = openGapDO.getCode() + openGapDO.getTradeDate();
                    OPEN_GAP_MAP.put(key, true);
                }
                return true;
            }
        }
        return false;
    }
}
