package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import org.json.JSONObject;
import util.DBConnect;

public class TestApiServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }

        JSONObject json = new JSONObject(sb.toString());
        String maDinhDanh = json.getString("ma_dinh_danh");
        String tenMay = json.getString("ten_may");

        try (Connection conn = DBConnect.getConnection()) {
            String sql = "INSERT INTO may_diet_co(ma_dinh_danh, ten_may, trang_thai) VALUES (?, ?, 'NGUNG_HOAT_DONG')";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, maDinhDanh);
            ps.setString(2, tenMay);
            ps.executeUpdate();

            JSONObject res = new JSONObject();
            res.put("message", "Đã thêm máy thử thành công");
            res.put("ma_dinh_danh", maDinhDanh);
            response.getWriter().print(res.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}

