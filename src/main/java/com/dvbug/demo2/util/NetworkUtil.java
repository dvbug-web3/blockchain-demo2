/*
 * Copyright (C) Vito
 * By Vito on 2022/1/11 12:03
 */
package com.dvbug.demo2.util;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkUtil {

    public static String getLocalHostIp() {
        return getLocalHostAddress().getHostAddress();
    }

    public static InetAddress getLocalHostAddress() {
        try {
            Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces();
            while (faces.hasMoreElements()) {
                NetworkInterface face = faces.nextElement();
                Enumeration<InetAddress> addresses = face.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.isSiteLocalAddress() &&
                            !addr.isLoopbackAddress() &&
                            !(addr instanceof Inet6Address)) return addr;
                }
            }
        } catch (SocketException e) {
            throw new IllegalStateException("Couldn't find the local machine ip.", e);
        }
        throw new IllegalStateException("Couldn't find the local machine ip.");
    }
}
