package controller;

import dao.LichSuCoDAO;
import dao.PhienHoatDongDAO;
import model.LichSuCo;
import model.PhienHoatDong;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class LichSuPhienServlet extends HttpServlet {
    private final LichSuCoDAO lichSuDAO = new LichSuCoDAO();
    private final PhienHoatDongDAO phienDAO = new PhienHoatDongDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy tham số từ URL
            String maPhienStr = request.getParameter("maPhien");
            String maMayStr = request.getParameter("maMay");

            if (maPhienStr == null || maMayStr == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu tham số maPhien hoặc maMay.");
                return;
            }

            int maPhien = Integer.parseInt(maPhienStr);
            int maMay = Integer.parseInt(maMayStr);

            // Lấy thông tin phiên hoạt động và danh sách lịch sử cỏ
            PhienHoatDong phien = phienDAO.findById(maPhien);
            if (phien == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy phiên hoạt động.");
                return;
            }

            List<LichSuCo> lichSuList = lichSuDAO.findByPhien(maPhien);

            // Gửi dữ liệu sang JSP
            request.setAttribute("phien", phien);
            request.setAttribute("lichSuList", lichSuList);
            request.setAttribute("maMay", maMay); // Giữ để quay lại đúng máy

            // Chuyển hướng sang trang lịch sử
            request.getRequestDispatcher("/views/lich-su-phien.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tham số không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi hệ thống.");
        }
    }
}
