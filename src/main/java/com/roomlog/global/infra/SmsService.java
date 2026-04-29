package com.roomlog.global.infra;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsService {

    @Value("${solapi.api-key}")
    private String apiKey;

    @Value("${solapi.api-secret}")
    private String apiSecret;

    @Value("${solapi.sender}")
    private String sender;

    private DefaultMessageService messageService;

    @PostConstruct
    public void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.solapi.com");
    }

    public boolean send(String to, String text) {
        try {
            Message message = new Message();
            message.setFrom(sender);
            message.setTo(to);
            message.setText(text);
            messageService.sendOne(new SingleMessageSendingRequest(message));
            return true;
        } catch (Exception e) {
            log.error("SMS 전송 실패 to={}: {}", to, e.getMessage());
            return false;
        }
    }
}
