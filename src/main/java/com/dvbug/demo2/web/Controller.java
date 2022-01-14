/*
 * Copyright (C) Vito
 * By Vito on 2022/1/11 10:50
 */
package com.dvbug.demo2.web;

import com.dvbug.demo2.blockchain.*;
import com.dvbug.demo2.p2p.node.MessageHandler;
import com.dvbug.demo2.util.GsonUtil;
import io.libp2p.core.PeerId;
import io.libp2p.core.multiformats.Multiaddr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.Map;

@RestController
public class Controller {
    @Autowired
    ChainManager chainManager;
    @Autowired
    PowManager powManager;
    @Autowired
    P2PManager p2pManager;
    @Autowired
    LocalCache cache;

    @GetMapping("/scan")
    @ResponseBody
    public ResponseEntity<?> scan() {
        return ResponseEntity.ok(cache.getBlockchain());
    }

    @GetMapping("/ping")
    @ResponseBody
    public DeferredResult<?> ping(@RequestParam("peer") String peerId) {
        DeferredResult<Object> result = new DeferredResult<>();
        p2pManager.setCallback(new MessageHandler() {
            @Override
            public void messageReceived(PeerId peerId, Multiaddr peerAddr, String message) {
                Map<String, Object> data = new HashMap<>();
                data.put("peerId", peerId);
                data.put("address", peerAddr);
                data.put("response", message);
                result.setResult(data);
            }
        });
        p2pManager.send(peerId, GsonUtil.toJson(new Message(MessageType.PING)));
        return result;
    }

    @GetMapping("/conn")
    @ResponseBody
    public DeferredResult<?> scan(@RequestParam("addr") String addr) {
        DeferredResult<Object> result = new DeferredResult<>();
        p2pManager.setCallback(new MessageHandler() {
            @Override
            public void onConnected(PeerId peerId, Multiaddr peerAddr) {
                Map<String, Object> data = new HashMap<>();
                data.put("peerId", peerId);
                data.put("address", peerAddr);
                result.setResult(data);
            }
        });
        p2pManager.conn(addr);
        return result;
    }

    @GetMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createGenesisBlock() {
        chainManager.createGenesisBlock();

        return ResponseEntity.ok(cache.getBlockchain());
    }

    @GetMapping("/mine")
    @ResponseBody
    public ResponseEntity<?> mine() {
        powManager.mine();
        return ResponseEntity.ok(cache.getBlockchain());
    }

    @GetMapping("/test")
    @ResponseBody
    public ResponseEntity<?> test() {
        chainManager.createGenesisBlock();
        powManager.mine();
        return ResponseEntity.ok(cache.getBlockchain());
    }
}
