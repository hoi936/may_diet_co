package dao;

import model.NguoiDung;
import util.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class NguoiDungDAO {

    // Hàm đăng nhập
    public NguoiDung login(String username, String password) {
        String sql = "SELECT * FROM nguoi_dung WHERE ten_dang_nhap = ? AND mat_khau = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, password.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                NguoiDung user = new NguoiDung();
                user.setMaNguoiDung(rs.getInt("ma_nguoi_dung"));
                user.setTenDangNhap(rs.getString("ten_dang_nhap"));
                user.setMatKhau(rs.getString("mat_khau"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Hàm thêm người dùng mới (trả về true nếu thêm thành công, false nếu trùng tên)
    public boolean insert(NguoiDung user) {
        String checkSql = "SELECT COUNT(*) FROM nguoi_dung WHERE ten_dang_nhap = ?";
        String insertSql = "INSERT INTO nguoi_dung (ten_dang_nhap, mat_khau) VALUES (?, ?)";
        try (Connection conn = DBConnect.getConnection()) {
            // Kiểm tra trùng username
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setString(1, user.getTenDangNhap().trim());
                ResultSet rs = psCheck.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // username đã tồn tại
                }
            }

            // Nếu chưa tồn tại thì thêm mới
            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setString(1, user.getTenDangNhap().trim());
                psInsert.setString(2, user.getMatKhau().trim());
                psInsert.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
