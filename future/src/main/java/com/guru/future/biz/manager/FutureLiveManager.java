package com.guru.future.biz.manager;

import com.guru.future.common.entity.dao.FutureLiveDO;
import com.guru.future.mapper.LiveDAO;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author j
 */
@Component
public class FutureLiveManager {
    @Resource
    private LiveDAO liveDAO;

    public FutureLiveDO getLiveDOByCode(String code) {
        return liveDAO.selectByPrimaryKey(code);
    }

    public List<FutureLiveDO> getAll() {
        return liveDAO.selectAll();
    }

    public Boolean upsertFutureLive(FutureLiveDO futureLiveDO) {
        if (futureLiveDO == null || Strings.isBlank(futureLiveDO.getCode())) {
            return false;
        }
        FutureLiveDO existedLiveDO = getLiveDOByCode(futureLiveDO.getCode());
        if (existedLiveDO != null) {
            if (!futureLiveDO.changeFlag().equals(existedLiveDO.changeFlag())) {
                return liveDAO.updateByPrimaryKeySelective(futureLiveDO) > 0;
            }
            return false;
        } else {
            return liveDAO.insertSelective(futureLiveDO) > 0;
        }
    }

    public Boolean removeAllData() {
        return liveDAO.deleteAll() > 0;
    }

}
