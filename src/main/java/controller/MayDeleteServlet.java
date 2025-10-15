package controller;

import dao.MayDietCoDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class MayDeleteServlet extends HttpServlet {
    private MayDietCoDAO mayDAO = new MayDietCoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        mayDAO.delete(id);
        response.sendRedirect(request.getContextPath() + "/may-list");
    }
}