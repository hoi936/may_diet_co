package model;

import java.time.LocalDateTime;

public class PhienHoatDong {
    private int maPhien;
    private String maDinhDanh;
    private LocalDateTime thoiGianBat;
    private LocalDateTime thoiGianTat;

    public PhienHoatDong() {}

    public PhienHoatDong(int maPhien, String maDinhDanh, LocalDateTime thoiGianBat, LocalDateTime thoiGianTat) {
        this.maPhien = maPhien;
        this.maDinhDanh = maDinhDanh;
        this.thoiGianBat = thoiGianBat;
        this.thoiGianTat = thoiGianTat;
    }

    public int getMaPhien() { return maPhien; }
    public void setMaPhien(int maPhien) { this.maPhien = maPhien; }

    public String getMaDinhDanh() { return maDinhDanh; }
    public void setMaDinhDanh(String maDinhDanh) { this.maDinhDanh = maDinhDanh; }

    public LocalDateTime getThoiGianBat() { return thoiGianBat; }
    public void setThoiGianBat(LocalDateTime thoiGianBat) { this.thoiGianBat = thoiGianBat; }

    public LocalDateTime getThoiGianTat() { return thoiGianTat; }
    public void setThoiGianTat(LocalDateTime thoiGianTat) { this.thoiGianTat = thoiGianTat; }
}
