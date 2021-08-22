package com.guru.future.biz.manager.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class FutureSmsManager {

    public void sendPriceFlashMsg(String code, String name, BigDecimal price, BigDecimal suggest) {
        try {
//            params = [name, price, suggest]
//            #签名参数未提供或者为空时，会使用默认签名发送短信 参数长度<=12
//            #if code in send_counter.keys() and send_counter[ code] >3:
//            #print('%s提醒超过3次，今天不再提醒！')
//        #return
//
//            if redis_client.get('MSG_COUNT_' + name) is not None and int(redis_client.get('MSG_COUNT_' + name)) >= 2:
//            print("该提示超过限制，不再发送信息!")
//            return
//                    result = ssender.send_with_param(86, to, 849005, params, sign = sms_sign, extend = "", ext = "")
//            redis_client.incr('MSG_COUNT_' + name)
//            redis_client.expire('MSG_COUNT_' + name, date_const.ONE_MINUTE * 30)
//            #send_counter[code] += 1
        } catch (Exception e) {
            log.error("sendPriceFlashMsg error={}", e);
        }
    }

}
