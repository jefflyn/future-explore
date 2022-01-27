package com.guru.future.common.enums;

public enum ContractType {
    CONTINUE(1, "连续"),
    MAIN(2, "主力"),
    NORMAL(3, "普通"),
    ;

    private Integer code;
    private String desc;

    ContractType(Integer code, String desc) {
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
