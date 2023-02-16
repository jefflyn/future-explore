package com.guru.future.common.entity.domain;

import lombok.Data;

import java.util.Date;

@Data
public class NDayStat {
    private String code;

    private Double closeChange;

    private Double settleChange;

    private Boolean up;

    private Integer days;

    private Double thrDChange;

    private Double fvDChange;

    private Double svDChange;

    private Integer price;

    private Integer avg5d;

    private Integer avg10d;

    private Integer avg20d;

    private Integer avg60d;

    private Double p5t10;

    private Double pt5;

    private Double pt10;

    private Double pt20;

    private Double pt60;

    private Boolean trendUp;

    private Date updateTime;

    public boolean isMa5Up() {
        return avg5d >= avg10d && avg10d >= avg20d && avg20d >= avg60d;
    }

    public boolean isMa10Up() {
        return avg10d >= avg20d && avg20d >= avg60d;
    }

    public boolean isMa20Up() {
        return avg20d >= avg60d;
    }

    public boolean isMa5Down() {
        return avg5d <= avg10d && avg10d <= avg20d && avg20d <= avg60d;
    }

    public boolean isMa10Down() {
        return avg10d <= avg20d && avg20d <= avg60d;
    }

    public boolean isMa20Down() {
        return avg20d <= avg60d;
    }



}