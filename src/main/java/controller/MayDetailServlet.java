package controller;

// Đảm bảo bạn đã import đủ các lớp này
import dao.MayDietCoDAO;
import dao.PhienHoatDongDAO;
import dao.LenhDieuKhienDAO;
import model.MayDietCo;
import model.PhienHoatDong;
import controller.ClientConnectionManager; // <-- Import này rất quan trọng

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class MayDetailServlet extends HttpServlet {
    private final MayDietCoDAO mayDAO = new MayDietCoDAO();
    private final PhienHoatDongDAO phienDAO = new PhienHoatDongDAO();
    private final LenhDieuKhienDAO lenhDAO = new LenhDieuKhienDAO();

    /**
     * ✅ HÀM NÀY ĐÃ ĐƯỢC CẬP NHẬT
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

            List<PhienHoatDong> phienList = phienDAO.getAllByMay(may.getMaDinhDanh());

            // ✅✅✅ LOGIC MỚI ĐỂ KIỂM TRA ONLINE ✅✅✅
            // Kiểm tra xem có socket nào đang kết nối cho máy này không
            boolean isOnline = (ClientConnectionManager.getConnection(may.getMaDinhDanh()) != null);
            
            // Gửi tất cả dữ liệu ra JSP
            request.setAttribute("may", may);
            request.setAttribute("phienList", phienList);
            request.setAttribute("isOnline", isOnline); // <-- Gửi trạng thái online

            request.getRequestDispatcher("/views/may-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID máy không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi máy chủ.");
        }
    }

    /**
     * ✅ HÀM NÀY GIỮ NGUYÊN (Không cần sửa)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ... (Toàn bộ code doPost của bạn giữ nguyên như cũ) ...
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
            
            if (!"DANG_HOAT_DONG".equals(currentState)) {
                
                float quangDuong = 20.0f; 
                try {
                    quangDuong = Float.parseFloat(request.getParameter("quangDuongMucTieu"));
                } catch (Exception e) {
                    System.err.println("Không đọc được quãng đường, dùng giá trị mặc định 20.0m");
                }

                int newMaPhien = phienDAO.startPhien(maDinhDanh, quangDuong);

                if (newMaPhien != -1) { 
                    may.setTrangThai("DANG_HOAT_DONG");
                    mayDAO.update(may);
                    
                    lenhDAO.insertCommand(maDinhDanh, action);
                    
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
            
             if ("DANG_HOAT_DONG".equals(currentState)) {
                
                lenhDAO.insertCommand(maDinhDanh, action);
                ClientConnectionManager.sendCommand(maDinhDanh, "STOP");
                System.out.println("✅ Đã gửi lệnh: STOP (Dừng thủ công)");
                
            } else {
                 System.out.println("⚠️ Cảnh báo: Máy đã dừng, bỏ qua lệnh STOP.");
            }
        }

        response.sendRedirect(request.getContextPath() + "/may-detail?id=" + maMay);
    }
}