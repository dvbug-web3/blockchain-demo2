/*
 * Copyright (C) Vito
 * By Vito on 2022/1/12 11:34
 */
package com.dvbug.demo2.p2p.protocol;

import io.libp2p.core.multistream.StrictProtocolBinding;

public class MessageBinding extends StrictProtocolBinding<MessageController> {
    public MessageBinding(MessageProtocol protocol) {
        super(protocol.getAnnounce(), protocol);
    }
}
