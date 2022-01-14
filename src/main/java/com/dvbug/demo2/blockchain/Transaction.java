/*
 * Copyright (C) Vito
 * By Vito on 2022/1/11 10:58
 */
package com.dvbug.demo2.blockchain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@RequiredArgsConstructor
public class Transaction implements Serializable {
    private final String id;
    private final String bizData;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
