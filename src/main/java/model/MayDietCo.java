package model;

import java.sql.Timestamp;

public class MayDietCo {
    private int maMay;
    private String maDinhDanh;
    private String tenMay;
    private String trangThai;
    private Timestamp lanCuoiHoatDong;

    // Getters & Setters
    public int getMaMay() { return maMay; }
    public void setMaMay(int maMay) { this.maMay = maMay; }

    public String getMaDinhDanh() { return maDinhDanh; }
    public void setMaDinhDanh(String maDinhDanh) { this.maDinhDanh = maDinhDanh; }

    public String getTenMay() { return tenMay; }
    public void setTenMay(String tenMay) { this.tenMay = tenMay; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public Timestamp getLanCuoiHoatDong() { return lanCuoiHoatDong; }
    public void setLanCuoiHoatDong(Timestamp lanCuoiHoatDong) { this.lanCuoiHoatDong = lanCuoiHoatDong; }
}
