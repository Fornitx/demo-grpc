package com.example.utils;

import java.io.IOException;
import java.net.ServerSocket;

public class NetUtils {
    private NetUtils() {
    }

    public static int findFreePort() {
        try (var serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
