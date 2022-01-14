/*
 * Copyright (C) Vito
 * By Vito on 2022/1/11 10:53
 */
package com.dvbug.demo2.blockchain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class Block implements Serializable {
    private int index;
    private String hash;
    private String prevHash;
    private long timestamp;
    private int nonce;
    private List<Transaction> transactions;
}
