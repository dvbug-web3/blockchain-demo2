/*
 * Copyright (C) Vito
 * By Vito on 2022/1/13 16:58
 */
package com.dvbug.demo2.p2p.protocol;

import io.libp2p.core.PeerId;
import io.libp2p.core.multiformats.Multiaddr;

public interface MessageCallback {
    void onMessage(PeerId peerId, Multiaddr peerAddr, String message);
}
