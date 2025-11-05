//package controller;
//
//import dao.LenhDieuKhienDAO;
//import dao.MayDietCoDAO;
//import model.MayDietCo;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//import java.io.IOException;
//import java.net.Socket;
//import java.io.PrintWriter;
//
//public class LenhDieuKhienServlet extends HttpServlet {
//    private final LenhDieuKhienDAO lenhDAO = new LenhDieuKhienDAO();
//    private final MayDietCoDAO mayDAO = new MayDietCoDAO();
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        String maDinhDanh = request.getParameter("maDinhDanh");
//        String lenh = request.getParameter("lenh");
//        String maMay = request.getParameter("maMay");
//
//        try {
//            // 1️⃣ Gửi lệnh qua socket tới Jetson
//            String jetsonIP = "192.168.1.240"; // đổi theo IP Jetson thật
//            int port = 5000;
//            try (Socket socket = new Socket(jetsonIP, port);
//                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
//                out.println(lenh.equals("BAT") ? "ON" : "OFF");
//            }
//
//            // 2️⃣ Cập nhật trạng thái máy trong DB
//            String trangThai = lenh.equals("BAT") ? "DANG_HOAT_DONG" : "NGUNG_HOAT_DONG";
//            mayDAO.capNhatTrangThai(maDinhDanh, trangThai);
//
//            // 3️⃣ Lưu lịch sử lệnh điều khiển
//            lenhDAO.luuLenh(maMay, lenh);
//
//            // 4️⃣ ✅ Quay lại trang chi tiết máy sau khi xử lý
//            response.sendRedirect(request.getContextPath() + "/may-detail?id=" + maMay);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi gửi lệnh điều khiển");
//        }
//    }
//}
