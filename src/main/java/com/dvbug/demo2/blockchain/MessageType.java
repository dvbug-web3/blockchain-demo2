/*
 * Copyright (C) Vito
 * By Vito on 2022/1/11 17:41
 */
package com.dvbug.demo2.blockchain;

import java.util.Arrays;
import java.util.Optional;

public enum MessageType {
    QUERY_LATEST_BLOCK(1),
    RESPONSE_LATEST_BLOCK(2),
    QUERY_BLOCKCHAIN(3),
    RESPONSE_BLOCKCHAIN(4),
    PING(98),
    PONG(99);

    int value;

    MessageType(int value) {
        this.value = value;
    }

    public static MessageType find(int value) {
        MessageType[] messageTypes = MessageType.values();
        Optional<MessageType> first = Arrays.stream(messageTypes).filter(c -> c.value == value).findFirst();
        return first.orElse(null);
    }
}
