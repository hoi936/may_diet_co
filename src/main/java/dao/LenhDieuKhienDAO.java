package dao;

import util.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class LenhDieuKhienDAO {

    // Lưu lệnh điều khiển vào DB
    public void insertCommand(String maDinhDanh, String lenh) {
        String sql = "INSERT INTO lenh_dieu_khien(ma_dinh_danh, lenh, trang_thai) VALUES (?, ?, 'CHO_XU_LY')";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maDinhDanh);
            ps.setString(2, lenh);
            ps.executeUpdate();

            System.out.println("✅ Đã ghi lệnh " + lenh + " cho " + maDinhDanh + " vào DB.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}