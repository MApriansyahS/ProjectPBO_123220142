import javax.swing.*;
import javax.swing.table.*;
import org.jdatepicker.impl.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

public class TaskView extends JFrame {
    private TaskController controller;
    private DefaultTableModel tableModel;
    private JTable taskTable;
    private JComboBox<String> filterComboBox;
    private JTextField descriptionField;
    private JDatePickerImpl datePicker;
    private JComboBox<String> priorityComboBox;

    public TaskView(TaskController controller) {
        this.controller = controller;

        setTitle("Aplikasi Pengingat Tugas (To-Do List)");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadTasksFromDB();

        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Filter combo box
        filterComboBox = new JComboBox<>(new String[] {"Semua Tugas", "Belum Selesai", "Sudah Selesai"});
        filterComboBox.addActionListener(e -> loadTasksFromDB());
        panel.add(filterComboBox, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new Object[] {"ID", "Deskripsi", "Deadline", "Selesai", "Prioritas"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // semua sel tidak bisa diedit langsung
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Boolean.class;
                if (columnIndex == 0 || columnIndex == 4) return Integer.class;
                return String.class;
            }
        };
        taskTable = new JTable(tableModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Sembunyikan kolom ID
        taskTable.getColumnModel().getColumn(0).setMinWidth(0);
        taskTable.getColumnModel().getColumn(0).setMaxWidth(0);
        taskTable.getColumnModel().getColumn(0).setWidth(0);

        // Renderer prioritas dengan warna
        taskTable.getColumnModel().getColumn(4).setCellRenderer(new PriorityCellRenderer());

        JScrollPane scrollPane = new JScrollPane(taskTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Input form bawah
        JPanel inputPanel = new JPanel();

        descriptionField = new JTextField(20);
        inputPanel.add(new JLabel("Deskripsi:"));
        inputPanel.add(descriptionField);

        // Date picker untuk deadline
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        UtilDateModel model = new UtilDateModel();
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        inputPanel.add(new JLabel("Deadline:"));
        inputPanel.add(datePicker);

        // Combo prioritas
        priorityComboBox = new JComboBox<>(new String[] {"High", "Medium", "Low"});
        inputPanel.add(new JLabel("Prioritas:"));
        inputPanel.add(priorityComboBox);

        JButton addButton = new JButton("Tambah");
        inputPanel.add(addButton);

        JButton editButton = new JButton("Edit");
        inputPanel.add(editButton);

        JButton deleteButton = new JButton("Hapus");
        inputPanel.add(deleteButton);

        JButton toggleButton = new JButton("Tandai Selesai/Belum");
        inputPanel.add(toggleButton);

        panel.add(inputPanel, BorderLayout.SOUTH);

        add(panel);

        // Action listener tombol tambah
        addButton.addActionListener(e -> {
            String desc = descriptionField.getText().trim();
            if (desc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Deskripsi harus diisi!");
                return;
            }
            if (datePicker.getModel().getValue() == null) {
                JOptionPane.showMessageDialog(this, "Deadline harus dipilih!");
                return;
            }
            java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
            LocalDate deadline = new java.sql.Date(selectedDate.getTime()).toLocalDate();

            int priority = priorityComboBox.getSelectedIndex() + 1;

            Task task = new Task(desc, deadline, priority);
            controller.addTask(task);
            clearInput();
            loadTasksFromDB();
        });

        // Tombol edit
        editButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih tugas untuk diedit!");
                return;
            }
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String currentDesc = (String) tableModel.getValueAt(selectedRow, 1);
            String currentDeadlineStr = (String) tableModel.getValueAt(selectedRow, 2);
            int currentPriority = (int) tableModel.getValueAt(selectedRow, 4);

            // Dialog edit
            JTextField descField = new JTextField(currentDesc, 20);

            UtilDateModel editModel = new UtilDateModel();
            editModel.setDate(
                Integer.parseInt(currentDeadlineStr.substring(0,4)),
                Integer.parseInt(currentDeadlineStr.substring(5,7)) -1,
                Integer.parseInt(currentDeadlineStr.substring(8,10))
            );
            editModel.setSelected(true);
            JDatePanelImpl editDatePanel = new JDatePanelImpl(editModel, new Properties());
            JDatePickerImpl editDatePicker = new JDatePickerImpl(editDatePanel, new DateLabelFormatter());

            JComboBox<String> priorityEditCombo = new JComboBox<>(new String[] {"High", "Medium", "Low"});
            priorityEditCombo.setSelectedIndex(currentPriority -1);

            JPanel editPanel = new JPanel(new GridLayout(3,2));
            editPanel.add(new JLabel("Deskripsi:"));
            editPanel.add(descField);
            editPanel.add(new JLabel("Deadline:"));
            editPanel.add(editDatePicker);
            editPanel.add(new JLabel("Prioritas:"));
            editPanel.add(priorityEditCombo);

            int result = JOptionPane.showConfirmDialog(this, editPanel, "Edit Tugas", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String newDesc = descField.getText().trim();
                if (newDesc.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Deskripsi harus diisi!");
                    return;
                }
                if (!editDatePicker.getModel().isSelected()) {
                    JOptionPane.showMessageDialog(this, "Deadline harus dipilih!");
                    return;
                }
                java.util.Date selectedDate = (java.util.Date) editDatePicker.getModel().getValue();
                LocalDate newDeadline = new java.sql.Date(selectedDate.getTime()).toLocalDate();
                int newPriority = priorityEditCombo.getSelectedIndex() + 1;

                Task task = new Task(id, newDesc, newDeadline, (Boolean) tableModel.getValueAt(selectedRow, 3), newPriority);
                controller.updateTask(task);
                loadTasksFromDB();
            }
        });

        // Tombol hapus
        deleteButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih tugas untuk dihapus!");
                return;
            }
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus tugas?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.deleteTask(id);
                loadTasksFromDB();
            }
        });

        // Tombol toggle status selesai
        toggleButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih tugas untuk ubah status!");
                return;
            }
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            boolean currentStatus = (boolean) tableModel.getValueAt(selectedRow, 3);
            controller.toggleTaskStatus(id, !currentStatus);
            loadTasksFromDB();
        });
    }

    private void loadTasksFromDB() {
        Integer filter = null;
        String selected = (String) filterComboBox.getSelectedItem();
        if ("Belum Selesai".equals(selected)) filter = 0;
        else if ("Sudah Selesai".equals(selected)) filter = 1;

        List<Task> tasks = controller.loadTasksByStatus(filter);

        tableModel.setRowCount(0);
        for (Task t : tasks) {
            tableModel.addRow(new Object[] {
                t.getId(),
                t.getDescription(),
                t.getDeadline().toString(),
                t.isDone(),
                t.getPriority()
            });
        }
    }

    private void clearInput() {
        descriptionField.setText("");
        datePicker.getModel().setValue(null);
        priorityComboBox.setSelectedIndex(1); // Medium default
    }
}
