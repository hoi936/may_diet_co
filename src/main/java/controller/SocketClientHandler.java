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
                
                // --- (TOÀN BỘ CODE TRONG VÒNG LẶP WHILE CỦA BẠN GIỮ NGUYÊN) ---
                
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                System.out.println("📥 Nhận từ [" + (maDinhDanh != null ? maDinhDanh : socket.getInetAddress()) + "]: " + line);

                if (maDinhDanh == null) {
                    if (line.startsWith("MAY_ID:")) {
                        maDinhDanh = line.substring(7).trim();
                        ClientConnectionManager.addConnection(maDinhDanh, socket);
                        System.out.println("✅ Máy " + maDinhDanh + " đã xác thực và kết nối.");
                    } else {
                        System.err.println("⚠️ Lỗi: Client chưa xác thực. Đóng kết nối.");
                        break; 
                    }
                    continue; 
                }

                // ------ Xử lý các lệnh sau khi đã xác thực ------

                if (line.startsWith("STATUS:")) {
                    String status = line.substring(7).trim();
                    mayDAO.updateTrangThai(maDinhDanh, status);
                    System.out.println("💾 Cập nhật trạng thái " + maDinhDanh + " = " + status);
                
                } else if (line.startsWith("WEED:")) {
                    try {
                        String[] parts = line.split(":", 5); 
                        int maPhien = Integer.parseInt(parts[1]);
                        String viTri = parts[2];
                        int soCoDiet = Integer.parseInt(parts[3]);
                        String duongDanAnh = parts[4];
                        
                        LichSuCo lsc = new LichSuCo();
                        lsc.setMaDinhDanh(maDinhDanh); 
                        lsc.setMaPhien(maPhien);
                        lsc.setViTri(viTri);
                        lsc.setSoCoDiet(soCoDiet);
                        lsc.setSoCoPhatHien(1); 
                        lsc.setDuongDanAnh(duongDanAnh);
                        
                        lichSuDAO.insert(lsc); 
                        System.out.println("💾 Đã lưu Lịch sử cỏ cho phiên " + maPhien);

                    } catch (Exception e) {
                        System.err.println("⚠️ Lỗi phân tích lệnh WEED: " + line + " | Lỗi: " + e.getMessage());
                    }
                
                } else if (line.startsWith("COMPLETED:")) {
                    try {
                        float distance = Float.parseFloat(line.substring(10).trim());
                        System.out.println("🏁 Máy " + maDinhDanh + " tự hoàn thành: " + distance + "m");
                        
                        mayDAO.updateTrangThai(maDinhDanh, "NGUNG_HOAT_DONG");
                        phienDAO.stopPhien(maDinhDanh, distance); 
                    
                    } catch (Exception e) {
                        System.err.println("⚠️ Lỗi phân tích lệnh COMPLETED: " + line + " | Lỗi: " + e.getMessage());
                    }

                } else if (line.startsWith("STOPPED:")) {
                    try {
                        float distance = Float.parseFloat(line.substring(8).trim());
                        System.out.println("🛑 Máy " + maDinhDanh + " bị dừng thủ công: " + distance + "m");
                        
                        mayDAO.updateTrangThai(maDinhDanh, "NGUNG_HOAT_DONG");
                        phienDAO.stopPhien(maDinhDanh, distance);

                    } catch (Exception e) {
                        System.err.println("⚠️ Lỗi phân tích lệnh STOPPED: " + line + " | Lỗi: " + e.getMessage());
                    }
                }
                
                // --- (HẾT CODE TRONG VÒNG LẶP WHILE) ---
            } 

        } catch (IOException e) {
            System.out.println("⚠️ Mất kết nối với " + (maDinhDanh != null ? maDinhDanh : "client"));
        } finally {
            
            // ✅✅✅ LOGIC MỚI CỦA BẠN NẰM Ở ĐÂY ✅✅✅
            
            // Dọn dẹp chỉ khi máy đã xác thực (maDinhDanh != null)
            if (maDinhDanh != null) {
                // 1. Xóa socket khỏi bộ nhớ
                ClientConnectionManager.removeConnection(maDinhDanh);
                
                System.out.println("...Đang kiểm tra và tự động dừng phiên cho " + maDinhDanh + " do mất kết nối.");
                
                // 2. Cập nhật trạng thái máy về "NGUNG_HOAT_DONG"
                // Điều này rất quan trọng để nút "Bật máy" sáng lại
                mayDAO.updateTrangThai(maDinhDanh, "NGUNG_HOAT_DONG");
                
                // 3. Dừng phiên đang chạy (nếu có)
                // Hàm stopPhien đã đủ thông minh (nhờ "AND thoi_gian_tat IS NULL")
                // Nó sẽ chỉ cập nhật phiên nào đang chạy.
                // Chúng ta dùng -1.0f làm mã lỗi "Mất kết nối"
                phienDAO.stopPhien(maDinhDanh, -1.0f);
                
                System.out.println("✅ Đã tự động dừng phiên và cập nhật trạng thái cho " + maDinhDanh);
                
                System.out.println("❌ Máy " + maDinhDanh + " đã ngắt kết nối.");
            }
            
            // Đóng socket vật lý
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