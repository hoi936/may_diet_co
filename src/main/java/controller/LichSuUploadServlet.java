package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import util.DBConnect;

@WebServlet("/api/upload-lich-su")
@MultipartConfig
public class LichSuUploadServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String maLichSu = request.getParameter("ma_lich_su");
        String maDinhDanh = request.getParameter("ma_dinh_danh");
        String soCoPhatHien = request.getParameter("so_co_phat_hien");
        String soCoDiet = request.getParameter("so_co_diet");
        String maPhien = request.getParameter("ma_phien");
        String viTri = request.getParameter("vi_tri");
        String thoiGian = request.getParameter("thoi_gian");

        Part filePart = request.getPart("anh");
        String fileName = "anh_" + System.currentTimeMillis() + ".png";

        // Đường dẫn thực tế trên server
        String uploadPath = getServletContext().getRealPath("/Images");
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        // Lưu file ảnh
        filePart.write(uploadPath + File.separator + fileName);

        // Ghi vào DB
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "INSERT INTO lich_su_co(ma_lich_su, ma_dinh_danh, so_co_phat_hien, so_co_diet, vi_tri, thoi_gian, duong_dan_anh, ma_phien) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, maLichSu);
            ps.setString(2, maDinhDanh);
            ps.setString(3, soCoPhatHien);
            ps.setString(4, soCoDiet);
            ps.setString(5, viTri);
            ps.setString(6, thoiGian);
            ps.setString(7, "Images/" + fileName);
            ps.setString(8, maPhien);
            ps.executeUpdate();

            response.getWriter().print("{\"status\":\"ok\", \"file\":\"" + fileName + "\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().print("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
        }
    }
}
