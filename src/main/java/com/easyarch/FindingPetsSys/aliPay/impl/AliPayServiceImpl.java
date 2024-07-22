//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.aliPay.impl;

import cn.hutool.core.util.StrUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.easyarch.FindingPetsSys.aliPay.AliPayService;
import com.easyarch.FindingPetsSys.config.AliPayConfig;
import com.easyarch.FindingPetsSys.entity.Order;
import com.easyarch.FindingPetsSys.exception.ValidatorException;
import com.easyarch.FindingPetsSys.mapper.NoteFollowMapper;
import com.easyarch.FindingPetsSys.mapper.NoteMapper;
import com.easyarch.FindingPetsSys.mapper.OrderMapper;
import com.easyarch.FindingPetsSys.rocketmq.producer.RocketMQOrderProducerService;
import com.easyarch.FindingPetsSys.util.UserContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AliPayServiceImpl implements AliPayService {

    @Autowired
    private AliPayConfig aliPayConfig;
    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private NoteFollowMapper noteFollowMapper;
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private RocketMQOrderProducerService orderProducerService;

    /**
     * 调用aliPay 第三方接口
     *
     * @param outTradeNo  Alipay订单号（唯一）
     * @param totalAmount 金额
     * @param subject
     * @return 页面
     * @throws AlipayApiException 失败
     */
    public String pay(String outTradeNo, BigDecimal totalAmount, String subject) throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        //支付宝订单号、金额、回调
        model.setOutTradeNo(outTradeNo);
        model.setTotalAmount(String.valueOf(totalAmount));
        model.setSubject(subject);
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        request.setNotifyUrl(aliPayConfig.notifyUrl);
        request.setBizModel(model);
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "POST");
        String pageRedirectionData = response.getBody();
        if (response.isSuccess()) {
            log.info("User[{}] create Alipay connect Success", UserContext.getUser().getUserId());
            return pageRedirectionData;
        } else {
            log.info("User[{}] create Alipay connect Failed", UserContext.getUser().getUserId());
            return null;
        }
    }

    /**
     * 用于aliPay 第三方支付成功后调用的接口
     *
     * @param request http请求
     * @return .
     * @throws ValidatorException 参数异常
     * @throws AlipayApiException 签名异常
     */
    @Transactional
    public String notify(HttpServletRequest request) throws ValidatorException, AlipayApiException {
        String tradeStatus = request.getParameter("trade_status");
        if (StrUtil.hasEmpty(tradeStatus)) {
            throw new ValidatorException("错误参数");
        } else {
            if (tradeStatus.equals("TRADE_SUCCESS")) {
                Map<String, String> params = new HashMap<>();
                Map<String, String[]> requestParams = request.getParameterMap();

                for (String param : requestParams.keySet()) {
                    params.put(param, request.getParameter(param));
                }
                String content;
                String sign = params.get("sign");
                content = AlipaySignature.getSignCheckContentV1(params);
                //验签
                boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, aliPayConfig.alipayPublicKey, "UTF-8");
                if (checkSignature) {
                    log.info("Alibaba has executed a callback");
                    //拿到订单id
                    Long orderId = Long.valueOf((params.get("out_trade_no")).split("-")[0]);
                    Order order = orderMapper.queryOrderByOrderId(orderId);
                    Byte orderStatus = order.getStatus();
                    //再次验证防止订单是否是需要支付的状态
                    if (orderStatus != 0 && orderStatus != 1) {
                        refund(params.get("out_trade_no"), new BigDecimal(params.get("total_amount")), "订单异常");
                        return "error";
                    }
                    //定金/尾款
                    if (orderStatus == 0) {
                        //1.修改订单状态
                        //2.帖子对外可见
                        order.setStatus((byte) 3);
                        orderMapper.updateOrderStatus(order);
                        noteMapper.updateNoteVisibility(order.getNoteId(),true);
                        orderProducerService.sendDelayOrderFinish(orderId);
                        log.info("Order[{}] success deposit", orderId);
                    } else {
                        //1.修改订单状态
                        //2.修改帖子对外不可见
                        //3.修改关注状态
                        order.setStatus((byte) 2);
                        orderMapper.updateOrderStatus(order);
                        noteMapper.updateNoteVisibility(order.getNoteId(),false);
                        noteFollowMapper.updateNoteFollowStatusByNoteIdAndStatus(order.getNoteId(), (byte) 1, (byte) 2);
                        log.info("Order[{}] success finalPayment", orderId);
                    }
                }
            }

            return "success";
        }
    }

    /**
     * 调用aliPay 第三方 退款接口
     *
     * @param outTradeNo   alipay 订单号
     * @param refundAmount 退款金额
     * @param reason       退款原因
     */
    public void refund(String outTradeNo, BigDecimal refundAmount, String reason) {
        int maxRetries = 3;
        int retryCount = 0;
        AlipayTradeRefundResponse response = null;

        while (retryCount < maxRetries) {
            try {
                AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
                AlipayTradeRefundModel model = new AlipayTradeRefundModel();
                model.setOutTradeNo(outTradeNo);
                model.setRefundAmount(refundAmount.toString());
                model.setRefundReason(reason);
                request.setBizModel(model);
                response = alipayClient.execute(request);
                if (response.isSuccess()) {
                    log.info("Order[{}] refund success", response.getOutTradeNo());
                    return;
                }
                ++retryCount;
                Thread.sleep(1000L * (long) (retryCount + 1));
                log.warn("Order[{}] refund failed try again {}", response.getOutTradeNo(), retryCount);
            } catch (InterruptedException | AlipayApiException e) {
                throw new RuntimeException(e);
            }
        }

        log.error("Order[{}]refund failed", response.getOutTradeNo());
    }
}
