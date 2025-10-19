<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Đăng nhập hệ thống Máy diệt cỏ</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/frontend/css/login.css">
    </head>
    <body>
        <div class="login-container">
            <h2>🚜 Đăng nhập hệ thống Máy diệt cỏ  </h2>
            <form action="login" method="post" class="login-form">
                <label for="username">Tên đăng nhập</label>
                <input type="text" id="username" name="username" required placeholder="Nhập tên đăng nhập..."/>

                <label for="password">Mật khẩu</label>
                <input type="password" id="password" name="password" required placeholder="Nhập mật khẩu..."/>

                <button type="submit">Đăng nhập</button>
                <p class="error">${error}</p>
            </form>
            <a href="<%= request.getContextPath()%>/register" class="register-link">Tạo tài khoản mới</a>
        </div>
    </div>
    <script>
        document.querySelector(".login-form").addEventListener("submit", function (e) {
            e.preventDefault();

            document.body.classList.add("fade-out"); // thêm hiệu ứng
            // ép trình duyệt render lại ngay lập tức
            void document.body.offsetWidth;

            // chờ 700ms rồi mới submit
            setTimeout(() => {
                this.submit();
            }, 700);
        });
    </script>
</body>
</html>
