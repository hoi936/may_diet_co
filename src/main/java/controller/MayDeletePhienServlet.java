package controller; // Hoặc package servlet của bạn

// QUAN TRỌNG: Hãy chắc chắn bạn import ĐÚNG BỘ
// (Chọn 1 trong 2, tùy thuộc vào file pom.xml của bạn)

// LỰA CHỌN 1: Nếu code của bạn đang dùng "javax" (Giống ảnh chụp)
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// LỰA CHỌN 2: Nếu file pom.xml của bạn dùng "jakarta"
// import jakarta.servlet.ServletException;
// import jakarta.servlet.annotation.WebServlet;
// import jakarta.servlet.http.HttpServlet;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

import dao.PhienHoatDongDAO;
import java.io.IOException;

// Đây là "Bảng chỉ đường" mà Tomcat tìm kiếm
@WebServlet("/may-delete-phien")
public class MayDeletePhienServlet extends HttpServlet {

    private PhienHoatDongDAO phienHoatDongDAO;

    @Override
    public void init() {
        phienHoatDongDAO = new PhienHoatDongDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 1. Lấy mã phiên cần xóa từ URL
            int maPhien = Integer.parseInt(request.getParameter("maPhien"));
            
            // 2. Lấy mã máy để biết đường quay lại
            String maMay = request.getParameter("maMay");

            // 3. Gọi DAO để thực hiện xóa
            phienHoatDongDAO.deletePhien(maPhien);

            // 4. Chuyển hướng người dùng quay lại trang chi tiết máy
            response.sendRedirect(request.getContextPath() + "/may-detail?id=" + maMay);
            
        } catch (Exception e) {
            e.printStackTrace();
            // (Bạn có thể chuyển hướng đến một trang lỗi)
        }
    }
}