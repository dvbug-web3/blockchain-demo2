/*
 * Copyright (C) Vito
 * By Vito on 2022/1/13 16:57
 */
package com.dvbug.demo2.p2p.protocol;

import io.libp2p.core.P2PChannel;
import io.libp2p.core.PeerId;
import io.libp2p.core.Stream;
import io.libp2p.core.multiformats.Multiaddr;
import io.libp2p.protocol.ProtocolHandler;
import io.libp2p.protocol.ProtocolMessageHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

public class MessageProtocol extends ProtocolHandler<MessageController> {
    private final String announce = "/p2p/chat/3.1.0";
    private final MessageCallback callback;
    private final boolean useLimit;

    public MessageProtocol(MessageCallback callback) {
        super(-1, -1);
        this.useLimit = false;
        this.callback = callback;
    }

    public MessageProtocol(long initiatorTrafficLimit, long responderTrafficLimit, MessageCallback callback) {
        super(initiatorTrafficLimit, responderTrafficLimit);
        this.useLimit = true;
        this.callback = callback;
    }

    public String getAnnounce() {
        return announce;
    }

    @Override
    public CompletableFuture<MessageController> initChannel(P2PChannel ch) {
        if (useLimit) {
            return super.initChannel(ch);
        } else {
            Stream stream = (Stream) ch;
            initProtocolStream(stream);

            if (stream.isInitiator()) {
                return onStartInitiator(stream);
            } else {
                return onStartResponder(stream);
            }
        }
    }

    @Override
    protected CompletableFuture<MessageController> onStartInitiator(Stream stream) {
        return onStart(stream);
    }

    @Override
    protected CompletableFuture<MessageController> onStartResponder(Stream stream) {
        return onStart(stream);
    }

    private CompletableFuture<MessageController> onStart(Stream stream) {
        CompletableFuture<Void> ready = new CompletableFuture<>();
        MessageResponder messageResponder = new MessageResponder(callback, ready);
        stream.pushHandler(messageResponder);
        return ready.thenApply(unused -> messageResponder);
    }

    public static class MessageResponder implements MessageController, ProtocolMessageHandler<ByteBuf> {
        private Stream stream;
        private final MessageCallback callback;
        private final CompletableFuture<Void> ready;

        public MessageResponder(MessageCallback callback, CompletableFuture<Void> ready) {
            this.callback = callback;
            this.ready = ready;
        }

        @Override
        public void fireMessage(Stream stream, Object o) {
            onMessage(stream, (ByteBuf) o);
        }

        @Override
        public void onActivated(Stream stream) {
            this.stream = stream;
            ready.complete(null);
        }

        @Override
        public void onClosed(Stream stream) {

        }

        @Override
        public void onException(Throwable throwable) {

        }

        @Override
        public void onMessage(Stream stream, ByteBuf message) {
            String msgStr = message.toString(Charset.defaultCharset());
            PeerId peerId = stream.remotePeerId();
            Multiaddr multiaddr = new Multiaddr(stream.getConnection().remoteAddress(), peerId);
            callback.onMessage(peerId, multiaddr, msgStr);
        }

        @Override
        public void send(String message) {
            byte[] data = message.getBytes(Charset.defaultCharset());
            stream.writeAndFlush(Unpooled.wrappedBuffer(data));
        }
    }
}
