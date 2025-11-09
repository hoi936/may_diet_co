<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*,model.MayDietCo,model.PhienHoatDong" %>
<%-- ✅ THÊM THƯ VIỆN JSTL (Nếu bạn chưa có) 
     Nếu bạn không dùng JSTL, code của tôi bên dưới dùng <% %> scriptlet nên vẫn chạy OK
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<%
    MayDietCo may = (MayDietCo) request.getAttribute("may");
    List<PhienHoatDong> phienList = (List<PhienHoatDong>) request.getAttribute("phienList");
    
    // Kiểm tra trạng thái máy
    boolean isRunning = "DANG_HOAT_DONG".equals(may.getTrangThai());
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết máy <%= may.getTenMay() %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/frontend/css/may_list.css">
    
    <%-- ✅ CSS ĐƠN GIẢN CHO FORM MỚI --%>
    <style>
        .start-control {
            display: flex;
            align-items: center;
            gap: 10px; /* Khoảng cách giữa các phần tử */
        }
        .start-control label {
            font-weight: bold;
            font-size: 1em;
        }
        .start-control input[type="number"] {
            width: 80px;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 1em;
        }
        .control-buttons {
            margin-top: 20px;
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

            <%-- ✅ FORM ĐIỀU KHIỂN ĐÃ CẬP NHẬT --%>
            <form method="post" action="<%= request.getContextPath()%>/may-detail?id=<%= may.getMaMay()%>" class="control-buttons">
                
                <%-- KHỐI "BẬT MÁY" - Chỉ hiển thị khi máy đang dừng --%>
                <% if (!isRunning) { %>
                    <div class="start-control">
                        <label for="quangDuong">Quãng đường (m):</label>
                        <%-- Đây là ô input quan trọng, Servlet sẽ đọc "name" của nó --%>
                        <input type="number" name="quangDuongMucTieu" id="quangDuong" value="20" min="1">
                        
                        <button type="submit" name="action" value="START" class="btn btn-add">
                            Bật máy
                        </button>
                    </div>
                <% } %>

                <%-- KHỐI "TẮT MÁY" - Chỉ hiển thị khi máy đang chạy --%>
                <% if (isRunning) { %>
                    <div class="stop-control">
                        <button type="submit" name="action" value="STOP" class="btn btn-delete">
                            Tắt máy (Dừng thủ công)
                        </button>
                    </div>
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
                    <%-- ✅ CỘT MỚI --%>
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
                    
                    <%-- ✅ LOGIC HIỂN THỊ QUÃNG ĐƯỜNG MỚI --%>
                    <td>
                        <%
                            // Format số thành 1 chữ số thập phân (ví dụ: 20.0)
                            String mucTieu = String.format("%.1f", phien.getQuangDuongMucTieu()); 
                            
                            if (phien.getQuangDuongHoanThanh() == null) {
                                // Đang chạy (hoặc phiên cũ không có dữ liệu)
                                if (phien.getThoiGianTat() == null) {
                        %>
                                <span style="color: #007bff; font-weight: bold;">... / <%= mucTieu %> m</span>
                        <%
                                } else {
                                    // Phiên cũ từ trước khi nâng cấp
                                    out.print("... / 0.0 m");
                                }
                            } else {
                                // Đã kết thúc
                                String hoanThanh = String.format("%.1f", phien.getQuangDuongHoanThanh());
                        %>
                                <b style="color: #28a745;"><%= hoanThanh %> m / <%= mucTieu %> m</b>
                        <%
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
                    <%-- ✅ Cập nhật colspan="5" --%>
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