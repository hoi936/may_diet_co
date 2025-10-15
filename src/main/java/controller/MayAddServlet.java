package controller;

import dao.MayDietCoDAO;
import model.MayDietCo;
import model.NguoiDung;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class MayAddServlet extends HttpServlet {
    private MayDietCoDAO mayDAO = new MayDietCoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hiển thị form thêm
        request.getRequestDispatcher("/views/may-add.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        NguoiDung user = (NguoiDung) session.getAttribute("user");

        MayDietCo may = new MayDietCo();
        may.setMaDinhDanh(request.getParameter("maDinhDanh"));
        may.setTenMay(request.getParameter("tenMay"));
        may.setTrangThai("NGUNG_HOAT_DONG"); // mặc định

        mayDAO.insert(may, user.getMaNguoiDung());

        response.sendRedirect(request.getContextPath() + "/may-list");
    }
}
