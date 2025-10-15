package controller;

import dao.MayDietCoDAO;
import model.MayDietCo;
import model.NguoiDung;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class MayListServlet extends HttpServlet {
    private final MayDietCoDAO mayDAO = new MayDietCoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        NguoiDung user = (NguoiDung) session.getAttribute("user");
        List<MayDietCo> list = mayDAO.getAllByUser(user.getMaNguoiDung());

        request.setAttribute("listMay", list);
        request.getRequestDispatcher("views/may-list.jsp").forward(request, response);
    }
}
