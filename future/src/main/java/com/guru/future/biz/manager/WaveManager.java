package com.guru.future.biz.manager;

import cn.hutool.core.bean.BeanUtil;
import com.guru.future.common.entity.dao.WaveDO;
import com.guru.future.common.entity.domain.Wave;
import com.guru.future.mapper.WaveMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author j
 */
@Component
public class WaveManager {
    @Resource
    private WaveMapper waveMapper;

    public Map<String, Wave> getWaveMap() {
        List<WaveDO> waveDOList = waveMapper.selectAll();

        return waveDOList.stream().map(
                e -> BeanUtil.toBean(e, Wave.class)
        ).collect(Collectors.toMap(Wave::getCode, Function.identity()));
    }


}
