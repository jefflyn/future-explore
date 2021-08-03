package com.guru.future.biz.manager;

import com.guru.future.common.utils.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

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

    public void notifyOpenGapHtml(String content) throws MessagingException {
        this.sendHtmlMail("缺口报告", from, from, content);
    }

    public void sendHtmlMail(String subject, String from, String to, String content) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject(subject);
        helper.setFrom(from);
        helper.setTo(to);
//        helper.setCc("37xxxxx37@qq.com");
//        helper.setBcc("14xxxxx098@qq.com");
        helper.setSentDate(new Date());
        helper.setText(content, true);
        // 这里引入的是Template的Context
//        Context context = new Context();
//        // 设置模板中的变量
//        context.setVariable("username", "javaboy");
//        context.setVariable("num","000001");
//        context.setVariable("salary", "99999");
//        // 第一个参数为模板的名称
//        String process = templateEngine.process("hello.html", context);
//        // 第二个参数true表示这是一个html文本
//        helper.setText(process,true);

        javaMailSender.send(mimeMessage);
    }
}
