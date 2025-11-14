<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*,model.MayDietCo,model.PhienHoatDong" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<%
    // --- Lấy dữ liệu từ Servlet ---
    MayDietCo may = (MayDietCo) request.getAttribute("may");
    List<PhienHoatDong> phienList = (List<PhienHoatDong>) request.getAttribute("phienList");
    
    // --- Lấy các trạng thái ---
    String trangThai = may.getTrangThai();
    boolean isOnline = (request.getAttribute("isOnline") != null && (Boolean) request.getAttribute("isOnline"));
    
    // --- Biến kiểm soát trạng thái ---
    boolean isRunning = "DANG_HOAT_DONG".equals(trangThai);
    boolean isPaused = "TAM_DUNG".equals(trangThai);
    boolean isStopped = "NGUNG_HOAT_DONG".equals(trangThai);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết máy <%= may.getTenMay() %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/frontend/css/may_list.css">
    
    <style>
        /* CSS cho các khối điều khiển */
        .control-buttons { margin-top: 20px; }
        .control-block { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
        .control-block label { font-weight: bold; font-size: 1em; }
        .control-block input[type="number"] { width: 80px; padding: 8px; border: 1px solid #ccc; border-radius: 4px; font-size: 1em; }
        
        /* CSS cho các nút mới */
        .btn-pause { background-color: #ffc107; color: black; }
        .btn-resume { background-color: #28a745; color: white; }

        /* CSS cho nút bị vô hiệu hóa */
        button:disabled, input:disabled {
            background-color: #cccccc !important;
            color: #666666 !important;
            cursor: not-allowed;
            border-color: #999999;
        }
        
        /* Căn chỉnh cho cột hành động có nhiều nút */
        .machine-table .actions {
            display: flex;
            gap: 8px;
            justify-content: center;
            flex-wrap: wrap;
        }
    </style>
</head>
<body>
    <div class="container">
        <header class="page-header">
            <h1>Chi tiết Máy diệt cỏ</h1>
            <div class="top-buttons">
                <a href="<%= request.getContextPath() %>/may-list" class="btn btn-view">← Quay lại danh sách</a>
                <a href="<%= request.getContextPath() %>/logout" class="btn btn-logout">Đăng xuất</a>
            </div>
        </header>

        <div class="machine-detail">
            <h2><%= may.getTenMay() %></h2>
            <p><b>Mã định danh:</b> <%= may.getMaDinhDanh() %></p>
            
            <%-- HIỂN THỊ TRẠNG THÁI MÁY --%>
            <p><b>Trạng thái:</b> 
                <% if (isRunning) { %>
                    <span class="status-on"><%= trangThai %></span>
                <% } else if (isPaused) { %>
                    <span style="color: #ffc107; font-weight: bold;"><%= trangThai %></span>
                <% } else { %>
                    <span class="status-off"><%= trangThai %></span>
                <% } %>
            </p>

            <%-- HIỂN THỊ TRẠNG THÁI KẾT NỐI --%>
            <p><b>Trạng thái kết nối:</b>
                <% if (isOnline) { %>
                    <span style="color: #28a745; font-weight: bold;">Đã kết nối (Online)</span>
                <% } else { %>
                    <span style="color: #dc3545; font-weight: bold;">Mất kết nối (Offline)</span>
                <% } %>
            </p>

            <%-- KHỐI FORM ĐIỀU KHIỂN --%>
            <form method="post" action="<%= request.getContextPath()%>/may-detail?id=<%= may.getMaMay()%>" class="control-buttons">
                
                <%-- ==== TRƯỜNG HỢP 1: MÁY ĐANG DỪNG ==== --%>
                <% if (isStopped) { %>
                    <div class="control-block start-control">
                        <label for="quangDuong">Quãng đường (m):</label>
                        <input type="number" name="quangDuongMucTieu" id="quangDuong" value="20" min="1" 
                               <%= !isOnline ? "disabled" : "" %> >
                        
                        <button type="submit" name="action" value="START" class="btn btn-add" 
                                <%= !isOnline ? "disabled" : "" %> >
                            Bắt đầu phiên 
                        </button>
                    </div>
                <% } %>

                <%-- ==== TRƯỜNG HỢP 2: MÁY ĐANG CHẠY ==== --%>
                <% if (isRunning) { %>
                    <div class="control-block running-control">
                        <button type="submit" name="action" value="PAUSE" class="btn btn-view btn-pause" 
                                <%= !isOnline ? "disabled" : "" %> >
                            ⏸ Tạm dừng
                        </button>
                        <button type="submit" name="action" value="STOP" class="btn btn-delete" 
                                <%= !isOnline ? "disabled" : "" %> >
                            ⏹ Kết thúc phiên 
                        </button>
                    </div>
                <% } %>
                
                <%-- ==== TRƯỜNG HỢP 3: MÁY ĐANG TẠM DỪNG ==== --%>
                <% if (isPaused) { %>
                    <div class="control-block paused-control">
                        <button type="submit" name="action" value="RESUME" class="btn btn-add btn-resume" 
                                <%= !isOnline ? "disabled" : "" %> >
                            ▶️ Tiếp tục
                        </button>
                        <button type="submit" name="action" value="STOP" class="btn btn-delete" 
                                <%= !isOnline ? "disabled" : "" %> >
                            ⏹ Kết thúc phiên 
                        </button>
                    </div>
                <% } %>
                
                <%-- Thông báo lỗi nếu Offline --%>
                <% if (!isOnline) { %>
                    <p style="color: #dc3545; margin-top: 10px;">Không thể điều khiển do máy đang Offline.</p>
                <% } %>
            </form>
        </div>

        <h3>Danh sách các phiên hoạt động</h3>
        <table class="machine-table">
            <thead>
                <tr>
                    <th>Mã phiên</th>
                    <th>Thời gian bắt đầu</th>
                    <th>Thời gian kết thúc</th>
                    <th>Quãng đường (Hoàn thành / Mục tiêu)</th> 
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <%
                    if (phienList != null && !phienList.isEmpty()) {
                        for (PhienHoatDong phien : phienList) {
                %>
                <tr>
                    <td><%= phien.getMaPhien() %></td>
                    <td><%= phien.getThoiGianBat() %></td>
                    
                    <%-- Cột Thời gian kết thúc (Cập nhật logic hiển thị) --%>
                    <td>
                        <% if (phien.getThoiGianTat() != null) { %>
                            <%= phien.getThoiGianTat() %>
                        <% } else { %>
                            <span style="color: #007bff; font-weight: bold;">Đang hoạt động</span>
                        <% } %>
                    </td>
                    
                    <%-- Cột Quãng đường (Logic của bạn giữ nguyên) --%>
                    <td>
                        <%
                            String mucTieu = String.format("%.1f", phien.getQuangDuongMucTieu()); 
                            if (phien.getQuangDuongHoanThanh() == null) {
                                if (phien.getThoiGianTat() == null) {
                        %>
                                <span style="color: #007bff; font-weight: bold;">... / <%= mucTieu %> m</span>
                        <%
                                } else {
                                    out.print("... / 0.0 m");
                                }
                            } else {
                                float hoanThanh = phien.getQuangDuongHoanThanh();
                                if (hoanThanh < 0) {
                        %>
                                <b style="color: #dc3545;">Lỗi (Mất kết nối)</b>
                        <%
                                } else {
                                    String hoanThanhStr = String.format("%.1f", hoanThanh);
                        %>
                                <b style="color: #28a745;"><%= hoanThanhStr %> m / <%= mucTieu %> m</b>
                        <%
                                }
                            }
                        %>
                    </td>
                    
                    <td class="actions">
                        <%-- Nút "Xem chi tiết" luôn hiển thị --%>
                        <a href="<%= request.getContextPath() %>/lich-su-phien?maPhien=<%= phien.getMaPhien() %>&maMay=<%= may.getMaMay() %>" class="btn btn-view">
                            Xem chi tiết
                        </a>
                        
                        <%-- 
                          LOGIC MỚI:
                          Chỉ hiển thị nút "Xóa" nếu phien.getThoiGianTat() KHÁC NULL
                          (Tức là phiên đã kết thúc)
                        --%>
                        <% if (phien.getThoiGianTat() != null) { %>
                            <a href="<%= request.getContextPath() %>/may-delete-phien?maPhien=<%= phien.getMaPhien() %>&maMay=<%= may.getMaMay() %>" 
                               class="btn btn-delete"
                               onclick="return confirm('Bạn có chắc muốn xóa phiên #<%= phien.getMaPhien() %> này không?');">
                                Xóa
                            </a>
                        <% } %>
                        <%-- Nếu phiên đang chạy (thoiGianTat == null), nút Xóa sẽ không được hiển thị --%>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="5" class="no-data">Chưa có phiên hoạt động nào.</td> 
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>
    </div>
</body>
</html>