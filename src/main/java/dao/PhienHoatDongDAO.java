package dao;
import java.util.LinkedHashMap;
import java.util.Map;
import model.PhienHoatDong;
import util.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhienHoatDongDAO {

    /**
     * ✅ ĐÃ SỬA: Nhận quãng đường và trả về ma_phien (ID phiên)
     * @return ma_phien vừa được tạo, hoặc -1 nếu thất bại.
     */
    public int startPhien(String maDinhDanh, float quangDuongMucTieu) {
        String sql = "INSERT INTO phien_hoat_dong (ma_dinh_danh, thoi_gian_bat, quang_duong_muc_tieu) VALUES (?, NOW(), ?)";
        int generatedMaPhien = -1; // Giá trị mặc định nếu thất bại

        // "Statement.RETURN_GENERATED_KEYS" là mấu chốt để lấy ID
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, maDinhDanh);
            ps.setFloat(2, quangDuongMucTieu);
            
            int affectedRows = ps.executeUpdate();

            // Lấy ID vừa được tạo ra
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedMaPhien = rs.getInt(1); // Lấy mã phiên
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedMaPhien; // Trả mã phiên về cho Servlet
    }

    /**
     * ✅ ĐÃ SỬA: Nhận quãng đường hoàn thành
     * Hàm này sẽ được gọi bởi SocketClientHandler khi máy báo cáo về.
     */
    public void stopPhien(String maDinhDanh, float quangDuongHoanThanh) {
        // Cập nhật phiên DANG CHẠY (thoi_gian_tat IS NULL) của máy đó
        String sql = "UPDATE phien_hoat_dong SET thoi_gian_tat = NOW(), quang_duong_hoan_thanh = ? WHERE ma_dinh_danh = ? AND thoi_gian_tat IS NULL";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setFloat(1, quangDuongHoanThanh); // Quãng đường thực tế
            ps.setString(2, maDinhDanh);
            ps.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ✅ ĐÃ SỬA: Đọc thêm 2 cột mới
     */
    public PhienHoatDong getPhienDangChay(String maDinhDanh) {
        String sql = "SELECT * FROM phien_hoat_dong WHERE ma_dinh_danh = ? AND thoi_gian_tat IS NULL LIMIT 1";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDinhDanh);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToPhienHoatDong(rs); // Dùng hàm chung
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ✅ ĐÃ SỬA: Đọc thêm 2 cột mới
     */
    public PhienHoatDong findById(int maPhien) {
        String sql = "SELECT * FROM phien_hoat_dong WHERE ma_phien = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maPhien);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToPhienHoatDong(rs); // Dùng hàm chung
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ✅ ĐÃ SỬA: Đọc thêm 2 cột mới
     */
    public List<PhienHoatDong> getAllByMay(String maDinhDanh) {
        List<PhienHoatDong> list = new ArrayList<>();
        String sql = "SELECT * FROM phien_hoat_dong WHERE ma_dinh_danh = ? ORDER BY ma_phien DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDinhDanh);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRowToPhienHoatDong(rs)); // Dùng hàm chung
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * ✅ HÀM MỚI: Dùng chung để đọc dữ liệu từ ResultSet
     * @throws SQLException
     */
    public void deletePhien(int maPhien) {
        String sqlDeleteLichSu = "DELETE FROM lich_su_co WHERE ma_phien = ?";
        String sqlDeletePhien = "DELETE FROM phien_hoat_dong WHERE ma_phien = ?";
        
        Connection conn = null;
        PreparedStatement psLichSu = null;
        PreparedStatement psPhien = null;

        try {
            conn = DBConnect.getConnection();
            
            // Bắt đầu một Transaction
            conn.setAutoCommit(false); 

            // 1. Xóa dữ liệu "con" (lịch sử cỏ) trước
            psLichSu = conn.prepareStatement(sqlDeleteLichSu);
            psLichSu.setInt(1, maPhien);
            psLichSu.executeUpdate();
            
            // 2. Xóa dữ liệu "cha" (phiên) sau
            psPhien = conn.prepareStatement(sqlDeletePhien);
            psPhien.setInt(1, maPhien);
            psPhien.executeUpdate();
            
            // Nếu cả hai thành công, commit transaction
            conn.commit();
            
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi, rollback tất cả thay đổi
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        } finally {
            // Đóng các tài nguyên
            try {
                if (psLichSu != null) psLichSu.close();
                if (psPhien != null) psPhien.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Trả về trạng thái tự động commit
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private PhienHoatDong mapRowToPhienHoatDong(ResultSet rs) throws SQLException {
        PhienHoatDong phien = new PhienHoatDong();
        phien.setMaPhien(rs.getInt("ma_phien"));
        phien.setMaDinhDanh(rs.getString("ma_dinh_danh"));
        phien.setThoiGianBat(rs.getTimestamp("thoi_gian_bat").toLocalDateTime());

        Timestamp thoiGianTat = rs.getTimestamp("thoi_gian_tat");
        if (thoiGianTat != null) {
            phien.setThoiGianTat(thoiGianTat.toLocalDateTime());
        }

        // Đọc dữ liệu mới
        phien.setQuangDuongMucTieu(rs.getFloat("quang_duong_muc_tieu"));
        
        // Dùng rs.getObject() để kiểm tra giá trị NULL an toàn
        Object hoanThanhObj = rs.getObject("quang_duong_hoan_thanh");
        if (hoanThanhObj != null) {
            phien.setQuangDuongHoanThanh((Float) hoanThanhObj);
        } else {
            phien.setQuangDuongHoanThanh(null); // Set là null nếu trong DB là NULL
        }
        
        return phien;
    } 
}