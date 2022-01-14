/*
 * Copyright (C) Vito
 * By Vito on 2022/1/11 11:07
 */
package com.dvbug.demo2.blockchain;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LocalCache {
    private List<Block> blockchain;

    public LocalCache() {
        blockchain = new CopyOnWriteArrayList<>();
    }

    public void add(Block block, boolean genesis) {
        if (genesis && !blockchain.isEmpty()) {
            return;
        }
        blockchain.add(block);
    }

    public List<Block> getBlockchain() {
        return ImmutableList.copyOf(blockchain);
    }

    public void replace(List<Block> chain) {
        blockchain = new ArrayList<>(chain);
    }

    public Block getLatestBlock() {
        return blockchain.size() > 0 ? blockchain.get(blockchain.size() - 1) : null;
    }

    public int getBlockchainLevel() {
        return blockchain.size();
    }
}
