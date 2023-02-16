package com.guru.future.common.enums;

public enum LogType {
    UP_FAST(11, "上涨"),
    UP_FLASH(12, "急速上涨"),
    DOWN_FAST(13, "下跌"),
    DOWN_FLASH(14, "急速下跌"),

    HIGH_DAY(21, "日内高点"),
    HIGH_CONTRACT(22, "合约高点"),
    HIGH_HIST(23, "历史高点"),
    LOW_DAY(24, "日内低点"),
    LOW_CONTRACT(25, "合约低点"),
    LOW_HIST(26, "历史低点"),

    MA_UP_5(31, "突破5日线"),
    MA_UP_10(32, "突破10日线"),
    MA_UP_20(33, "突破20日线"),
    MA_UP_60(34, "突破60日线"),

    MA_DOWN_5(41, "跌破5日线"),
    MA_DOWN_10(42, "跌破10日线"),
    MA_DOWN_20(43, "跌破20日线"),
    MA_DOWN_60(44, "跌破60日线"),

    ;

    private Integer code;
    private String desc;

    LogType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
