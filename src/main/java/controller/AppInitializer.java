package controller;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppInitializer implements ServletContextListener {

    private SocketServerManager server;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        server = new SocketServerManager();
        server.start();
        System.out.println("✅ [AppInitializer] Socket Server đã khởi động và lắng nghe kết nối...");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            if (server != null && server.isAlive()) {
                server.shutdownServer(); // Gọi hàm dừng chính thức
                server.join(1000); // Đợi tối đa 1s để thread dừng hẳn
            }
            System.out.println("🛑 [AppInitializer] Socket Server đã dừng hoàn toàn.");
        } catch (InterruptedException e) {
            System.err.println("⚠️ Lỗi khi dừng server: " + e.getMessage());
        }
    }
}