/*
 * Copyright (C) Vito
 * By Vito on 2022/1/12 11:37
 */
package com.dvbug.demo2.p2p.protocol;


public class Messager extends MessageBinding {
    public Messager(MessageCallback callback) {
        super(new MessageProtocol(callback));
    }
}
