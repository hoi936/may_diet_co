package controller;

// Đảm bảo bạn đã import đủ các lớp này
import dao.MayDietCoDAO;
import dao.PhienHoatDongDAO;
import dao.LenhDieuKhienDAO;
import model.MayDietCo;
import model.PhienHoatDong;
import controller.ClientConnectionManager; 

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class MayDetailServlet extends HttpServlet {
    private final MayDietCoDAO mayDAO = new MayDietCoDAO();
    private final PhienHoatDongDAO phienDAO = new PhienHoatDongDAO();
    private final LenhDieuKhienDAO lenhDAO = new LenhDieuKhienDAO();

    /**
     * ✅ HÀM NÀY GIỮ NGUYÊN
     * (Nó đã đúng vì nó gọi getAllByMay đã được sửa)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int maMay = Integer.parseInt(request.getParameter("id"));
            MayDietCo may = mayDAO.findById(maMay);
            
            if (may == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy máy.");
                return;
            }

            // Hàm này đã được sửa ở Bước 3 để đọc dữ liệu quãng đường
            List<PhienHoatDong> phienList = phienDAO.getAllByMay(may.getMaDinhDanh());

            request.setAttribute("may", may);
            request.setAttribute("phienList", phienList);
            request.getRequestDispatcher("/views/may-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID máy không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi máy chủ.");
        }
    }

    /**
     * ✅ HÀM NÀY THAY ĐỔI LOGIC HOÀN TOÀN
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int maMay = Integer.parseInt(request.getParameter("id"));
        String action = request.getParameter("action");

        MayDietCo may = mayDAO.findById(maMay);
        if (may == null || action == null) {
            response.sendRedirect(request.getContextPath() + "/may-list");
            return;
        }

        String maDinhDanh = may.getMaDinhDanh();
        String currentState = may.getTrangThai(); 

        if (action.equals("START")) {
            
            // Chỉ chạy nếu máy đang KHÔNG hoạt động
            if (!"DANG_HOAT_DONG".equals(currentState)) {
                
                // 1. Lấy quãng đường từ form
                float quangDuong = 20.0f; // Giá trị mặc định nếu có lỗi
                try {
                    // "quangDuongMucTieu" là 'name' của ô input ở Bước 6
                    quangDuong = Float.parseFloat(request.getParameter("quangDuongMucTieu"));
                } catch (Exception e) {
                    System.err.println("Không đọc được quãng đường, dùng giá trị mặc định 20.0m");
                }

                // 2. TẠO PHIÊN MỚI và LẤY VỀ MÃ PHIÊN (từ Bước 3)
                int newMaPhien = phienDAO.startPhien(maDinhDanh, quangDuong);

                if (newMaPhien != -1) { // Nếu tạo phiên thành công (ID > -1)
                    // 3. Cập nhật trạng thái máy
                    may.setTrangThai("DANG_HOAT_DONG");
                    mayDAO.update(may);
                    
                    // 4. Ghi lệnh vào DB (cho lịch sử)
                    lenhDAO.insertCommand(maDinhDanh, action);
                    
                    // 5. Gửi lệnh GỘP đến Jetson: "START:<ma_phien>:<quang_duong>"
                    // Ví dụ: "START:78:20.0"
                    String command = String.format("START:%d:%.1f", newMaPhien, quangDuong);
                    
                    ClientConnectionManager.sendCommand(maDinhDanh, command);
                    System.out.println("✅ Đã gửi lệnh: " + command);

                } else {
                    System.err.println("⚠️ LỖI NGHIÊM TRỌNG: Không thể tạo phiên mới trong CSDL.");
                }
                
            } else {
                System.out.println("⚠️ Cảnh báo: Máy đã chạy, bỏ qua lệnh START.");
            }

        } else if (action.equals("STOP")) {
            
             // Chỉ dừng nếu máy đang CHẠY
            if ("DANG_HOAT_DONG".equals(currentState)) {
                
                // 1. Ghi lệnh vào DB (lịch sử)
                lenhDAO.insertCommand(maDinhDanh, action);

                // 2. Gửi lệnh STOP đến Jetson
                ClientConnectionManager.sendCommand(maDinhDanh, "STOP");
                System.out.println("✅ Đã gửi lệnh: STOP (Dừng thủ công)");
                
                // 3. ✅ QUAN TRỌNG: KHÔNG cập nhật CSDL ở đây.
                // Chúng ta sẽ đợi Jetson gửi về "STOPPED:<distance>"
                // và SocketClientHandler sẽ làm việc đó.
                
            } else {
                 System.out.println("⚠️ Cảnh báo: Máy đã dừng, bỏ qua lệnh STOP.");
            }
        }

        // Chuyển hướng về trang chi tiết
        response.sendRedirect(request.getContextPath() + "/may-detail?id=" + maMay);
    }
}