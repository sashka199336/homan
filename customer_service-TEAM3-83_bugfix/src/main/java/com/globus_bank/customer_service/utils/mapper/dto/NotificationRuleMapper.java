package com.globus_bank.customer_service.utils.mapper.dto;

import com.globus_bank.customer_service.dto.common.ContactsDto;
import com.globus_bank.customer_service.dto.kafka.NotificationRuleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationRuleMapper {
    
    @Mapping(source = "customerId", target = "clientId")
    @Mapping(source = "phoneNumber", target = "phone")
    @Mapping(target = "preferNotificationChannels", expression = "java(java.util.Set.of(contactsDto.getChannel()))")
    NotificationRuleDto toNotificationRuleDto(ContactsDto contactsDto);
}
