/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.time.LocalDateTime;
/**
 *
 * @author ADMIN
 */
public class LichSuCo {
    
    private int maLichSu;
    private String maDinhDanh;
    private int soCoPhatHien;
    private int soCoDiet;
    private String viTri;
    private LocalDateTime thoiGian;
    private String duongDanAnh;

    // Getters & Setters
    public int getMaLichSu() {
        return maLichSu;
    }

    public void setMaLichSu(int maLichSu) {
        this.maLichSu = maLichSu;
    }

    public String getMaDinhDanh() {
        return maDinhDanh;
    }

    public void setMaDinhDanh(String maDinhDanh) {
        this.maDinhDanh = maDinhDanh;
    }

    public int getSoCoPhatHien() {
        return soCoPhatHien;
    }

    public void setSoCoPhatHien(int soCoPhatHien) {
        this.soCoPhatHien = soCoPhatHien;
    }

    public int getSoCoDiet() {
        return soCoDiet;
    }

    public void setSoCoDiet(int soCoDiet) {
        this.soCoDiet = soCoDiet;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public LocalDateTime getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(LocalDateTime thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getDuongDanAnh() {
        return duongDanAnh;
    }

    public void setDuongDanAnh(String duongDanAnh) {
        this.duongDanAnh = duongDanAnh;
    }
}

