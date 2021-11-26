package com.guru.future.common.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FutureOverviewVO {
    private BigDecimal avgChange;

    private String overviewDesc;

    private List<CategorySummary> categorySummaryList;

    @Data
    static class CategorySummary implements Comparable<CategorySummary> {
        private String categoryName;

        private BigDecimal avgChange;

        private String bestName;

        private BigDecimal bestChange;

        private String worstName;

        private BigDecimal worstChange;

        @Override
        public int compareTo(CategorySummary o) {
            return o.avgChange.compareTo(this.avgChange);
        }

        @Override
        public String toString(){
            return categoryName + " 平均涨幅: " + avgChange
                    + "[领涨品种: " + bestName + " " + bestChange+ "]"
                    + "[领跌品种: " + worstName + " " + worstChange+ "]";
        }
    }

}