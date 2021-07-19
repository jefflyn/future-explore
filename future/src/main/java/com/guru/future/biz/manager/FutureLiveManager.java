package com.guru.future.biz.manager;

import com.guru.future.domain.FutureLiveDO;
import com.guru.future.mapper.FutureLiveDAO;
import org.apache.logging.log4j.util.Strings;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author j
 */
@Component
public class FutureLiveManager {
    @Resource
    private FutureLiveDAO futureLiveDAO;

    public FutureLiveDO getLiveDOByCode(String code) {
        FutureLiveDO futureLiveDO = futureLiveDAO.selectByPrimaryKey(code);
        return futureLiveDO;
    }

    public Boolean upsertFutureLive(FutureLiveDO futureLiveDO) {
        if (futureLiveDO == null || Strings.isBlank(futureLiveDO.getCode())) {
            return false;
        }
        FutureLiveDO existedLiveDO = getLiveDOByCode(futureLiveDO.getCode());
        if (existedLiveDO != null) {
            if (!futureLiveDO.toString().equals(existedLiveDO.toString())) {
                return futureLiveDAO.updateByPrimaryKeySelective(futureLiveDO) > 0;
            }
            return false;
        } else {
            return futureLiveDAO.insertSelective(futureLiveDO) > 0;
        }
    }

}
