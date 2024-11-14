package com.sky.controller.notify;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sky.properties.WeChatProperties;
import com.sky.service.OrderService;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;


@RestController
@RequestMapping("/notify")
@Slf4j
public class PayNotifyController {
    @Resource
    private OrderService orderService;

    @Resource
    private WeChatProperties weChatProperties;

    @RequestMapping("/paySuccess")
    public void paySuccessNotify(HttpServletRequest request, HttpServletResponse response) throws IOException, GeneralSecurityException {
        String body=readData(request);
        log.info("支付回调成功：{}",body);

        String plainText=decryptData(body);
        log.info("解密后的文本：{}", plainText);

        JSONObject jsonObject = JSON.parseObject(plainText);
        String outTradeNo = jsonObject.getString("out_trade_no");//商户平台订单号
        String transactionId = jsonObject.getString("transaction_id");//微信支付交易号

        log.info("商户平台订单号：{}", outTradeNo);
        log.info("微信支付交易号：{}", transactionId);

        //业务处理，修改订单状态、来单提醒
        orderService.paySuccess(outTradeNo);

        //给微信响应
        responseToWeixin(response);

    }


    private String readData(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder result=new StringBuilder();
        String line;

        while((line=reader.readLine())!=null){
            if(result.length()>0){
                result.append(line);
            }
            result.append(line);
        }
        return result.toString();
    }


    private String decryptData(String data) throws GeneralSecurityException {
        JSONObject resultObject= JSON.parseObject(data);
        JSONObject resource= (JSONObject) resultObject.get("resource");
        String ciphertext = resource.getString("ciphertext");
        String nonce = resource.getString("nonce");
        String associatedData = resource.getString("associated_data");


        AesUtil aesUtil = new AesUtil(weChatProperties.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        String plainText = aesUtil.decryptToString(associatedData.getBytes(StandardCharsets.UTF_8),
                nonce.getBytes(StandardCharsets.UTF_8),
                ciphertext);

        return plainText;
    }

    private void responseToWeixin(HttpServletResponse response) throws IOException {
        response.setStatus(200);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("code", "SUCCESS");
        map.put("message", "SUCCESS");
        response.setHeader("Content-type", ContentType.APPLICATION_JSON.toString());
        response.getOutputStream().write(JSON.toJSONString(map).getBytes(StandardCharsets.UTF_8));

        response.flushBuffer();
    }
}
