package com.guru.future.common.entity.vo;

import com.guru.future.common.utils.NumberUtil;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FutureOverviewVO {
    private String totalAvgChangeStr;

    private String overviewDesc;

    private String histOverviewDesc;

    private List<CategorySummary> categorySummaryList;

    @Data
    public static class CategorySummary implements Comparable<CategorySummary> {
        private Integer sortNo;

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
    public String toString() {
        StringBuilder resultStr = new StringBuilder("Index: " + getTotalAvgChangeStr() + "【" + getOverviewDesc() + "】" + histOverviewDesc + "\n");
        if (CollectionUtils.isEmpty(getCategorySummaryList())) {
            return resultStr.toString();
        }
        for (FutureOverviewVO.CategorySummary categorySummary : getCategorySummaryList()) {
            resultStr.append(categorySummary.getSortNo()).append(".")
                    .append(String.format("%-4s", categorySummary.getCategoryName()))
                    .append(categorySummary.getAvgChangeStr())
                    .append(" 【")
                    .append(String.format("%s: ", categorySummary.getBestName()))
                    .append(categorySummary.getBestChange()).append(" ").append(NumberUtil.price2String(categorySummary.getBestPrice())).append(" | ")
                    .append(String.format("%s: ", categorySummary.getWorstName()))
                    .append(categorySummary.getWorstChange()).append(" ").append(NumberUtil.price2String(categorySummary.getWorstPrice()))
                    .append("】\n");
        }
        return resultStr.toString();
    }

}