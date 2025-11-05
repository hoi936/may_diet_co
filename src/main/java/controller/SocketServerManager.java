package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServerManager extends Thread {

    private static final int PORT = 5000;
    private ServerSocket serverSocket;

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("🌐 Socket Server đang chạy trên cổng " + PORT);

            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serverSocket.accept();
                String clientIp = clientSocket.getInetAddress().getHostAddress();
                System.out.println("📡 Thiết bị kết nối từ IP: " + clientIp);

                // Mỗi client có 1 luồng riêng
                SocketClientHandler handler = new SocketClientHandler(clientSocket);
                handler.start();
            }

        } catch (IOException e) {
            if (!Thread.currentThread().isInterrupted()) {
                System.out.println("⚠️ Lỗi Socket Server: " + e.getMessage());
            } else {
                System.out.println("🛑 Socket Server đã được dừng an toàn.");
            }
        }
    }

    // ✅ Dừng server khi Tomcat tắt
    public void shutdownServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("⚠️ Không thể đóng ServerSocket: " + e.getMessage());
        }
        interrupt();
    }
}