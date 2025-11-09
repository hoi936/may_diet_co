package controller;

// ✅ THÊM CÁC IMPORT MỚI
import dao.MayDietCoDAO;
import dao.PhienHoatDongDAO; 
import dao.LichSuCoDAO;       
import model.LichSuCo;        

import java.io.*;
import java.net.Socket;
// Bỏ import ConcurrentHashMap nếu không dùng ở đây

public class SocketClientHandler extends Thread {
    
    private final Socket socket;
    private final MayDietCoDAO mayDAO = new MayDietCoDAO();
    // ✅ KHỞI TẠO CÁC DAO MỚI
    private final PhienHoatDongDAO phienDAO = new PhienHoatDongDAO(); 
    private final LichSuCoDAO lichSuDAO = new LichSuCoDAO();       
    
    private String maDinhDanh = null; // Mã định danh của máy này (JETSON004, ...)

    public SocketClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // Dùng try-with-resources để tự động đóng BufferedReader
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                
                // Bỏ qua các dòng trống
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                System.out.println("📥 Nhận từ [" + (maDinhDanh != null ? maDinhDanh : socket.getInetAddress()) + "]: " + line);

                // ✅ LOGIC XÁC THỰC: Luôn phải là lệnh đầu tiên
                if (maDinhDanh == null) {
                    if (line.startsWith("MAY_ID:")) {
                        maDinhDanh = line.substring(7).trim();
                        ClientConnectionManager.addConnection(maDinhDanh, socket);
                        System.out.println("✅ Máy " + maDinhDanh + " đã xác thực và kết nối.");
                    } else {
                        // Nếu lệnh đầu tiên không phải MAY_ID, ngắt kết nối
                        System.err.println("⚠️ Lỗi: Client chưa xác thực. Đóng kết nối.");
                        break; 
                    }
                    continue; // Đọc dòng tiếp theo
                }

                // ------ Xử lý các lệnh sau khi đã xác thực ------

                if (line.startsWith("STATUS:")) {
                    // Lệnh: "STATUS:<trang_thai_may>"
                    // Ví dụ: "STATUS:DANG_DI_CHUYEN"
                    String status = line.substring(7).trim();
                    mayDAO.updateTrangThai(maDinhDanh, status);
                    System.out.println("💾 Cập nhật trạng thái " + maDinhDanh + " = " + status);
                
                } else if (line.startsWith("WEED:")) {
                    // ✅ LOGIC MỚI: XỬ LÝ DỮ LIỆU CỎ
                    // Lệnh: "WEED:<ma_phien>:<vi_tri>:<so_co_diet>:<duong_dan_anh>"
                    // Ví dụ: "WEED:78:12.3,-45.6:1:img/anh_123.jpg"
                    try {
                        String[] parts = line.split(":", 5); // Tách thành 5 phần
                        int maPhien = Integer.parseInt(parts[1]);
                        String viTri = parts[2];
                        int soCoDiet = Integer.parseInt(parts[3]);
                        String duongDanAnh = parts[4];
                        
                        LichSuCo lsc = new LichSuCo();
                        lsc.setMaDinhDanh(maDinhDanh); // Lấy từ biến của Handler
                        lsc.setMaPhien(maPhien);
                        lsc.setViTri(viTri);
                        lsc.setSoCoDiet(soCoDiet);
                        lsc.setSoCoPhatHien(1); // Mặc định 1 lần phát hiện
                        lsc.setDuongDanAnh(duongDanAnh);
                        
                        lichSuDAO.insert(lsc); // Lưu vào DB (từ Bước 3)
                        System.out.println("💾 Đã lưu Lịch sử cỏ cho phiên " + maPhien);

                    } catch (Exception e) {
                        System.err.println("⚠️ Lỗi phân tích lệnh WEED: " + line + " | Lỗi: " + e.getMessage());
                    }
                
                } else if (line.startsWith("COMPLETED:")) {
                    // ✅ LOGIC MỚI: MÁY TỰ HOÀN THÀNH
                    // Lệnh: "COMPLETED:<distance>"
                    // Ví dụ: "COMPLETED:20.1"
                    try {
                        float distance = Float.parseFloat(line.substring(10).trim());
                        System.out.println("🏁 Máy " + maDinhDanh + " tự hoàn thành: " + distance + "m");
                        
                        // Cập nhật CSDL: Dừng máy VÀ đóng phiên (từ Bước 3)
                        mayDAO.updateTrangThai(maDinhDanh, "NGUNG_HOAT_DONG");
                        phienDAO.stopPhien(maDinhDanh, distance); 
                    
                    } catch (Exception e) {
                        System.err.println("⚠️ Lỗi phân tích lệnh COMPLETED: " + line + " | Lỗi: " + e.getMessage());
                    }

                } else if (line.startsWith("STOPPED:")) {
                    // ✅ LOGIC MỚI: MÁY BỊ DỪNG THỦ CÔNG
                    // Lệnh: "STOPPED:<distance>"
                    // Ví dụ: "STOPPED:15.5"
                    try {
                        float distance = Float.parseFloat(line.substring(8).trim());
                        System.out.println("🛑 Máy " + maDinhDanh + " bị dừng thủ công: " + distance + "m");
                        
                        // Cập nhật CSDL: Dừng máy VÀ đóng phiên (từ Bước 3)
                        mayDAO.updateTrangThai(maDinhDanh, "NGUNG_HOAT_DONG");
                        phienDAO.stopPhien(maDinhDanh, distance);

                    } catch (Exception e) {
                        System.err.println("⚠️ Lỗi phân tích lệnh STOPPED: " + line + " | Lỗi: " + e.getMessage());
                    }
                }
            } // Hết vòng lặp while

        } catch (IOException e) {
            // Lỗi này xảy ra khi client ngắt kết nối đột ngột
            System.out.println("⚠️ Mất kết nối với " + (maDinhDanh != null ? maDinhDanh : "client"));
        } finally {
            // Dọn dẹp
            if (maDinhDanh != null) {
                ClientConnectionManager.removeConnection(maDinhDanh);
                
                // (Tùy chọn nâng cao sau này)
                // Bạn có thể kiểm tra xem máy có đang chạy mà mất kết nối không
                // và tự động gọi phienDAO.stopPhien(maDinhDanh, -1) 
                // (với -1 là mã lỗi "mất kết nối")
                
                System.out.println("❌ Máy " + maDinhDanh + " đã ngắt kết nối.");
            }
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                // Bỏ qua
            }
        }
    }
}