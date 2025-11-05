package controller;

import dao.MayDietCoDAO;
import model.MayDietCo;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class SocketClientHandler extends Thread {
    // ⛔ ĐÃ XÓA MAP CỤC BỘ "connectedClients" TẠI ĐÂY

    private final Socket socket;
    private final MayDietCoDAO mayDAO = new MayDietCoDAO();
    private String maDinhDanh = null;

    public SocketClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("📥 Nhận từ thiết bị: " + line);

                // Lần đầu Jetson gửi mã nhận diện
                if (line.startsWith("MAY_ID:")) {
                    maDinhDanh = line.substring(7).trim();
                    
                    // ✅ SỬA LỖI: Gọi đến Manager trung tâm
                    ClientConnectionManager.addConnection(maDinhDanh, socket);
                    
                    System.out.println("✅ Máy " + maDinhDanh + " đã kết nối");
                    continue;
                }

                // Cập nhật trạng thái thiết bị
                if (line.startsWith("STATUS:")) {
                    String status = line.substring(7).trim();
                    mayDAO.updateTrangThai(maDinhDanh, status); // Hàm này giờ đã chạy đúng
                    System.out.println("💾 Cập nhật trạng thái " + maDinhDanh + " = " + status);
                }
            }

        } catch (IOException e) {
            System.out.println("⚠️ Mất kết nối với " + maDinhDanh + ": " + e.getMessage());
        } finally {
            if (maDinhDanh != null) {
                // ✅ SỬA LỖI: Gọi đến Manager trung tâm
                ClientConnectionManager.removeConnection(maDinhDanh);
                
                System.out.println("❌ Máy " + maDinhDanh + " đã ngắt kết nối.");
            }
        }
    }
}