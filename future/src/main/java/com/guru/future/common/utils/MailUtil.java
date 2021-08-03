package com.guru.future.common.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * @author j
 */
@Slf4j
public class MailUtil {
    public static SimpleMailMessage buildSimpleMail(String subject, String from, String to, String content) {
        // 构建一个邮件对象
        SimpleMailMessage message = new SimpleMailMessage();
        // 设置邮件主题
        message.setSubject(subject);
        // 设置邮件发送者，这个跟application.yml中设置的要一致
        message.setFrom(from);
        // 设置邮件接收者，可以有多个接收者，中间用逗号隔开，以下类似
        // message.setTo("10*****16@qq.com","12****32*qq.com");
        message.setTo(to);
        // 设置邮件抄送人，可以有多个抄送人
//        message.setCc("");
        // 设置隐秘抄送人，可以有多个
//        message.setBcc("");
        // 设置邮件发送日期
        message.setSentDate(new Date());
        // 设置邮件的正文
        message.setText(content);

        return message;
    }

    public static void main(String[] args) {

    }
}
