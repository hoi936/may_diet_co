package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false); // lấy session hiện tại nếu có
        if (session != null) {
            session.invalidate(); // xóa session
        }
        response.sendRedirect("login"); // quay về trang login
    }
}
