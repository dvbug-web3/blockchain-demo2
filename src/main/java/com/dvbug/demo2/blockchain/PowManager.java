/*
 * Copyright (C) Vito
 * By Vito on 2022/1/11 11:57
 */
package com.dvbug.demo2.blockchain;

import com.dvbug.demo2.util.GsonUtil;
import com.dvbug.demo2.util.NetworkUtil;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PowManager {
    private final LocalCache cache;
    private final ChainManager chainManager;
    private final P2PManager p2pManager;

    // 挖矿
    public Block mine() {
        List<Transaction> tsaList = new ArrayList<>();
        tsaList.add(new Transaction("1", String.format("This block generated by node %s:%s", NetworkUtil.getLocalHostIp(), 1)));
        tsaList.add(new Transaction("2", "Blockchain level: " + (cache.getBlockchainLevel() + 1)));

        String newHash = "";
        int nonce = 0;
        long start = System.currentTimeMillis();
        //System.out.println("mine start");
        while (true) {
            newHash = chainManager.calculateHash(cache.getLatestBlock().getHash(), tsaList, nonce);

            if (chainManager.validHash(newHash)) {
                //System.out.printf("mine done, hash=%s, elapsed=%s%n", newHash, System.currentTimeMillis()-start);
                break;
            }

            //System.out.printf("the %d times invalid hash: %s%n", nonce, newHash);
            nonce++;
        }

        Block block = chainManager.createNewBlock(nonce, cache.getLatestBlock().getHash(), newHash, tsaList);

        Message msg = new Message(MessageType.RESPONSE_LATEST_BLOCK, GsonUtil.toJson(block));
        p2pManager.broadcast(GsonUtil.toJson(msg));

        return block;
    }
}
