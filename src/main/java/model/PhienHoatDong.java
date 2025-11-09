package model;

import java.time.LocalDateTime;

public class PhienHoatDong {
    private int maPhien;
    private String maDinhDanh;
    private LocalDateTime thoiGianBat;
    private LocalDateTime thoiGianTat;

    // ✅✅✅ DỮ LIỆU MỚI THÊM VÀO ✅✅✅
    private float quangDuongMucTieu;
    // Dùng kiểu "Float" (object) để có thể nhận giá trị NULL từ CSDL
    private Float quangDuongHoanThanh; 

    // --- (Constructor cũ của bạn giữ nguyên) ---
    public PhienHoatDong() {}

    public PhienHoatDong(int maPhien, String maDinhDanh, LocalDateTime thoiGianBat, LocalDateTime thoiGianTat) {
        // (Constructor này nếu có thì giữ nguyên, không cần sửa)
        this.maPhien = maPhien;
        this.maDinhDanh = maDinhDanh;
        this.thoiGianBat = thoiGianBat;
        this.thoiGianTat = thoiGianTat;
    }

    // --- (Các getter/setter cũ của bạn giữ nguyên) ---
    public int getMaPhien() { return maPhien; }
    public void setMaPhien(int maPhien) { this.maPhien = maPhien; }

    public String getMaDinhDanh() { return maDinhDanh; }
    public void setMaDinhDanh(String maDinhDanh) { this.maDinhDanh = maDinhDanh; }

    public LocalDateTime getThoiGianBat() { return thoiGianBat; }
    public void setThoiGianBat(LocalDateTime thoiGianBat) { this.thoiGianBat = thoiGianBat; }

    public LocalDateTime getThoiGianTat() { return thoiGianTat; }
    public void setThoiGianTat(LocalDateTime thoiGianTat) { this.thoiGianTat = thoiGianTat; }

    
    // ✅✅✅ GETTER/SETTER MỚI THÊM VÀO ✅✅✅
    
    public float getQuangDuongMucTieu() {
        return quangDuongMucTieu;
    }

    public void setQuangDuongMucTieu(float quangDuongMucTieu) {
        this.quangDuongMucTieu = quangDuongMucTieu;
    }

    public Float getQuangDuongHoanThanh() {
        return quangDuongHoanThanh;
    }

    public void setQuangDuongHoanThanh(Float quangDuongHoanThanh) {
        this.quangDuongHoanThanh = quangDuongHoanThanh;
    }
}