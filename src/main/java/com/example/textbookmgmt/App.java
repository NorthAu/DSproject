package com.example.textbookmgmt;

import com.example.textbookmgmt.dao.JdbcInventoryTransactionDao;
import com.example.textbookmgmt.dao.JdbcPublisherDao;
import com.example.textbookmgmt.dao.JdbcTextbookDao;
import com.example.textbookmgmt.dao.JdbcTextbookOrderDao;
import com.example.textbookmgmt.dao.JdbcTextbookTypeDao;
import com.example.textbookmgmt.service.InventoryService;
import com.example.textbookmgmt.service.PublisherService;
import com.example.textbookmgmt.service.TextbookOrderService;
import com.example.textbookmgmt.service.TextbookService;
import com.example.textbookmgmt.service.TextbookTypeService;
import com.example.textbookmgmt.ui.TextbookManagementFrame;
import com.example.textbookmgmt.util.DatabaseUtil;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        DatabaseUtil.initializeDatabase();
        JdbcTextbookDao textbookDao = new JdbcTextbookDao();
        JdbcPublisherDao publisherDao = new JdbcPublisherDao();
        JdbcTextbookTypeDao typeDao = new JdbcTextbookTypeDao();
        JdbcTextbookOrderDao orderDao = new JdbcTextbookOrderDao();
        JdbcInventoryTransactionDao inventoryDao = new JdbcInventoryTransactionDao();

        TextbookService service = new TextbookService(textbookDao);
        PublisherService publisherService = new PublisherService(publisherDao);
        TextbookTypeService typeService = new TextbookTypeService(typeDao);
        TextbookOrderService orderService = new TextbookOrderService(orderDao);
        InventoryService inventoryService = new InventoryService(inventoryDao, textbookDao);

        SwingUtilities.invokeLater(() -> {
            TextbookManagementFrame frame = new TextbookManagementFrame(service, publisherService, typeService, orderService, inventoryService);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
