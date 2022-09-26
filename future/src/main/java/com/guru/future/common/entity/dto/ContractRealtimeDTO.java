package com.guru.future.common.entity.dto;

import com.guru.future.common.utils.NumberUtil;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ContractRealtimeDTO {
    /**
     * 0：code
     */
    private String code;

    /**
     * 1：名字
     */
    private String name;

    /**
     * 2：不明数字
     */
    private Integer unknownNumber;

    /**
     * 3：开盘价
     */
    private BigDecimal open;

    /**
     * 4：最高价
     */
    private BigDecimal high;

    /**
     * 5：最低价
     */
    private BigDecimal low;

    /**
     * 6：昨日收盘价（有误）
     */
    private BigDecimal preClose;

    /**
     * 7：买价，即“买一”报价
     */
    private BigDecimal bid;

    /**
     * 8：卖价，即“卖一”报价
     */
    private BigDecimal ask;

    /**
     * 9：最新价，即收盘价
     */
    private BigDecimal price;

    /**
     * 10：今结算价
     */
    private BigDecimal settle;

    /**
     * 11：昨结算
     */
    private BigDecimal preSettle;

    /**
     * 12：买量
     */
    private Integer buyVol;

    /**
     * 13：卖量
     */
    private Integer sellVol;

    /**
     * 14：持仓量
     */
    private Integer holdVol;

    /**
     * 15：成交量
     */
    private Integer dealVol;

    /**
     * 16：商品交易所简称
     */
    private String exchange;

    /**
     * 17：品种名简称
     */
    private String alias;

    /**
     * 18：日期
     */
    private String tradeDate;

    /**
     * 28：今均价
     */
    private BigDecimal avgPrice;

    public static ContractRealtimeDTO convertFromHqList(List<String> list) {
        ContractRealtimeDTO contractRealtimeDTO = new ContractRealtimeDTO();
        if (!CollectionUtils.isEmpty(list)) { //&& SinaHqUtil.HQ_LIST_SIZE.equals(list.size())) {
            //        var hq_str_nf_PP2209="xxx2209,101459,8910.000,8967.000,8862.000,0.000,8882.000,8884.000,8882.000,0.000,8878.000,5,62,414914.000,348914,连,聚丙烯,2022-06-02,1,,,,,,,,,8911.924,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0"
            contractRealtimeDTO.setCode(list.get(0));
            contractRealtimeDTO.setName(list.get(1));
            contractRealtimeDTO.setUnknownNumber(Integer.valueOf(list.get(2)));
            contractRealtimeDTO.setOpen(NumberUtil.string2Decimal(list.get(3)));
            contractRealtimeDTO.setHigh(NumberUtil.string2Decimal(list.get(4)));
            contractRealtimeDTO.setLow(NumberUtil.string2Decimal(list.get(5)));
            contractRealtimeDTO.setPreClose(NumberUtil.string2Decimal(list.get(6)));
            contractRealtimeDTO.setBid(NumberUtil.string2Decimal(list.get(7)));
            contractRealtimeDTO.setAsk(NumberUtil.string2Decimal(list.get(8)));
            contractRealtimeDTO.setPrice(NumberUtil.string2Decimal(list.get(9)));
            contractRealtimeDTO.setSettle(NumberUtil.string2Decimal(list.get(10)));
            contractRealtimeDTO.setPreSettle(NumberUtil.string2Decimal(list.get(11)));
            contractRealtimeDTO.setBuyVol(NumberUtil.string2Integer(list.get(12)));
            contractRealtimeDTO.setSellVol(NumberUtil.string2Integer(list.get(13)));
            contractRealtimeDTO.setHoldVol(NumberUtil.string2Integer(list.get(14)));
            contractRealtimeDTO.setDealVol(NumberUtil.string2Integer(list.get(15)));
            contractRealtimeDTO.setExchange(list.get(16));
            contractRealtimeDTO.setAlias(list.get(17));
            contractRealtimeDTO.setTradeDate(list.get(18));
            contractRealtimeDTO.setAvgPrice(NumberUtil.string2Decimal(list.get(28)));
        }
        return contractRealtimeDTO;
    }


}
