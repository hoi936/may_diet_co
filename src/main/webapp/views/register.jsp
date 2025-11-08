<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng ký tài khoản</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/frontend/css/register.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">
</head>
<body>
<div class="container">
    <h2>Tạo tài khoản mới</h2>

    <% String error = (String) request.getAttribute("error"); %>
    <% if (error != null) { %>
        <div class="error"><%= error %></div>
    <% } %>

    <form action="<%= request.getContextPath() %>/register" method="post">
        <label for="username">Tên đăng nhập:</label>
        <input type="text" id="username" name="username" required>

        <label for="password">Mật khẩu:</label>
        <input type="password" id="password" name="password" required>

        <button type="submit" class="btn">Đăng ký</button>
    </form>

    <div class="login-link">
        <p>Đã có tài khoản? <a href="<%= request.getContextPath() %>/login">Đăng nhập</a></p>
    </div>
</div>
</body>
</html>
