package ru.globus.notificationsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification_history")
@NoArgsConstructor
@Getter
@Setter
public class NotificationHistory extends BaseEntity {

    @Column(name = "notification_id")
    private String notificationId;

    @Column(name = "channel")
    @Enumerated(EnumType.STRING)
    private Channel channel;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;


    @ManyToOne
    @JoinColumn(name = "notification_rule_id", referencedColumnName = "id")
    private NotificationRule notificationRule;
}
