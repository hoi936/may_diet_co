package controller;

import dao.MayDietCoDAO;
import model.MayDietCo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class MayEditServlet extends HttpServlet {
    private MayDietCoDAO mayDAO = new MayDietCoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        MayDietCo may = mayDAO.findById(id);
        request.setAttribute("may", may);
        request.getRequestDispatcher("/views/may-edit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String maDinhDanh = request.getParameter("maDinhDanh");
        String tenMay = request.getParameter("tenMay");
        String trangThai = request.getParameter("trangThai");

        MayDietCo may = new MayDietCo();
        may.setMaMay(id);
        may.setMaDinhDanh(maDinhDanh);
        may.setTenMay(tenMay);
        may.setTrangThai(trangThai);

        mayDAO.update(may);

        response.sendRedirect(request.getContextPath() + "/may-list");
    }
}
