/*
 * Copyright (C) Vito
 * By Vito on 2022/1/11 17:04
 */
package com.dvbug.demo2.blockchain;

import com.dvbug.demo2.p2p.node.MessageHandler;
import com.dvbug.demo2.p2p.node.P2PNode;
import com.dvbug.demo2.util.GsonUtil;
import io.libp2p.core.PeerId;
import io.libp2p.core.multiformats.Multiaddr;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class P2PManager {
    final ChainManager chainManager;
    final LocalCache cache;
    final P2PNode p2pNode;

    @Getter
    @Setter
    private MessageHandler callback;

    @PostConstruct
    void init() {
        p2pNode.setMessageHandler(new MessageHandler() {
            @Override
            public void onConnected(PeerId peerId, Multiaddr addr) {
                log.debug("New peer {} connected", addr);
                if (null != callback) callback.onConnected(peerId, addr);
            }

            @Override
            public void onDisconnected(PeerId peerId, Multiaddr addr) {
                log.debug("Peer {} disconnected", addr);
                if (null != callback) callback.onDisconnected(peerId, addr);
            }

            @Override
            public void messageReceived(PeerId peerId, Multiaddr addr, String msg) {
                log.info("Received peer {} message: {}", addr, msg);
                if (null != callback) callback.messageReceived(peerId, addr, msg);
                Message message = GsonUtil.fromJson(msg, Message.class);
                if (null == message) return;

                switch (message.getType()) {
                    case QUERY_LATEST_BLOCK:
                        send(peerId.toBase58(), msgResponseLatestBlock());
                        break;
                    case RESPONSE_LATEST_BLOCK:
                        handleBlockResponse(message.getData());
                        break;
                    case QUERY_BLOCKCHAIN:
                        send(peerId.toBase58(), msgResponseBlockchain());
                        break;
                    case RESPONSE_BLOCKCHAIN:
                        handleBlockchainResponse(message.getData());
                        break;
                    case PING:
                        send(peerId.toBase58(), msgResponsePong());
                }
            }
        });
    }

    public void conn(String addr) {
        p2pNode.conn(addr);
    }

    private synchronized void handleBlockResponse(String msg) {
        Block latestBlockReceived = GsonUtil.fromJson(msg, Block.class);
        Block latestBlock = cache.getLatestBlock();
        if (null == latestBlockReceived) {
            return;
        }

        if (null != latestBlock) {
            if (latestBlockReceived.getIndex() > latestBlock.getIndex() + 1) {
                log.info("Querying main chain on net");
                broadcast(msgQueryBlockchain());
            } else if (latestBlockReceived.getIndex() > latestBlock.getIndex() &&
                    latestBlock.getHash().equals(latestBlockReceived.getPrevHash())) {
                log.info("Append received block to local chain");
                if (chainManager.addBlock(latestBlockReceived)) {
                    broadcast(msgResponseLatestBlock());
                }
            }
        } else {
            log.info("Querying main chain on net");
            broadcast(msgQueryBlockchain());
        }
    }

    private synchronized void handleBlockchainResponse(String msg) {
        List<Block> receivedChain = GsonUtil.fromJsonArray(msg, Block.class);
        if (receivedChain.isEmpty() || !chainManager.validChain(receivedChain)) return;

        receivedChain.sort(Comparator.comparingInt(Block::getIndex));

        Block latestBlockReceived = receivedChain.get(receivedChain.size() - 1);
        Block latestBlock = cache.getLatestBlock();

        if (null == latestBlock) {
            cache.replace(receivedChain);
            return;
        }

        if (latestBlockReceived.getIndex() > latestBlock.getIndex()) {
            if (latestBlock.getHash().equals(latestBlockReceived.getPrevHash())) {
                if (chainManager.addBlock(latestBlockReceived)) {
                    p2pNode.broadcast(msgResponseLatestBlock());
                }
            } else {
                cache.replace(receivedChain);
            }
        }
    }

    public String msgQueryLatestBlock() {
        return GsonUtil.toJson(new Message(MessageType.QUERY_LATEST_BLOCK));
    }

    public String msgQueryBlockchain() {
        return GsonUtil.toJson(new Message(MessageType.QUERY_BLOCKCHAIN));
    }

    public String msgResponseLatestBlock() {
        Message msg = new Message(MessageType.RESPONSE_LATEST_BLOCK, GsonUtil.toJson(cache.getLatestBlock()));
        return GsonUtil.toJson(msg);
    }

    public String msgResponseBlockchain() {
        Message msg = new Message(MessageType.RESPONSE_BLOCKCHAIN, GsonUtil.toJson(cache.getBlockchain()));
        return GsonUtil.toJson(msg);
    }

    public String msgResponsePong() {
        Message msg = new Message(MessageType.PONG);
        return GsonUtil.toJson(msg);
    }

    public void send(String peerId, String message) {
        log.info("Send message to {} peer, message={}", peerId, message);
        p2pNode.send(peerId, message);
    }

    public void broadcast(String message) {
        log.info("Broadcast message to p2p net, message={}", message);
        p2pNode.broadcast(message);
    }
}
