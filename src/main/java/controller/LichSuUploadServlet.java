package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import util.DBConnect;

/**
 * Servlet này xử lý việc upload file ảnh và dữ liệu lịch sử cỏ
 * từ máy Jetson (Client) qua HTTP POST.
 * * Nó lắng nghe tại URL: /api/upload-lich-su
 */
@WebServlet("/api/upload-lich-su")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1,  // 1 MB (dữ liệu tạm)
    maxFileSize = 1024 * 1024 * 10, // 10 MB (tối đa 1 file)
    maxRequestSize = 1024 * 1024 * 15 // 15 MB (tối đa 1 request)
)
public class LichSuUploadServlet extends HttpServlet {
    
    // Thư mục con trong webapp để lưu ảnh. Ví dụ: /may_diet_co/Images/
    private static final String UPLOAD_DIR = "Images"; 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Lấy dữ liệu dạng text (form-data)
            String maDinhDanh = request.getParameter("ma_dinh_danh");
            String soCoPhatHien = request.getParameter("so_co_phat_hien");
            String soCoDiet = request.getParameter("so_co_diet");
            String maPhien = request.getParameter("ma_phien");
            String viTri = request.getParameter("vi_tri");

            // Xử lý file ảnh
            Part filePart = request.getPart("anh"); // "anh" là key mà Python gửi
            String originalFileName = filePart.getSubmittedFileName(); // Lấy tên file gốc (vd: abc.png)
            
            // Tạo tên file duy nhất trên server để tránh ghi đè
            String fileName = "anh_" + maDinhDanh + "_" + System.currentTimeMillis() + "_" + originalFileName;
            
            // Đường dẫn tuyệt đối trên server để lưu file
            // getServletContext().getRealPath("") -> đường dẫn gốc của webapp
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
            
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // Tạo thư mục /Images nếu chưa có
            }

            // Ghi file vào thư mục
            filePart.write(uploadPath + File.separator + fileName);
            
            // Đường dẫn tương đối để lưu vào CSDL (ví dụ: "Images/anh_JETSON004_123456.png")
            String dbPath = UPLOAD_DIR + "/" + fileName;

            // Ghi vào DB
            try (Connection conn = DBConnect.getConnection()) {
                
                // ✅ ĐÃ SỬA: Bỏ ma_lich_su (để CSDL tự tăng) và thoi_gian (dùng hàm NOW() của SQL)
                String sql = "INSERT INTO lich_su_co(ma_dinh_danh, so_co_phat_hien, so_co_diet, vi_tri, thoi_gian, duong_dan_anh, ma_phien) "
                           + "VALUES (?, ?, ?, ?, NOW(), ?, ?)";
                
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, maDinhDanh);
                ps.setString(2, soCoPhatHien);
                ps.setString(3, soCoDiet);
                ps.setString(4, viTri);
                ps.setString(5, dbPath); // Lưu đường dẫn tương đối
                ps.setString(6, maPhien);
                
                ps.executeUpdate();

                // Trả về kết quả thành công
                out.print("{\"status\":\"ok\", \"file_saved_as\":\"" + dbPath + "\"}");
                
            } catch (SQLException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"status\":\"error\", \"message\":\"Loi SQL: " + e.getMessage() + "\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\", \"message\":\"Loi Servlet: " + e.getMessage() + "\"}");
        }
    }
}