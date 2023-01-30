package com.guru.future.biz.service.gene;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.guru.future.biz.manager.BasicManager;
import com.guru.future.biz.manager.TsFutureDailyManager;
import com.guru.future.common.entity.vo.FutureWaveVO;
import com.guru.future.common.entity.dao.TsFutureDailyDO;
import joinery.DataFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.guru.future.common.utils.DateUtil.TRADE_DATE_PATTERN_FLAT;

/**
 * @author j
 */
@Service
@Slf4j
public class WaveService {
    private final static String COLUMN_CODE = "code";
    private final static String COLUMN_DATE = "date";
    private final static String COLUMN_OPEN = "open";
    private final static String COLUMN_HIGH = "high";
    private final static String COLUMN_LOW = "low";
    private final static String COLUMN_CLOSE = "close";
    private final static List<String> DAILY_COLUMNS = Lists.newArrayList(COLUMN_CODE, COLUMN_DATE, COLUMN_OPEN, COLUMN_HIGH, COLUMN_LOW, COLUMN_CLOSE);

    @Resource
    private BasicManager basicManager;

    @Resource
    private TsFutureDailyManager tsFutureDailyManager;


    public void startWaveProcess(List<String> codes, String startDate, String endDate) {
        for (String code : codes) {
            List<TsFutureDailyDO> tsFutureDailyDOList = tsFutureDailyManager.getByTsCode(code, startDate, endDate);
            DataFrame waveDataFrame = buildWaveDataFrame(tsFutureDailyDOList);

            // generateWaveData(waveDataFrame);
        }
    }

    private DataFrame buildWaveDataFrame(List<TsFutureDailyDO> tsFutureDailyDOList) {
        Map<String, List<Object>> dataMap = new HashMap<>();
        for (String column : DAILY_COLUMNS) {
            dataMap.put(column, new ArrayList<>());
        }
        List<Integer> rowIndexes = new ArrayList<>();
        for (int i = 0; i < tsFutureDailyDOList.size(); i++) {
            rowIndexes.add(i);
            TsFutureDailyDO futureDailyDO = tsFutureDailyDOList.get(i);
            dataMap.get(COLUMN_CODE).add(futureDailyDO.getTsCode());
            dataMap.get(COLUMN_DATE).add(futureDailyDO.getTradeDate());
            dataMap.get(COLUMN_OPEN).add(futureDailyDO.getOpen());
            dataMap.get(COLUMN_HIGH).add(futureDailyDO.getHigh());
            dataMap.get(COLUMN_LOW).add(futureDailyDO.getLow());
            dataMap.get(COLUMN_CLOSE).add(futureDailyDO.getClose());
        }
        DataFrame<Object> dfData = new DataFrame<Object>(rowIndexes, DAILY_COLUMNS,
                Arrays.asList(dataMap.get(COLUMN_CODE), dataMap.get(COLUMN_DATE), dataMap.get(COLUMN_OPEN),
                        dataMap.get(COLUMN_HIGH), dataMap.get(COLUMN_LOW), dataMap.get(COLUMN_CLOSE))
        );

        generateWaveData(dfData, -1, true, 0, 15);
//        # sorted by date asc
//        left_data.reverse()
//        right_data = generateWaveData(code, hist_data, beginlow, 'right', duration, pchange)

        return null;
    }

    private List<FutureWaveVO> generateWaveData(DataFrame df, int direction, boolean beginLow, int duration, int change) {
        List<FutureWaveVO> waveVOList = new ArrayList<>();
        String firstDate = (String) df.head(1).get(0, COLUMN_DATE);
        DataFrame lastOne = df.tail(1);
        String lastDate = (String) df.get(lastOne.index().iterator().next(), COLUMN_DATE);
        // start from the lowest price, calc the wave from both sides
        List<BigDecimal> lowList = df.col(COLUMN_LOW);
        BigDecimal pivotLow = Collections.min(lowList);
        int pivotLowIndex = lowList.indexOf(pivotLow);
        List pivotRec = df.row(pivotLowIndex);
        String pivotDate = (String) df.get(pivotLowIndex, COLUMN_DATE);
        BigDecimal pivotClose = (BigDecimal) df.get(pivotLowIndex, COLUMN_CLOSE);

        boolean isMax = beginLow;
        String beginDate = firstDate;
        String endDate = pivotDate;
        BigDecimal beginPrice = pivotLow;
        BigDecimal endPrice = pivotLow;
        if (direction > 0) {
            beginDate = pivotDate;
            endDate = lastDate;
        }
        long diffDays = DateUtil.between(DateUtil.parse(endDate, TRADE_DATE_PATTERN_FLAT),
                DateUtil.parse(beginDate, TRADE_DATE_PATTERN_FLAT), DateUnit.DAY);
        while (diffDays > duration){
//            data = df[(df.date >= begindate) & (df.date < enddate)] if direction == 'left' else df[
//                    (df.date > begindate) & (df.date <= enddate)]
//            price = data.max()['high'] if ismax else data.min()['low']
//
//            status = ''
//            rec = data[data.high == price] if ismax else data[data.low == price]
//            idx = rec.index.to_numpy()[0]
//            date = rec.at[idx, 'date']
//            close = rec.at[idx, 'close']
        }
        return null;
    }

    public static void main(String[] args) {
        DataFrame<Object> df = new DataFrame<Object>(
                Arrays.asList(0, 1, 2),
                Arrays.asList("name", "value"),
                Arrays.asList(
                        Arrays.asList("alpha", "bravo", "charlie"), Arrays.asList(10, 20, 30)
                )
        );
        Object result = df.get(2, "name");
        System.out.println(result);
        System.out.println(df.map());
    }
}
