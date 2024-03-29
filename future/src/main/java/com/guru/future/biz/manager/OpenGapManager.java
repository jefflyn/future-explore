package com.guru.future.biz.manager;

import com.guru.future.common.entity.dao.OpenGapDO;
import com.guru.future.mapper.OpenGapDAO;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class OpenGapManager {
    private static Map<String, Boolean> openGapMap = new HashMap<>();

    @Resource
    private OpenGapDAO openGapDAO;

    public List<OpenGapDO> getCurrentOpenGap() {
        return openGapDAO.selectByCurrentDate();
    }

    public Boolean batchAddOpenGapLog(List<OpenGapDO> openGapDOList) {
        ListIterator<OpenGapDO> openGapIterator = openGapDOList.listIterator();
        while (openGapIterator.hasNext()) {
            OpenGapDO openGapDO = openGapIterator.next();
            String key = openGapDO.getCode() + openGapDO.getTradeDate();
            if (openGapMap.get(key) != null) {
                openGapIterator.remove();
            }
        }
        if (!CollectionUtils.isEmpty(openGapDOList) && openGapDAO.insertBatch(openGapDOList) > 0) {
            log.info("save open gap data successfully!");
            for (OpenGapDO openGapDO : openGapDOList) {
                String key = openGapDO.getCode() + openGapDO.getTradeDate();
                openGapMap.put(key, true);
            }
            return true;
        }
        return false;
    }
}
