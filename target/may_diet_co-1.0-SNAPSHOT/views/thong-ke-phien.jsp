<%@ page contentType="text/html;charset=UTF-8" %>
<%
    // Lấy dữ liệu đã được Servlet chuẩn bị
    String thongKeDataJson = (String) request.getAttribute("thongKeDataJson");
    String maPhien = request.getAttribute("maPhien").toString();
    String maMay = request.getAttribute("maMay").toString();
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"> 
    
    <title>Thống kê phiên #<%= maPhien %></title>
    
    <link rel="stylesheet" href="<%= request.getContextPath() %>/frontend/css/thong-ke-phien.css">
    
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <div class="container">
        <header class="page-header">
            <h1>📊 Biểu đồ thống kê cỏ - Phiên #<%= maPhien %></h1>
            
            <a href="<%= request.getContextPath() %>/lich-su-phien?maPhien=<%= maPhien %>&maMay=<%= maMay %>" class="btn btn-view">
                ← Quay lại lịch sử phiên
            </a>
        </header>
        
        <div class="chart-container">
            <canvas id="myChart"></canvas>
        </div>
    </div>
    
    <script>
        // 3. Lấy dữ liệu JSON từ Servlet
        const jsonData = <%= thongKeDataJson %>;
        
        // 4. Tách dữ liệu thành 2 mảng
        const originalLabels = Object.keys(jsonData);
        const dataValues = Object.values(jsonData);
        
        // 5. Làm tròn nhãn (ví dụ: 0.476000... -> "0.476")
        const formattedLabels = originalLabels.map(label => {
            return parseFloat(label).toFixed(3); 
        });
        
        // 6. Lấy thẻ canvas
        const ctx = document.getElementById('myChart').getContext('2d');
        
        // 7. Vẽ biểu đồ
        new Chart(ctx, {
            type: 'bar', 
            data: {
                labels: formattedLabels, // Dùng nhãn đã làm tròn
                datasets: [{
                    label: 'Số cỏ phát hiện',
                    data: dataValues, 
                    backgroundColor: 'rgba(0, 102, 204, 0.6)', 
                    borderColor: 'rgba(0, 102, 204, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    y: {
                        beginAtZero: true, 
                        title: {
                            display: true,
                            text: 'Số lượng cỏ' 
                        }
                    },
                    x: {
                        title: {
                            display: true,
                            text: 'Vị trí ( m )'
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: false 
                    }
                }
            }
        });
    </script>
</body>
</html>