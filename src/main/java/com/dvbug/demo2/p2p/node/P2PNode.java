/*
 * Copyright (C) Vito
 * By Vito on 2022/1/13 17:04
 */
package com.dvbug.demo2.p2p.node;

import com.dvbug.demo2.p2p.protocol.MessageController;
import com.dvbug.demo2.p2p.protocol.Messager;
import com.dvbug.demo2.util.NetworkUtil;
import io.libp2p.core.Discoverer;
import io.libp2p.core.Host;
import io.libp2p.core.PeerId;
import io.libp2p.core.StreamPromise;
import io.libp2p.core.dsl.HostBuilder;
import io.libp2p.core.multiformats.Multiaddr;
import io.libp2p.discovery.MDnsDiscovery;
import kotlin.Pair;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class P2PNode {
    @Data
    @AllArgsConstructor
    private static class PeerController {
        private String name;
        private MessageController controller;
    }

    private Discoverer peerFinder;
    private final Set<PeerId> knownNodes = new HashSet<>();
    @Getter
    private final Map<PeerId, PeerController> peers = new HashMap<>();
    private final Host host;
    private final InetAddress privateAddress;
    private final Messager messager;
    @Getter
    @Setter
    private MessageHandler messageHandler;

    public P2PNode(int port) {
        this(port, null);
    }

    public P2PNode(int port, MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        privateAddress = NetworkUtil.getLocalHostAddress();
        messager = new Messager(this::messageReceived);
        host = new HostBuilder()
                .protocol(messager)
                .listen(String.format("/ip4/%s/tcp/%d", privateAddress.getHostAddress(), Math.max(port, 0)))
                .build();
    }

    @SneakyThrows
    public void conn(String addr) {
        Multiaddr multiaddr = Multiaddr.fromString(addr);
        Pair<PeerId, Multiaddr> peerIdAndAddr = multiaddr.toPeerIdAndAddr();
        peerFound(peerIdAndAddr.getFirst(), multiaddr);
    }

    public void start() {
        host.start().thenAccept(unused -> {
            host.listenAddresses().forEach(addr -> {
                log.info("Node listened on {}", addr.toString());
            });

            peerFinder = new MDnsDiscovery(host,
                    MDnsDiscovery.Companion.getServiceTag(),
                    MDnsDiscovery.Companion.getQueryInterval(),
                    privateAddress);
            peerFinder.getNewPeerFoundListeners().add(info -> {
                log.info("new peer found: {}", info);
                peerFound(info.getPeerId(), info.getAddresses().get(0));
                return null;
            });
            peerFinder.start().thenAccept(unused2 -> {
                log.info("peer finder started");
            });
        });
    }

    public void broadcast(String message) {
        if (peers.isEmpty()) {
            log.warn("No peers on {}", host.getPeerId().toBase58());
        }

        peers.forEach((peerId, friend) -> {
            friend.getController().send(message);
        });
    }

    public void send(String peerId, String message) {
        peers.computeIfPresent(PeerId.fromBase58(peerId), (pid, friend) -> {
            friend.getController().send(message);
            return friend;
        });
    }

    public void stop() {
        peerFinder.stop();
        host.stop();
    }

    private void messageReceived(PeerId peerId, Multiaddr peerAddr, String message) {
        PeerController peerController = peers.get(peerId);
        if (null == peerController) {
            conn(peerAddr.toString());
        }
        if (!"/hi".equals(message)) {
            messageHandler.messageReceived(peerId, peerAddr, message);
        }
    }

    private void peerFound(PeerId peerId, Multiaddr addr) {
        if (peerId == host.getPeerId() || knownNodes.contains(peerId)) return;

        StreamPromise<? extends MessageController> chat =
                new Messager(this::messageReceived).dial(host, peerId, addr);

        chat.getStream().thenAccept(s -> {
            knownNodes.add(peerId);
            messageHandler.onConnected(peerId, addr);

            s.closeFuture().thenAccept(unit -> {
                peers.remove(peerId);
                knownNodes.remove(peerId);
                messageHandler.onDisconnected(peerId, addr);
            });
        });


        chat.getController().thenAccept(c -> {
            c.send("/hi");
            peers.put(peerId, new PeerController(peerId.toBase58(), c));
        });
    }
}
