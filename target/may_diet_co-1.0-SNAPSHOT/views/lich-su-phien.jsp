<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*,model.LichSuCo,model.PhienHoatDong" %>
<%
    PhienHoatDong phien = (PhienHoatDong) request.getAttribute("phien");
    List<LichSuCo> lichSuList = (List<LichSuCo>) request.getAttribute("lichSuList");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lịch sử cỏ của phiên #<%= phien.getMaPhien() %></title>
    
    <link rel="stylesheet" href="<%= request.getContextPath() %>/frontend/css/lich-su-phien.css">
</head>
<body>
    <div class="container">
        <header class="page-header">
            <h1>Lịch sử cỏ của phiên #<%= phien.getMaPhien() %></h1>
            <a href="<%= request.getContextPath() %>/may-detail?id=<%= request.getAttribute("maMay") %>" class="btn btn-view">
                ← Quay lại chi tiết máy
            </a>
        </header>

        <div style="background: var(--light-color); padding: 15px 20px; border-radius: 8px; border: 1px solid var(--border-color); margin-bottom: 20px;">
            <p><b>Mã máy:</b> <%= phien.getMaDinhDanh() %></p>
            <p><b>Thời gian bắt đầu:</b> <%= phien.getThoiGianBat() %></p>
            <p><b>Thời gian kết thúc:</b> <%= phien.getThoiGianTat() != null ? phien.getThoiGianTat() : "Đang hoạt động" %></p>
        </div>

        <h3 style="color:var(--primary-color); margin-top:30px; margin-bottom: 20px;">Dữ liệu phát hiện & diệt cỏ</h3>

        <table class="machine-table">
            <thead>
                <tr>
                    <th>Thời gian</th>
                    <th>Số cỏ phát hiện</th>
                    <th>Thứ tự</th>
                    <th>Ảnh minh họa</th>
                </tr>
            </thead>
            <tbody>
                <%
                    if (lichSuList != null && !lichSuList.isEmpty()) {
                        for (LichSuCo ls : lichSuList) {
                %>
                <tr>
                    <td data-label="Thời gian"><%= ls.getThoiGian() %></td>
                    <td data-label="Số cỏ"><%= ls.getSoCoPhatHien() %></td>
                    <td data-label="Thứ tự"><%= ls.getViTri() %></td>
                    <td data-label="Ảnh">
                        <% if (ls.getDuongDanAnh() != null && !ls.getDuongDanAnh().isEmpty()) { %>
                            <img src="<%= request.getContextPath() %>/<%= ls.getDuongDanAnh() %>" height="80" class="preview-img"/>
                        <% } else { %>
                            <i>Không có ảnh</i>
                        <% } %>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="4" class="no-data">Chưa có dữ liệu cỏ nào được ghi nhận cho phiên này</td>
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>
    </div>

    <div id="imageModal" class="image-modal-overlay">
        <span class="modal-close">&times;</span>
        <img class="modal-content" id="modalImage">
    </div>


    <script>
    // Hiệu ứng chuyển trang mượt
    document.querySelectorAll('a').forEach(link => {
        link.addEventListener('click', function (e) {
            const href = this.getAttribute('href');
            if (!href || href.startsWith('#') || href.startsWith('http')) return; 

            e.preventDefault();
            document.body.classList.add('fade-out');
            
            setTimeout(() => {
                window.location.href = href;
            }, 700); 
        });
    });

    /*
    =========================================
    ✅ JAVASCRIPT CHO MODAL XEM ẢNH
    =========================================
    */
    
    // 1. Lấy các phần tử modal
    const modalOverlay = document.getElementById("imageModal");
    const modalImage = document.getElementById("modalImage");
    const closeModal = document.querySelector(".modal-close");

    // 2. Lấy tất cả các ảnh có class "preview-img"
    const previewImages = document.querySelectorAll(".preview-img");

    // 3. Thêm sự kiện click cho mỗi ảnh
    previewImages.forEach(img => {
        img.addEventListener("click", function() {
            modalOverlay.style.display = "flex"; // Bật flexbox để căn giữa
            
            setTimeout(() => { // Cần một độ trễ nhỏ để transition hoạt động
                modalOverlay.classList.add("show");
            }, 10); 
            
            modalImage.src = this.src; 
        });
    });

    // 4. Hàm để đóng modal
    function closeImageModal() {
        modalOverlay.classList.remove("show"); // Tắt hiệu ứng mờ
        
        setTimeout(() => { // Đợi hiệu ứng mờ tắt (300ms) rồi mới ẩn
            modalOverlay.style.display = "none";
        }, 300); 
    }

    // 5. Gán sự kiện click cho nút đóng (dấu X)
    closeModal.addEventListener("click", closeImageModal);

    // 6. Gán sự kiện click cho nền mờ (click ra ngoài để tắt)
    modalOverlay.addEventListener("click", function(event) {
        if (event.target === modalOverlay) {
            closeImageModal();
        }
    });

</script>
</body>
</html>