//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.controller;

import com.alipay.api.AlipayApiException;
import com.easyarch.FindingPetsSys.aliPay.AliPayService;
import com.easyarch.FindingPetsSys.dto.OrderDetailDto;
import com.easyarch.FindingPetsSys.dto.Result;
import com.easyarch.FindingPetsSys.exception.NotFoundException;
import com.easyarch.FindingPetsSys.exception.OperationFailedException;
import com.easyarch.FindingPetsSys.exception.ServiceException;
import com.easyarch.FindingPetsSys.exception.ValidatorException;
import com.easyarch.FindingPetsSys.service.OrderService;
import com.easyarch.FindingPetsSys.util.UserContext;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping({"/orders"})
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private AliPayService aliPayService;

    public OrderController() {
    }

    @PostMapping({"/{orderId}/payment"})
    public Result<String> orderPayment(@PathVariable Long orderId) throws AlipayApiException, OperationFailedException, ServiceException, NotFoundException {
        return Result.created("调用支付宝接口...", orderService.payment( orderId,UserContext.getUser().getUserId()));
    }

    @PostMapping({"/notify"})
    public Result<String> notify(HttpServletRequest request) throws ValidatorException, AlipayApiException {
        return Result.success(aliPayService.notify(request));
    }

    @GetMapping({""})
    public Result<PageInfo<OrderDetailDto>> pageOrderList(@RequestParam("offset")
                                                          @Range(min = 1, message = "Number must be greater than {min}") int offset,
                                                          @RequestParam("limit")
                                                          @Range(min = 1, max = 15, message = "Number must be between {min} and {max}") int limit) {
        return Result.success(orderService.pageSelectOrders(UserContext.getUser().getUserId(), offset, limit));
    }
}
