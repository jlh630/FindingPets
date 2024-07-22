package com.easyarch.FindingPetsSys.util;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import com.easyarch.FindingPetsSys.dto.CaptchaDto;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class CaptchaGeneratorUtil {

    /**
     *  生成图形验证码（纯数字）
     * @param len 数字位数
     * @return 验证码实体
     * @throws IOException 异常
     */
    @SneakyThrows
    public static CaptchaDto GraphCaptchaNum(int len) {
        if (len >= 10) {
            len = 6;
        }
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(300, 100, len, 3);

        RandomGenerator randomGenerator = new RandomGenerator("0123456789", len);
        captcha.setGenerator(randomGenerator);
        String code = captcha.getCode();
        BufferedImage image = captcha.getImage();
        // 将图片转为Base64编码
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
        return new CaptchaDto(base64Image, code);
    }
}
