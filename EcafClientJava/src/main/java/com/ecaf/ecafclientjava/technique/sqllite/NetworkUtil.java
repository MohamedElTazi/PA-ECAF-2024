package com.ecaf.ecafclientjava.technique.sqllite;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkUtil {
    public static boolean isOnline() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("www.google.com", 80), 2000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
