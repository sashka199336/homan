package ru.globus.notificationsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification_rule")
@NoArgsConstructor
@Getter
@Setter
public class NotificationRule extends BaseEntity {

    @Column(name="client_id", unique = true)
    private String clientId;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "device_token")
    private String deviceToken;

    @Column(name = "is_email_active")
    private boolean isEmailActive;

    @Column(name = "is_sms_active")
    private boolean isSmsActive;

    @Column(name = "is_push_active")
    private boolean isPushActive;
}
