package controller;

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
            boolean isOnline = (ClientConnectionManager.getConnection(may.getMaDinhDanh()) != null);
            
            request.setAttribute("may", may);
            request.setAttribute("phienList", phienList);
            request.setAttribute("isOnline", isOnline); 

            request.getRequestDispatcher("/views/may-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID máy không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi máy chủ.");
        }
    }

    /**
     * ✅✅✅ HÀM NÀY ĐÃ ĐƯỢC SỬA LỖI RELOAD (Cách 2) ✅✅✅
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

        // 1. LỆNH START
        if (action.equals("START")) {
            if (!"DANG_HOAT_DONG".equals(currentState)) {
                float quangDuong = 20.0f; 
                try {
                    quangDuong = Float.parseFloat(request.getParameter("quangDuongMucTieu"));
                } catch (Exception e) { /* Bỏ qua, dùng giá trị mặc định */ }
                
                int newMaPhien = phienDAO.startPhien(maDinhDanh, quangDuong);

                if (newMaPhien != -1) { 
                    mayDAO.updateTrangThai(maDinhDanh, "DANG_HOAT_DONG");
                    lenhDAO.insertCommand(maDinhDanh, action);
                    String command = String.format("START:%d:%.1f", newMaPhien, quangDuong);
                    ClientConnectionManager.sendCommand(maDinhDanh, command);
                    System.out.println("✅ Đã gửi lệnh: " + command);
                } else {
                    System.err.println("⚠️ LỖI: Không thể tạo phiên mới trong CSDL.");
                }
            } 
        
        // 2. LỆNH STOP
        } else if (action.equals("STOP")) {
             if ("DANG_HOAT_DONG".equals(currentState) || "TAM_DUNG".equals(currentState)) { 
                mayDAO.updateTrangThai(maDinhDanh, "NGUNG_HOAT_DONG"); 
                lenhDAO.insertCommand(maDinhDanh, action);
                ClientConnectionManager.sendCommand(maDinhDanh, "STOP");
                System.out.println("✅ Đã gửi lệnh: STOP (Dừng thủ công)");
            }
        
        // 3. LỆNH PAUSE
        } else if (action.equals("PAUSE")) {
            if ("DANG_HOAT_DONG".equals(currentState)) { 
                mayDAO.updateTrangThai(maDinhDanh, "TAM_DUNG");
                lenhDAO.insertCommand(maDinhDanh, action);
                ClientConnectionManager.sendCommand(maDinhDanh, "PAUSE");
                System.out.println("✅ Đã gửi lệnh: PAUSE");
            }

        // 4. LỆNH RESUME
        } else if (action.equals("RESUME")) {
            if ("TAM_DUNG".equals(currentState)) { 
                mayDAO.updateTrangThai(maDinhDanh, "DANG_HOAT_DONG");
                lenhDAO.insertCommand(maDinhDanh, action);
                ClientConnectionManager.sendCommand(maDinhDanh, "RESUME");
                System.out.println("✅ Đã gửi lệnh: RESUME");
            }
        }

        // ✅✅✅ DÒNG SỬA LỖI NẰM Ở ĐÂY ✅✅✅
        // Thêm độ trễ 0.5 giây để đợi tin nhắn "cũ" (stale) từ client
        try { 
            Thread.sleep(500); // Đợi 500 mili-giây
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Chuyển hướng về trang chi tiết
        response.sendRedirect(request.getContextPath() + "/may-detail?id=" + maMay);
    }
}