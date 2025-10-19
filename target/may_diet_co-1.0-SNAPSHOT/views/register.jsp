<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng ký tài khoản</title>
    <style>
        /* ==== Giao diện tổng thể ==== */
        body {
            font-family: "Segoe UI", sans-serif;
            background: #f6f7f8;
            margin: 0;
            color: #222;
        }

        .container {
            max-width: 450px;
            margin: 80px auto;
            background: #fff;
            padding: 40px 50px;
            border-radius: 14px;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
        }

        h2 {
            text-align: center;
            color: #333;
            margin-bottom: 30px;
        }

        label {
            font-weight: 600;
            display: block;
            margin-top: 15px;
        }

        input[type="text"],
        input[type="password"] {
            width: 100%;
            padding: 12px;
            margin-top: 6px;
            border: 1px solid #ccc;
            border-radius: 8px;
            font-size: 15px;
            transition: 0.3s;
        }

        input[type="text"]:focus,
        input[type="password"]:focus {
            border-color: #ff7b00;
            box-shadow: 0 0 5px rgba(255, 123, 0, 0.4);
            outline: none;
        }

        .btn {
            width: 100%;
            background-color: #ff7b00;
            color: white;
            border: none;
            padding: 12px;
            border-radius: 8px;
            margin-top: 25px;
            font-size: 16px;
            cursor: pointer;
            transition: background 0.3s;
        }

        .btn:hover {
            background-color: #e56a00;
        }

        .error {
            color: #d9534f;
            background-color: #f8d7da;
            padding: 10px 15px;
            border-radius: 8px;
            margin-bottom: 15px;
            text-align: center;
        }

        .login-link {
            text-align: center;
            margin-top: 20px;
        }

        .login-link a {
            color: #ff7b00;
            text-decoration: none;
            font-weight: 600;
        }

        .login-link a:hover {
            text-decoration: underline;
        }
    </style>
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
