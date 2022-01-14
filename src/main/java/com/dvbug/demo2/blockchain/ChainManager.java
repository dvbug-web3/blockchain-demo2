/*
 * Copyright (C) Vito
 * By Vito on 2022/1/11 11:16
 */
package com.dvbug.demo2.blockchain;

import com.dvbug.demo2.util.CryptoUtil;
import com.dvbug.demo2.util.GsonUtil;
import kotlin.Pair;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ChainManager {
    private final LocalCache cache;

    public Block createGenesisBlock() {
        Block genesisBlock = new Block()
                .setIndex(1)
                .setTimestamp(System.currentTimeMillis())
                .setNonce(1);

        List<Transaction> tsaList = new ArrayList<>();
        tsaList.add(new Transaction("1", "This is genesis block"));
        tsaList.add(new Transaction("2", "Blockchain level: 1"));

        genesisBlock.setTransactions(tsaList);
        genesisBlock.setHash(calculateHash("", tsaList, 1));
        cache.add(genesisBlock, true);

        return genesisBlock;
    }

    public Block createNewBlock(int nonce, String prevHash, String hash, List<Transaction> transactions) {
        Block block = new Block()
                .setIndex(cache.getBlockchainLevel() + 1)
                .setTimestamp(System.currentTimeMillis())
                .setTransactions(transactions)
                .setNonce(nonce)
                .setPrevHash(prevHash)
                .setHash(hash);
        if (addBlock(block)) {
            return block;
        }
        return null;
    }

    public boolean addBlock(Block block) {
        if (validNewBlock(block, cache.getLatestBlock())) {
            cache.add(block, false);
            return true;
        }
        return false;
    }

    public static boolean validNewBlock(Block newBlock, Block prevBlock) {
        if (!prevBlock.getHash().equals(newBlock.getPrevHash())) {
            System.err.println("invalid prev hash on new block: " + newBlock);
            return false;
        }

        String hash = calculateHash(newBlock.getPrevHash(), newBlock.getTransactions(), newBlock.getNonce());
        if (!hash.equals(newBlock.getHash())) {
            System.err.println("invalid hash on new block: " + newBlock);
            return false;
        }

        if (!validHash(newBlock.getHash())) {
            System.err.println("invalid hash (not started with \"0000\") on new block: " + newBlock);
            return false;
        }

        return true;
    }

    public static boolean validHash(String hash) {
        return hash.startsWith("0000");
    }

    public boolean validChain(List<Block> chain) {
        if (chain.isEmpty() || null == chain.get(0) || chain.get(0).getIndex() != 1) return false;

        Pair<Block, Boolean> init = new Pair<>(null, true);
        return chain.stream().reduce(init, (prev, curr) -> {
            if (prev == init) return new Pair<>(curr, true);
            if (!prev.getSecond()) return new Pair<>(curr, false);

            return new Pair<>(curr, validNewBlock(curr, prev.getFirst()));
        }, (p, c) -> null).getSecond();
    }

    public static String calculateHash(String prevHash, List<Transaction> transactions, int nonce) {
        return CryptoUtil.sha256(prevHash + GsonUtil.toJson(transactions) + nonce);
    }
}
