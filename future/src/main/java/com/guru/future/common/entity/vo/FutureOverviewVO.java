package com.guru.future.common.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FutureOverviewVO {
    private String totalAvgChangeStr;

    private String overviewDesc;

    private List<CategorySummary> categorySummaryList;

    @Data
    public static class CategorySummary implements Comparable<CategorySummary> {
        private String categoryName;

        private BigDecimal avgChange;

        private String avgChangeStr;

        private String bestName;

        private String bestChange;

        private BigDecimal bestPrice;

        private String worstName;

        private String worstChange;

        private BigDecimal worstPrice;

        @Override
        public int compareTo(CategorySummary o) {
            return this.avgChange.compareTo(o.avgChange);
        }
    }

    @Override
    public String toString(){
        StringBuilder resultStr = new StringBuilder("市场平均涨幅: " + getTotalAvgChangeStr() + "【" + getOverviewDesc() + "】\n");
        for (FutureOverviewVO.CategorySummary categorySummary : getCategorySummaryList()) {
            resultStr.append(String.format("%-4s", categorySummary.getCategoryName()))
                    .append(categorySummary.getAvgChangeStr())
                    .append(" 【")
                    .append(String.format("%s: ", categorySummary.getBestName()))
                    .append(categorySummary.getBestChange()).append("(").append(categorySummary.getBestPrice()).append("), ")
                    .append(String.format("%s: ", categorySummary.getWorstName()))
                    .append(categorySummary.getWorstChange()).append("(").append(categorySummary.getWorstPrice()).append(")")
                    .append("】\n");
        }
        return resultStr.toString();
    }

}