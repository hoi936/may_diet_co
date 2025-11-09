<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*,model.MayDietCo,model.PhienHoatDong" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<%
    MayDietCo may = (MayDietCo) request.getAttribute("may");
    List<PhienHoatDong> phienList = (List<PhienHoatDong>) request.getAttribute("phienList");
    
    // Kiểm tra trạng thái máy
    boolean isRunning = "DANG_HOAT_DONG".equals(may.getTrangThai());
    
    // ✅ Lấy biến isOnline từ Servlet (phải kiểm tra null)
    boolean isOnline = (request.getAttribute("isOnline") != null && (Boolean) request.getAttribute("isOnline"));
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết máy <%= may.getTenMay() %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/frontend/css/may_list.css">
    
    <style>
        .start-control { display: flex; align-items: center; gap: 10px; }
        .start-control label { font-weight: bold; font-size: 1em; }
        .start-control input[type="number"] { width: 80px; padding: 8px; border: 1px solid #ccc; border-radius: 4px; font-size: 1em; }
        .control-buttons { margin-top: 20px; }
        
        /* CSS cho nút bị vô hiệu hóa */
        button:disabled, input:disabled {
            background-color: #cccccc;
            cursor: not-allowed;
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
            <p><b>Trạng thái:</b> 
                <span class="<%= isRunning ? "status-on" : "status-off" %>">
                    <%= may.getTrangThai() %>
                </span>
            </p>

            <%-- ✅ HIỂN THỊ TRẠNG THÁI KẾT NỐI --%>
            <p><b>Trạng thái kết nối:</b>
                <% if (isOnline) { %>
                    <span style="color: #28a745; font-weight: bold;">Đã kết nối (Online)</span>
                <% } else { %>
                    <span style="color: #dc3545; font-weight: bold;">Mất kết nối (Offline)</span>
                <% } %>
            </p>

            <%-- ✅ FORM ĐIỀU KHIỂN ĐÃ CẬP NHẬT LOGIC 'disabled' --%>
            <form method="post" action="<%= request.getContextPath()%>/may-detail?id=<%= may.getMaMay()%>" class="control-buttons">
                
                <%-- KHỐI "BẬT MÁY" --%>
                <% if (!isRunning) { %>
                    <div class="start-control">
                        <label for="quangDuong">Quãng đường (m):</label>
                        <input type="number" name="quangDuongMucTieu" id="quangDuong" value="20" min="1" 
                               <%= !isOnline ? "disabled" : "" %> > <%-- Vô hiệu hóa nếu Offline --%>
                        
                        <button type="submit" name="action" value="START" class="btn btn-add" 
                                <%= !isOnline ? "disabled" : "" %> > <%-- Vô hiệu hóa nếu Offline --%>
                            Bật máy
                        </button>
                    </div>
                <% } %>

                <%-- KHỐI "TẮT MÁY" --%>
                <% if (isRunning) { %>
                    <div class="stop-control">
                        <button type="submit" name="action" value="STOP" class="btn btn-delete" 
                                <%= !isOnline ? "disabled" : "" %> > <%-- Vô hiệu hóa nếu Offline --%>
                            Tắt máy (Dừng thủ công)
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
                    <td><%= phien.getThoiGianTat() != null ? phien.getThoiGianTat() : "Đang hoạt động" %></td>
                    
                    <%-- ✅✅✅ LOGIC NÀY ĐÃ ĐƯỢC CẬP NHẬT ĐỂ XỬ LÝ LỖI ✅✅✅ --%>
                    <td>
                        <%
                            String mucTieu = String.format("%.1f", phien.getQuangDuongMucTieu()); 
                            
                            if (phien.getQuangDuongHoanThanh() == null) {
                                // Trường hợp 1: Đang chạy (NULL và thời gian tắt cũng NULL)
                                if (phien.getThoiGianTat() == null) {
                        %>
                                <span style="color: #007bff; font-weight: bold;">... / <%= mucTieu %> m</span>
                        <%
                                } else {
                                    // Trường hợp 2: Phiên cũ (NULL nhưng đã có thời gian tắt)
                                    out.print("... / 0.0 m");
                                }
                            } else {
                                // Trường hợp 3: Đã kết thúc (có giá trị)
                                float hoanThanh = phien.getQuangDuongHoanThanh();
                                
                                if (hoanThanh < 0) {
                                    // ✅ TRẠNG THÁI LỖI MỚI (ví dụ: -1.0)
                        %>
                                <b style="color: #dc3545;">Lỗi (Mất kết nối)</b>
                        <%
                                } else {
                                    // ✅ Trạng thái hoàn thành bình thường
                                    String hoanThanhStr = String.format("%.1f", hoanThanh);
                        %>
                                <b style="color: #28a745;"><%= hoanThanhStr %> m / <%= mucTieu %> m</b>
                        <%
                                }
                            }
                        %>
                    </td>
                    
                    <td>
                        <a href="<%= request.getContextPath() %>/lich-su-phien?maPhien=<%= phien.getMaPhien() %>&maMay=<%= may.getMaMay() %>" class="btn btn-view">
                            Xem chi tiết
                        </a>
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