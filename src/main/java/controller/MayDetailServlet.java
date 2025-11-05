package controller;

import dao.MayDietCoDAO;
import dao.PhienHoatDongDAO;
import dao.LenhDieuKhienDAO;
import model.MayDietCo;
import model.PhienHoatDong;

// ✅ BƯỚC 1: THÊM IMPORT QUAN TRỌNG NÀY
import controller.ClientConnectionManager; 

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class MayDetailServlet extends HttpServlet {
    private final MayDietCoDAO mayDAO = new MayDietCoDAO();
    private final PhienHoatDongDAO phienDAO = new PhienHoatDongDAO();
    private final LenhDieuKhienDAO lenhDAO = new LenhDieuKhienDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Phần doGet giữ nguyên, không cần sửa
        int maMay = Integer.parseInt(request.getParameter("id"));
        MayDietCo may = mayDAO.findById(maMay);
        if (may == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy máy.");
            return;
        }

        List<PhienHoatDong> phienList = phienDAO.getAllByMay(may.getMaDinhDanh());

        request.setAttribute("may", may);
        request.setAttribute("phienList", phienList);
        request.getRequestDispatcher("/views/may-detail.jsp").forward(request, response);
    }

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

        if (action.equals("START")) {
            // --- Phần code CŨ của bạn (Giữ nguyên) ---
            lenhDAO.insertCommand(maDinhDanh, action);
            may.setTrangThai("DANG_HOAT_DONG");
            mayDAO.update(may);
            phienDAO.startPhien(maDinhDanh);
            
            // ✅ BƯỚC 2: GỬI LỆNH ĐẾN JETSON
            ClientConnectionManager.sendCommand(maDinhDanh, "START");

        } else if (action.equals("STOP")) {
            // --- Phần code CŨ của bạn (Giữ nguyên) ---
            lenhDAO.insertCommand(maDinhDanh, action);
            may.setTrangThai("NGUNG_HOAT_DONG");
            mayDAO.update(may);
            phienDAO.stopPhien(maDinhDanh);
            
            // ✅ BƯỚC 2: GỬI LỆNH ĐẾN JETSON
            ClientConnectionManager.sendCommand(maDinhDanh, "STOP");
        }

        // Chuyển hướng về trang chi tiết
        response.sendRedirect(request.getContextPath() + "/may-detail?id=" + maMay);
    }
}