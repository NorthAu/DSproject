package com.example.textbookmgmt.ui;

import com.example.textbookmgmt.entity.Textbook;
import com.example.textbookmgmt.service.InventoryService;
import com.example.textbookmgmt.service.PublisherService;
import com.example.textbookmgmt.service.TextbookOrderService;
import com.example.textbookmgmt.service.TextbookService;
import com.example.textbookmgmt.service.TextbookTypeService;
import com.example.textbookmgmt.ui.model.TextbookTableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TextbookManagementFrame extends JFrame {
    private final TextbookService textbookService;
    private final PublisherService publisherService;
    private final TextbookTypeService typeService;
    private final TextbookOrderService orderService;
    private final InventoryService inventoryService;
    private final TextbookTableModel tableModel = new TextbookTableModel();
    private final JTable table = new JTable(tableModel);
    private final TextbookFormPanel formPanel = new TextbookFormPanel();
    private PublisherPanel publisherPanel;
    private TextbookTypePanel typePanel;
    private OrderPanel orderPanel;
    private InventoryPanel inventoryPanel;
    private final JLabel statusLabel = new JLabel();

    public TextbookManagementFrame(TextbookService textbookService,
                                  PublisherService publisherService,
                                  TextbookTypeService typeService,
                                  TextbookOrderService orderService,
                                  InventoryService inventoryService) {
        super("高校教材管理系统");
        this.textbookService = textbookService;
        this.publisherService = publisherService;
        this.typeService = typeService;
        this.orderService = orderService;
        this.inventoryService = inventoryService;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(12, 12, 12, 12));
        setPreferredSize(new Dimension(1000, 640));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.setRowHeight(26);
        table.getSelectionModel().addListSelectionListener(e -> onTableSelection());

        add(buildTabs(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        reloadAll();
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        JPanel textbookPanel = new JPanel(new BorderLayout());
        textbookPanel.add(formPanel, BorderLayout.NORTH);
        textbookPanel.add(createTablePanel(), BorderLayout.CENTER);
        tabs.addTab("教材", textbookPanel);
        publisherPanel = new PublisherPanel(publisherService);
        typePanel = new TextbookTypePanel(typeService);
        orderPanel = new OrderPanel(textbookService, orderService);
        inventoryPanel = new InventoryPanel(textbookService, inventoryService);
        tabs.addTab("出版社", publisherPanel);
        tabs.addTab("教材类型", typePanel);
        tabs.addTab("订购", orderPanel);
        tabs.addTab("入库/领用", inventoryPanel);
        return tabs;
    }

    private JPanel createButtonPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(new EmptyBorder(4, 0, 0, 0));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));

        JButton addButton = new JButton("新增");
        addButton.setMnemonic('N');
        addButton.addActionListener(e -> onAdd());
        JButton updateButton = new JButton("修改");
        updateButton.setMnemonic('U');
        updateButton.addActionListener(e -> onUpdate());
        JButton deleteButton = new JButton("删除");
        deleteButton.setMnemonic('D');
        deleteButton.addActionListener(e -> onDelete());
        JButton refreshButton = new JButton("刷新");
        refreshButton.setMnemonic('R');
        refreshButton.addActionListener(e -> reloadAll());
        JButton clearButton = new JButton("清空选择");
        clearButton.addActionListener(e -> clearSelection());

        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(refreshButton);
        buttons.add(clearButton);

        statusLabel.setForeground(new Color(70, 70, 70));
        statusLabel.setBorder(new EmptyBorder(0, 4, 0, 0));

        container.add(statusLabel, BorderLayout.WEST);
        container.add(buttons, BorderLayout.EAST);
        return container;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("教材列表"),
                new EmptyBorder(6, 6, 6, 6)));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void onAdd() {
        try {
            Textbook textbook = formPanel.toTextbook(null);
            textbookService.create(textbook);
            reloadAll();
            formPanel.reset();
            JOptionPane.showMessageDialog(this, "新增成功", "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "校验失败", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onUpdate() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选中要修改的行", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            Textbook textbook = formPanel.toTextbook(id);
            textbookService.update(textbook);
            reloadAll();
            formPanel.reset();
            JOptionPane.showMessageDialog(this, "修改成功", "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "校验失败", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选中要删除的行", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除该教材吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            textbookService.delete(id);
            reloadAll();
            formPanel.reset();
        }
    }

    private void reloadAll() {
        List<Textbook> textbooks = textbookService.list();
        tableModel.setData(textbooks);
        updateStatus(textbooks.size());
        formPanel.setPublisherOptions(publisherService.list());
        formPanel.setTypeOptions(typeService.list());
        if (publisherPanel != null) {
            publisherPanel.reload();
        }
        if (typePanel != null) {
            typePanel.reload();
        }
        if (orderPanel != null) {
            orderPanel.reload();
        }
        if (inventoryPanel != null) {
            inventoryPanel.reload();
        }
    }

    private void onTableSelection() {
        int row = table.getSelectedRow();
        if (row >= 0 && row < tableModel.getRowCount()) {
            formPanel.fillFrom(tableModel.getTextbookAt(row));
        }
    }

    private void clearSelection() {
        table.clearSelection();
        formPanel.reset();
        updateStatus(tableModel.getRowCount());
    }

    private void updateStatus(int count) {
        statusLabel.setText(String.format("共 %d 条记录 · 支持列排序与表单同步", count));
    }
}
