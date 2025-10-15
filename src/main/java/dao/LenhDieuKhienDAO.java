package dao;

import util.DBConnect;
import java.sql.*;

public class LenhDieuKhienDAO {
    public void insertCommand(String maDinhDanh, String lenh) {
        String sql = "INSERT INTO lenh_dieu_khien(ma_dinh_danh, lenh, trang_thai) VALUES (?, ?, 'CHO_XU_LY')";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDinhDanh);
            ps.setString(2, lenh); // START hoặc STOP
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
