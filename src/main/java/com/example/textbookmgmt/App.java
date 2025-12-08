package com.example.textbookmgmt;

import com.example.textbookmgmt.dao.JdbcTextbookDao;
import com.example.textbookmgmt.service.TextbookService;
import com.example.textbookmgmt.ui.TextbookManagementFrame;
import com.example.textbookmgmt.util.DatabaseUtil;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        DatabaseUtil.initializeDatabase();
        JdbcTextbookDao dao = new JdbcTextbookDao();
        TextbookService service = new TextbookService(dao);

        SwingUtilities.invokeLater(() -> {
            TextbookManagementFrame frame = new TextbookManagementFrame(service);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
