package com.squareup.okhttp.internal;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface Dns {
    public static final Dns DEFAULT;

    /* renamed from: com.squareup.okhttp.internal.Dns.1 */
    static class C00141 implements Dns {
        C00141() {
        }

        public InetAddress[] getAllByName(String host) throws UnknownHostException {
            return InetAddress.getAllByName(host);
        }
    }

    InetAddress[] getAllByName(String str) throws UnknownHostException;

    static {
        DEFAULT = new C00141();
    }
}