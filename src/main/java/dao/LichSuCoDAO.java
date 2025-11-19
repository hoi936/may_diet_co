package dao;

import model.LichSuCo;
import util.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
public class LichSuCoDAO {

    /**
     * Hàm cũ của bạn, giữ nguyên
     */
    public List<LichSuCo> findByPhien(int maPhien) {
        List<LichSuCo> list = new ArrayList<>();
        // Câu lệnh SQL của bạn đã có ma_phien, nhưng trong CSDL là không có?
        // Tạm thời giữ nguyên theo code của bạn
        String sql = "SELECT * FROM lich_su_co WHERE ma_phien = ? ORDER BY thoi_gian DESC";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPhien);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LichSuCo ls = new LichSuCo();
                ls.setMaLichSu(rs.getInt("ma_lich_su"));
                ls.setMaDinhDanh(rs.getString("ma_dinh_danh"));
                ls.setSoCoPhatHien(rs.getInt("so_co_phat_hien"));
                ls.setSoCoDiet(rs.getInt("so_co_diet"));
                ls.setViTri(rs.getString("vi_tri"));
                ls.setThoiGian(rs.getTimestamp("thoi_gian").toLocalDateTime());
                ls.setDuongDanAnh(rs.getString("duong_dan_anh"));
                // ls.setMaPhien(rs.getInt("ma_phien")); // Dòng này bị thiếu trong code của bạn, nhưng hàm findByPhien lại dùng ma_phien?
                list.add(ls);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * ✅ HÀM MỚI: Để SocketClientHandler gọi khi nhận "WEED:"
     * (Tôi sẽ dùng các cột bạn đã có trong hàm trên)
     */
    public void insert(LichSuCo ls) {
        String sql = "INSERT INTO lich_su_co (ma_dinh_danh, so_co_phat_hien, so_co_diet, vi_tri, thoi_gian, duong_dan_anh, ma_phien) " +
                     "VALUES (?, ?, ?, ?, NOW(), ?, ?)";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, ls.getMaDinhDanh());
            ps.setInt(2, ls.getSoCoPhatHien());
            ps.setInt(3, ls.getSoCoDiet());
            ps.setString(4, ls.getViTri());
            ps.setString(5, ls.getDuongDanAnh());
            ps.setInt(6, ls.getMaPhien()); // Đây là mã phiên rất quan trọng

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Map<String, Integer> getStatisticsByPhien(int maPhien) {
        // Dùng LinkedHashMap để giữ nguyên thứ tự "ORDER BY"
        Map<String, Integer> stats = new LinkedHashMap<>();
        
        // SUM() để đếm, GROUP BY để nhóm, ORDER BY để sắp xếp
        String sql = "SELECT vi_tri, SUM(so_co_phat_hien) as tong_co " +
                     "FROM lich_su_co " +
                     "WHERE ma_phien = ? " +
                     "GROUP BY vi_tri " +
                     "ORDER BY vi_tri ASC"; // Sắp xếp theo vị trí
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, maPhien);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                stats.put(
                    rs.getString("vi_tri"), 
                    rs.getInt("tong_co")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }
}
