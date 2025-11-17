package ru.globus.notificationsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.globus.notificationsystem.dto.request.*;
import ru.globus.notificationsystem.entity.Channel;
import ru.globus.notificationsystem.entity.NotificationRule;
import ru.globus.notificationsystem.kafka.producer.NotificationProducer;
import ru.globus.notificationsystem.service.NotificationHistoryService;
import ru.globus.notificationsystem.service.NotificationRuleService;
import ru.globus.notificationsystem.service.NotificationService;
import ru.globus.notificationsystem.util.mapper.ChannelNotificationDtoMapper;
import ru.globus.notificationsystem.util.validator.ChannelValidationRequest;
import ru.globus.notificationsystem.util.validator.NotificationValidator;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationProducer notificationProducer;
    private final NotificationRuleService notificationRuleService;
    private final NotificationHistoryService notificationHistoryService;
    private final NotificationValidator validator;
    private final ChannelNotificationDtoMapper channelNotificationDtoMapper;

    @Override
    public void process(NotificationDto notificationDto, String notificationId) {
        log.info("Processing notification with ID: {}", notificationDto.getNotificationId());
        NotificationRule notificationRule = notificationRuleService
                .getNotificationRuleByClientId(notificationDto.getClientId());

        processByChannel(notificationDto, notificationRule);
    }

    private void processByChannel(NotificationDto notificationDto,
                                  NotificationRule notificationRule) {
        log.info("Processing notification by channels with ID: {}", notificationDto.getNotificationId());
        Set<Channel> channelsToProcess = getChannelsToProcess(notificationRule);
        for (Channel channel : channelsToProcess) {
            validator.validateChannelRequirements(new ChannelValidationRequest(channel, notificationRule));
            sendToChannel(notificationDto, notificationRule, channel);
        }
    }

    private Set<Channel> getChannelsToProcess(NotificationRule notificationRule) {
        //TODO Определить источник получения sessionId для WEB уведомления
        Set<Channel> channelSet = new HashSet<>();
        if (notificationRule.isEmailActive()) {
            channelSet.add(Channel.EMAIL);
        }
        if (notificationRule.isSmsActive()) {
            channelSet.add(Channel.SMS);
        }
        if (notificationRule.isPushActive()) {
            channelSet.add(Channel.PUSH);
        }
        return channelSet;
    }

    private void sendToChannel(NotificationDto notificationDto,
                               NotificationRule notificationRule,
                               Channel channel) {
        log.info("sendToChannel:  channel {}, notificationID: {}", channel, notificationDto.getNotificationId());
        notificationProducer.sendNotification(
                createChannelNotificationDto(notificationDto, notificationRule, channel),
                channel);
        notificationHistoryService.save(notificationDto, notificationRule, channel);
    }

    private ChannelNotificationDto createChannelNotificationDto(NotificationDto notificationDto,
                                                                NotificationRule notificationRule,
                                                                Channel channel) {
        return switch (channel) {
            case EMAIL -> channelNotificationDtoMapper.toEmailNotificationDto(notificationDto, notificationRule);
            case SMS -> channelNotificationDtoMapper.toSmsNotificationDto(notificationDto, notificationRule);
            case PUSH -> channelNotificationDtoMapper.toPushNotificationDto(notificationDto, notificationRule);
            case WEB -> channelNotificationDtoMapper.toWebNotificationDto(notificationDto, notificationRule);
        };

    }
}
