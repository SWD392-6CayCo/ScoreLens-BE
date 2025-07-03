package com.scorelens.Enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum WebSocketTopic {

    NOTI_LOGGING("/topic/logging_notification/"),
    NOTI_SHOT("/topic/shot_event/"),
    NOTI_NOTIFICATION("/topic/notification/"),
    ;


    String value;

    WebSocketTopic(String value) {
        this.value = value;
    }

}
