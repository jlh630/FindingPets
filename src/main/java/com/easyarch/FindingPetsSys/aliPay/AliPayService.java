//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.aliPay;

import com.alipay.api.AlipayApiException;
import com.easyarch.FindingPetsSys.exception.ValidatorException;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;

public interface AliPayService {
    String pay(String outTradeNo, BigDecimal totalAmount, String subject) throws AlipayApiException;

    String notify(HttpServletRequest request) throws ValidatorException, AlipayApiException;

    void refund(String outTradeNo, BigDecimal refundAmount, String reason);
}
