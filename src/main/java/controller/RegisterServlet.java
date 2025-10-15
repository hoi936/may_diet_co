package controller;

import dao.NguoiDungDAO;
import model.NguoiDung;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class RegisterServlet extends HttpServlet {
    private NguoiDungDAO userDao = new NguoiDungDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Tạo user mới
        NguoiDung user = new NguoiDung();
        user.setTenDangNhap(username);
        user.setMatKhau(password);

        boolean success = userDao.insert(user);

        if (success) {
            // Đăng ký thành công → chuyển về login
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            // Báo lỗi khi trùng username
            request.setAttribute("error", "Tên đăng nhập đã tồn tại!");
        }
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
        
    }
}
