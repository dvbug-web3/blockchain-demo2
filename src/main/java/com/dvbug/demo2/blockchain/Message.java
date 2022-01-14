/*
 * Copyright (C) Vito
 * By Vito on 2022/1/11 17:06
 */
package com.dvbug.demo2.blockchain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Message implements Serializable {
    private final MessageType type;
    private final String data;

    public Message(MessageType type) {
        this(type, null);
    }

    public Message(MessageType type, String data) {
        this.type = type;
        this.data = data;
    }
}
