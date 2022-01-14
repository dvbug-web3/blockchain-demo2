/*
 * Copyright (C) Vito
 * By Vito on 2022/1/11 15:14
 */
package com.dvbug.demo2.conf;

import com.dvbug.demo2.blockchain.ChainManager;
import com.dvbug.demo2.blockchain.LocalCache;
import com.dvbug.demo2.blockchain.P2PManager;
import com.dvbug.demo2.blockchain.PowManager;
import com.dvbug.demo2.p2p.node.P2PNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${server.port}")
    int port;

    @Value("${p2p.port}")
    int p2pPort;

    @Value("${blockchain.difficulty:4}")
    int difficulty;

    @Bean
    LocalCache localCache() {
        return new LocalCache();
    }

    @Bean
    ChainManager chainManager(LocalCache cache) {
        return new ChainManager(cache);
    }

    @Bean
    PowManager powManager(LocalCache cache, ChainManager chainManager, P2PManager p2pManager) {
        return new PowManager(cache, chainManager, p2pManager);
    }

    @Bean
    P2PNode p2PNode() {
        return new P2PNode(p2pPort);
    }

    @Bean
    P2PManager p2PManager(LocalCache cache, ChainManager chainManager, P2PNode p2pNode) {
        return new P2PManager(chainManager, cache, p2pNode);
    }
}
