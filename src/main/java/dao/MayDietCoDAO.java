package dao;

import model.MayDietCo;
import util.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MayDietCoDAO {

    // Lấy danh sách máy theo người dùng
    public List<MayDietCo> getAllByUser(int maNguoiDung) {
        List<MayDietCo> list = new ArrayList<>();
        String sql = "SELECT * FROM may_diet_co WHERE ma_nguoi_dung = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MayDietCo may = new MayDietCo();
                may.setIp(rs.getString("ip"));
                may.setMaMay(rs.getInt("ma_may"));
                may.setMaDinhDanh(rs.getString("ma_dinh_danh"));
                may.setTenMay(rs.getString("ten_may"));
                may.setTrangThai(rs.getString("trang_thai"));
                may.setLanCuoiHoatDong(rs.getTimestamp("lan_cuoi_hoat_dong"));
                list.add(may);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm máy mới cho người dùng
    public void insert(MayDietCo may, int maNguoiDung) {
        String sql = "INSERT INTO may_diet_co(ma_dinh_danh, ten_may, trang_thai, lan_cuoi_hoat_dong, ma_nguoi_dung) " +
                     "VALUES (?, ?, ?, NOW(), ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, may.getMaDinhDanh());
            ps.setString(2, may.getTenMay());
            ps.setString(3, may.getTrangThai());
            ps.setInt(4, maNguoiDung);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void update(MayDietCo may) {
        String sql = "UPDATE may_diet_co SET ma_dinh_danh = ?, ten_may = ?, trang_thai = ?, lan_cuoi_hoat_dong = NOW() WHERE ma_may = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, may.getMaDinhDanh());
            ps.setString(2, may.getTenMay());
            ps.setString(3, may.getTrangThai());
            ps.setInt(4, may.getMaMay());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Tìm máy theo ID
    public MayDietCo findById(int id) {
        String sql = "SELECT * FROM may_diet_co WHERE ma_may = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                MayDietCo may = new MayDietCo();
                may.setMaMay(rs.getInt("ma_may"));
                may.setMaDinhDanh(rs.getString("ma_dinh_danh"));
                may.setTenMay(rs.getString("ten_may"));
                may.setTrangThai(rs.getString("trang_thai"));
                may.setLanCuoiHoatDong(rs.getTimestamp("lan_cuoi_hoat_dong"));
                return may;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void delete(int id) {
        String sql = "DELETE FROM may_diet_co WHERE ma_may = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ SỬA LỖI: Đã triển khai hàm này
    public void updateTrangThai(String maDinhDanh, String state) {
        String sql = "UPDATE may_diet_co SET trang_thai = ?, lan_cuoi_hoat_dong = NOW() WHERE ma_dinh_danh = ?";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, state);
            ps.setString(2, maDinhDanh);
            ps.executeUpdate();
            
        } catch (Exception e) {
            System.err.println("Lỗi cập nhật trạng thái cho " + maDinhDanh + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}