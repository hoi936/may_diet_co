package dao;

import model.LichSuCo;
import util.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LichSuCoDAO {

    public List<LichSuCo> findByPhien(int maPhien) {
        List<LichSuCo> list = new ArrayList<>();
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
                list.add(ls);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
