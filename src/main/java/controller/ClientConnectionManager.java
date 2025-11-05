package controller;

// ✅ Các import mới đã được thêm
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class ClientConnectionManager {
    private static final Map<String, Socket> clientMap = new ConcurrentHashMap<>();

    // Thêm hoặc cập nhật socket thiết bị
    public static synchronized void addConnection(String maDinhDanh, Socket socket) {
        Socket old = clientMap.get(maDinhDanh);
        if (old != null && !old.isClosed()) {
            try {
                old.close();
                System.out.println("♻️ Đóng socket cũ của " + maDinhDanh);
            } catch (Exception ignored) {}
        }
        clientMap.put(maDinhDanh, socket);
        System.out.println("✅ Đã lưu socket cho " + maDinhDanh);
    }

    // Lấy socket theo mã định danh
    public static Socket getConnection(String maDinhDanh) {
        Socket socket = clientMap.get(maDinhDanh);
        if (socket != null && socket.isClosed()) {
            clientMap.remove(maDinhDanh);
            return null;
        }
        return socket;
    }

    // Xóa socket khi mất kết nối
    public static synchronized void removeConnection(String maDinhDanh) {
        Socket socket = clientMap.remove(maDinhDanh);
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
                System.out.println("🛑 Đã đóng socket cho " + maDinhDanh);
            } catch (Exception ignored) {}
        }
    }

    public static int getOnlineCount() {
        return clientMap.size();
    }

    // -------------------------------------------------------------------
    // ✅✅✅ HÀM MỚI QUAN TRỌNG ĐƯỢC THÊM VÀO ĐÂY ✅✅✅
    // -------------------------------------------------------------------
    /**
     * Gửi một chuỗi lệnh (ví dụ: "START" hoặc "STOP") đến một thiết bị cụ thể.
     * @param maDinhDanh Mã của thiết bị (ví dụ: "JETSON004")
     * @param command Lệnh cần gửi (ví dụ: "START")
     * @return true nếu gửi thành công, false nếu thiết bị offline hoặc có lỗi.
     */
    public static boolean sendCommand(String maDinhDanh, String command) {
        // Lấy socket đang "online" của thiết bị
        Socket socket = getConnection(maDinhDanh);

        if (socket != null) {
            try {
                // Dùng BufferedWriter để gửi lệnh.
                // Ký tự "\n" ở cuối là CỰC KỲ QUAN TRỌNG
                // để client Python (dùng readLine/recv) có thể nhận được.
                BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), "UTF-8")
                );
                
                writer.write(command + "\n"); // Ghi lệnh và thêm ký tự xuống dòng
                writer.flush(); // Đẩy lệnh đi ngay lập tức
                
                System.out.println("✅ Đã gửi lệnh '" + command + "' đến " + maDinhDanh);
                return true;

            } catch (IOException e) {
                // Thường xảy ra khi client bị mất mạng đột ngột
                System.err.println("⚠️ Lỗi khi gửi lệnh đến " + maDinhDanh + ": " + e.getMessage());
                // Nếu gửi lỗi, ta nên xóa socket hỏng này khỏi danh sách
                removeConnection(maDinhDanh); 
                return false;
            }
        } else {
            // Trường hợp này xảy ra khi Jetson không kết nối tới server
            System.out.println("ℹ️ Không thể gửi lệnh: Máy " + maDinhDanh + " đang offline (không có socket).");
            return false;
        }
    }
}