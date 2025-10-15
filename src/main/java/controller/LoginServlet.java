package controller;

import dao.NguoiDungDAO;
import model.NguoiDung;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    private NguoiDungDAO userDao = new NguoiDungDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // forward tới login.jsp trong folder views
        request.getRequestDispatcher("/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        NguoiDung user = userDao.login(username, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            // redirect tới may-list.jsp (trong views)
            response.sendRedirect(request.getContextPath() + "/may-list");
        } else {
            request.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
    }
}
