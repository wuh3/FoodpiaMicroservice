package com.foodopia.notification.functions;

import com.foodopia.notification.dto.AccountsMsgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class MessageFunctions {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageFunctions.class);

    @Bean
    public Function<AccountsMsgDto, AccountsMsgDto> sendEmail() {
        return aacountsMsgDto -> {
            LOGGER.info("Send email with the details: " + aacountsMsgDto);
            return aacountsMsgDto;
        };
    }

    @Bean
    public Function<AccountsMsgDto, String> sendSms() {
        return aacountsMsgDto -> {
            LOGGER.info("Send sms with the details: " + aacountsMsgDto);
            return aacountsMsgDto.userId();
        };
    }
}
