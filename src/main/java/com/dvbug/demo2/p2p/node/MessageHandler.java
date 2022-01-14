/*
 * Copyright (C) Vito
 * By Vito on 2022/1/12 12:37
 */
package com.dvbug.demo2.p2p.node;

import io.libp2p.core.PeerId;
import io.libp2p.core.multiformats.Multiaddr;

public interface MessageHandler {
    default void onConnected(PeerId peerId, Multiaddr peerAddr) {
    }

    default void onDisconnected(PeerId peerId, Multiaddr peerAddr) {
    }

    default void messageReceived(PeerId peerId, Multiaddr peerAddr, String message) {
    }
}
