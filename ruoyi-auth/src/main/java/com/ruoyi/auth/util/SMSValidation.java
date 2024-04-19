package com.ruoyi.auth.util;

import java.util.Random;
import java.util.regex.Pattern;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-15 10:14
 **/
public class SMSValidation {

    // 验证手机号格式是否合法
    public static boolean isValidPhoneNumber(String phoneNumber) {
        // 手机号正则表达式，中国大陆手机号的格式
        String phonePattern = "^(?:(?:\\+|00)86)?1[3-9]\\d{9}$";
        return Pattern.matches(phonePattern, phoneNumber);
    }


    // 生成6位随机数字验证码
    public static String generateVerificationCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10)); // 生成随机数字，范围在0到9之间
        }

        return sb.toString();
    }

}
