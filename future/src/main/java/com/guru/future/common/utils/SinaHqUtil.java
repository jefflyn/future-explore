package com.guru.future.common.utils;


import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SinaHqUtil {
    public final static Integer HQ_LIST_SIZE = 29;

    public static List<String> parse2List(String content) {
        List<String> result = new ArrayList<>();
        if (Strings.isNullOrEmpty(content)) {
            return result;
        }
        List<String> components = Splitter.on("=").splitToList(content);
        if (!CollectionUtils.isEmpty(components) && components.size() > 1) {
            String part1 = components.get(0);
            String part2 = components.get(1);
            String code = part1.substring(part1.lastIndexOf("_") + 1);
            part2 = StringUtils.replace(part2, "\"", "").replace(";", "");
            List<String> part2Contents = Splitter.on(",").splitToList(part2);
            result.add(code);
            result.addAll(part2Contents);
        }

        return result;
    }
}
