package com.guru.future.common.utils;


import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * @author j
 */
@Slf4j
public class StringUtil {
    public static List<String> strToList(String str, String regex) {
        List<String> strList = Splitter.on(regex).splitToList(str).stream().map(String::toLowerCase).map(String::trim)
                .collect(Collectors.toList());
        Collections.sort(strList);
        return strList;
    }

    public static void main(String[] args) {
        String words = "fruit/Apple/orange/grape/watermelon/pear/banana/strawberry/kiwi fruit/peach/melon/mango";
        List<String> wordList = strToList(words, "/");
        System.out.println(wordList);
        ListIterator<String> wordsIter = wordList.listIterator();
        while (wordsIter.hasNext()) {
            String word = wordsIter.next();
            if (word.contains("a")) {
                System.out.println(word);
                wordsIter.remove();
            }
        }
        System.out.println(wordList);

    }
}