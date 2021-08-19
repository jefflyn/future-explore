package com.guru.future.common.enums;

public enum DailyCollectType {
    COLLECT_TIMED(1, "定时采集"),
    COLLECT_LOW(2, "日内最低"),
    COLLECT_HIGH(3, "日内最高"),
    ;

    private Integer id;
    private String desc;

    DailyCollectType(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }
}
