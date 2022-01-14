/*
 * Copyright (C) Vito
 * By Vito on 2022/1/11 10:49
 */
package com.dvbug.demo2;

import com.dvbug.demo2.p2p.node.P2PNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RunApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RunApp.class, args);
        P2PNode p2pNode = context.getBean(P2PNode.class);
        p2pNode.start();
        Runtime.getRuntime().addShutdownHook(new Thread(p2pNode::stop, "P2PNodeShutdownHook"));
    }
}
