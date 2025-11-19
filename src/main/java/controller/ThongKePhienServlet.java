package controller;

import dao.LichSuCoDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Map;
import org.json.JSONObject; // Thư viện bạn đã có trong pom.xml

public class ThongKePhienServlet extends HttpServlet {
    private final LichSuCoDAO lichSuDAO = new LichSuCoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int maPhien = Integer.parseInt(request.getParameter("maPhien"));
            String maMay = request.getParameter("maMay");

            // 1. Lấy dữ liệu thống kê từ DAO
            Map<String, Integer> data = lichSuDAO.getStatisticsByPhien(maPhien);

            // 2. Chuyển Map thành JSON
            // Thư viện org.json sẽ tự động chuyển Map<String, Integer> thành {"key1": val1, "key2": val2}
            JSONObject jsonData = new JSONObject(data);

            // 3. Gửi JSON (dưới dạng String) và các ID sang JSP
            request.setAttribute("thongKeDataJson", jsonData.toString());
            request.setAttribute("maPhien", maPhien);
            request.setAttribute("maMay", maMay); // Để quay lại

            // 4. Forward sang trang JSP mới
            request.getRequestDispatcher("/views/thong-ke-phien.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tham số không hợp lệ.");
        }
    }
}