package com.example.textbookmgmt.ui;

import com.example.textbookmgmt.entity.InventoryTransaction;
import com.example.textbookmgmt.entity.Textbook;
import com.example.textbookmgmt.service.InventoryService;
import com.example.textbookmgmt.service.TextbookService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class InventoryPanel extends JPanel {
    private final TextbookService textbookService;
    private final InventoryService inventoryService;
    private final JComboBox<Textbook> textbookCombo = new JComboBox<>();
    private final JComboBox<String> directionCombo = new JComboBox<>(new String[]{"IN", "OUT"});
    private final JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
    private final JTextField reasonField = new JTextField(20);
    private final JSpinner occurredDateSpinner = buildDateSpinner();
    private final List<Textbook> textbooks = new ArrayList<>();
    private final InventoryTableModel tableModel = new InventoryTableModel();
    private final JTable table = new JTable(tableModel);

    private boolean filtering;

    public InventoryPanel(TextbookService textbookService, InventoryService inventoryService) {
        super(new BorderLayout(10, 10));
        this.textbookService = textbookService;
        this.inventoryService = inventoryService;
        setBorder(new EmptyBorder(6, 6, 6, 6));

        configureTextbookCombo();
        add(buildForm(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
        reloadAsync();
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("入库/领用"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("教材:"), gbc);
        gbc.gridx = 1;
        textbookCombo.setPreferredSize(new Dimension(240, 26));
        panel.add(textbookCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("方向:"), gbc);
        gbc.gridx = 1;
        panel.add(directionCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("数量:"), gbc);
        gbc.gridx = 1;
        panel.add(quantitySpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("原因:"), gbc);
        gbc.gridx = 1;
        panel.add(reasonField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("时间(yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        panel.add(occurredDateSpinner, gbc);
        return panel;
    }

    private JScrollPane buildTable() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("库存流水"));
        return scrollPane;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        JButton save = new JButton("记录");
        save.addActionListener(e -> onSave());
        JButton refresh = new JButton("刷新");
        refresh.addActionListener(e -> reloadAsync());
        panel.add(save);
        panel.add(refresh);
        return panel;
    }

    private void onSave() {
        try {
            InventoryTransaction tx = new InventoryTransaction();
            Textbook selected = (Textbook) textbookCombo.getSelectedItem();
            tx.setTextbookId(selected == null ? null : selected.getId());
            tx.setDirection((String) directionCombo.getSelectedItem());
            tx.setQuantity((Integer) quantitySpinner.getValue());
            tx.setReason(reasonField.getText());
            tx.setOccurredAt(buildDateTime());
            inventoryService.record(tx);
            reloadAsync();
            JOptionPane.showMessageDialog(this, "库存流水已记录，库存自动同步", "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "校验失败", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void reloadAsync() {
        SwingWorker<InventoryData, Void> worker = new SwingWorker<>() {
            @Override
            protected InventoryData doInBackground() {
                List<Textbook> loadedTextbooks = textbookService.list();
                List<InventoryTransaction> transactions = inventoryService.list();
                return new InventoryData(loadedTextbooks, transactions);
            }

            @Override
            protected void done() {
                try {
                    InventoryData data = get();
                    textbooks.clear();
                    textbooks.addAll(data.textbooks());
                    refreshComboItems();
                    tableModel.setData(data.transactions());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    JOptionPane.showMessageDialog(InventoryPanel.this, "加载库存流水失败: " + ex.getCause().getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private JSpinner buildDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);
        spinner.setPreferredSize(new Dimension(140, 26));
        return spinner;
    }

    private LocalDateTime buildDateTime() {
        if (!(occurredDateSpinner.getValue() instanceof Date date)) {
            return LocalDateTime.now();
        }
        String text = ((JSpinner.DateEditor) occurredDateSpinner.getEditor()).getTextField().getText();
        if (text == null || text.isBlank()) {
            return LocalDateTime.now();
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return LocalDateTime.of(localDate, LocalTime.NOON);
    }

    private void configureTextbookCombo() {
        textbookCombo.setEditable(true);
        textbookCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Textbook textbook) {
                label.setText(textbook.getTitle() + " (" + textbook.getIsbn() + ")");
            }
            return label;
        });

        JTextField editor = (JTextField) textbookCombo.getEditor().getEditorComponent();
        editor.getDocument().addDocumentListener(new SimpleDocumentAdapter(e -> SwingUtilities.invokeLater(this::filterCombo)));
    }

    private void filterCombo() {
        if (filtering) {
            return;
        }
        filtering = true;
        JTextField editor = (JTextField) textbookCombo.getEditor().getEditorComponent();
        String text = editor.getText().toLowerCase();
        DefaultComboBoxModel<Textbook> model = new DefaultComboBoxModel<>();
        for (Textbook textbook : textbooks) {
            String label = (textbook.getTitle() + " " + textbook.getIsbn()).toLowerCase();
            if (text.isBlank() || label.contains(text)) {
                model.addElement(textbook);
            }
        }
        textbookCombo.setModel(model);
        textbookCombo.setSelectedItem(null);
        editor.setText(text);
        if (textbookCombo.isDisplayable() && textbookCombo.isShowing()) {
            textbookCombo.setPopupVisible(true);
        }
        filtering = false;
    }

    private void refreshComboItems() {
        DefaultComboBoxModel<Textbook> model = new DefaultComboBoxModel<>();
        textbooks.forEach(model::addElement);
        textbookCombo.setModel(model);
        textbookCombo.setSelectedItem(null);
    }

    private record InventoryData(List<Textbook> textbooks, List<InventoryTransaction> transactions) {
    }

    static class InventoryTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "教材ID", "方向", "数量", "原因", "时间"};
        private List<InventoryTransaction> data = new ArrayList<>();

        public void setData(List<InventoryTransaction> data) {
            this.data = data;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            InventoryTransaction tx = data.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> tx.getId();
                case 1 -> tx.getTextbookId();
                case 2 -> tx.getDirection();
                case 3 -> tx.getQuantity();
                case 4 -> tx.getReason();
                case 5 -> tx.getOccurredAt();
                default -> null;
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 0, 1 -> Long.class;
                case 3 -> Integer.class;
                default -> Object.class;
            };
        }
    }
}
