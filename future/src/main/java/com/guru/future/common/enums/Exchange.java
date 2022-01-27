package com.guru.future.common.enums;

public enum Exchange {
    CFFEX("CFFEX", "中金所"),
    DCE("DCE", "大商所 "),
    CZCE("CZCE", "郑商所"),
    SHFE("SHFE", "上期所"),
    INE("INE", "上海国际能源交易中心"),

    ;

    private String code;
    private String desc;

    Exchange(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
