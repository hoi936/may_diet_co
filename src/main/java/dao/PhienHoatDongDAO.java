package dao;

import model.PhienHoatDong;
import util.DBConnect;
import java.sql.*;
import java.util.*;

public class PhienHoatDongDAO {

    public void startPhien(String maDinhDanh) {
        String sql = "INSERT INTO phien_hoat_dong (ma_dinh_danh, thoi_gian_bat) VALUES (?, NOW())";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDinhDanh);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPhien(String maDinhDanh) {
        String sql = "UPDATE phien_hoat_dong SET thoi_gian_tat = NOW() WHERE ma_dinh_danh = ? AND thoi_gian_tat IS NULL";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDinhDanh);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PhienHoatDong getPhienDangChay(String maDinhDanh) {
        String sql = "SELECT * FROM phien_hoat_dong WHERE ma_dinh_danh = ? AND thoi_gian_tat IS NULL LIMIT 1";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDinhDanh);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PhienHoatDong phien = new PhienHoatDong();
                phien.setMaPhien(rs.getInt("ma_phien"));
                phien.setMaDinhDanh(rs.getString("ma_dinh_danh"));
                phien.setThoiGianBat(rs.getTimestamp("thoi_gian_bat").toLocalDateTime());
                return phien;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public PhienHoatDong findById(int maPhien) {
    String sql = "SELECT * FROM phien_hoat_dong WHERE ma_phien = ?";
    try (Connection conn = DBConnect.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, maPhien);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new PhienHoatDong(
                rs.getInt("ma_phien"),
                rs.getString("ma_dinh_danh"),
                rs.getTimestamp("thoi_gian_bat").toLocalDateTime(),
                rs.getTimestamp("thoi_gian_tat") != null ? rs.getTimestamp("thoi_gian_tat").toLocalDateTime() : null
            );
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

    public List<PhienHoatDong> getAllByMay(String maDinhDanh) {
        List<PhienHoatDong> list = new ArrayList<>();
        String sql = "SELECT * FROM phien_hoat_dong WHERE ma_dinh_danh = ? ORDER BY ma_phien DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDinhDanh);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PhienHoatDong phien = new PhienHoatDong(
                    rs.getInt("ma_phien"),
                    rs.getString("ma_dinh_danh"),
                    rs.getTimestamp("thoi_gian_bat").toLocalDateTime(),
                    rs.getTimestamp("thoi_gian_tat") != null ? rs.getTimestamp("thoi_gian_tat").toLocalDateTime() : null
                );
                list.add(phien);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
