package com.guru.future.common.enums;

public enum OptionType {
    SELL_SHORT(-1, "卖空"),
    BUY_LONG(1, "买多"),

    ;

    private Integer code;
    private String desc;

    OptionType(Integer code, String desc) {
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
