package com.guru.future.biz.manager;

import com.guru.future.common.utils.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author j
 */
@Component
@Slf4j
public class FutureMailManager {
    @Resource
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void notifyOpenGap(String content) {
        // 构建一个邮件对象
        SimpleMailMessage message = MailUtil.buildSimpleMail("缺口报告", from, from, content);
        // 发送邮件
        javaMailSender.send(message);
    }
}
