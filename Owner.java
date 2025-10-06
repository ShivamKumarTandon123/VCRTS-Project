import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Owner extends JFrame {

    private final String ownerName;

    private JTextField ownerIdField;
    private JTextField vehicleInfoField;
    private JSpinner residencyAmountSpinner;
    private JComboBox<String> residencyUnitBox;

    private DefaultTableModel tableModel;
    private JTable table;

    // where entries will be stored
    private static final String OUTPUT_FILE = "owner_entries.csv";
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Owner(String ownerName) {
        this.ownerName = ownerName;

        setTitle("Owner Interface");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 560);
        setLocationRelativeTo(null);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(187, 213, 237));

        JLabel header = new JLabel("Owner console  Welcome " + ownerName, SwingConstants.CENTER);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        root.add(header);
        root.add(Box.createVerticalStrut(10));

        // form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        ownerIdField = new JTextField();
        vehicleInfoField = new JTextField();

        residencyAmountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        residencyUnitBox = new JComboBox<>(new String[] {"hours", "days"});

        int r = 0;

        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Owner ID"), gc);
        gc.gridx = 1; gc.gridy = r++; form.add(ownerIdField, gc);

        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Vehicle info"), gc);
        gc.gridx = 1; gc.gridy = r++; form.add(vehicleInfoField, gc);

        JPanel residencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        residencyPanel.setOpaque(false);
        residencyPanel.add(residencyAmountSpinner);
        residencyPanel.add(residencyUnitBox);

        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Approx residency time"), gc);
        gc.gridx = 1; gc.gridy = r++; form.add(residencyPanel, gc);

        JButton addButton = new JButton("Add entry to list");
        JButton saveButton = new JButton("Save list to file");
        JButton clearButton = new JButton("Clear form");
        JButton backButton = new JButton("Back");

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(addButton);
        buttons.add(saveButton);
        buttons.add(clearButton);
        buttons.add(backButton);

        gc.gridx = 0; gc.gridy = r; gc.gridwidth = 2;
        form.add(buttons, gc);

        root.add(form);
        root.add(Box.createVerticalStrut(12));

        // table for multiple entries
        tableModel = new DefaultTableModel(new Object[] {
                "Timestamp", "Owner ID", "Vehicle info", "Residency amount", "Unit"
        }, 0);
        table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(760, 260));
        root.add(tableScroll);

        // behavior
        addButton.addActionListener(this::onAdd);
        saveButton.addActionListener(this::onSave);
        clearButton.addActionListener(e -> clearForm());
        backButton.addActionListener(e -> {
            dispose();
            try {
                // If your project has a LandingPage with no args construco
                new LandingPage().setVisible(true);
            } catch (Throwable t) {
                // If LandingPage is not available just close
            }
        });

        setContentPane(root);
        setVisible(true);
    }

    private void onAdd(ActionEvent e) {
        String ownerId = ownerIdField.getText().trim();
        String vehicle = vehicleInfoField.getText().trim();
        int amount = (Integer) residencyAmountSpinner.getValue();
        String unit = String.valueOf(residencyUnitBox.getSelectedItem());

        if (ownerId.isEmpty() || vehicle.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Owner ID and Vehicle info");
            return;
        }

        String ts = TS_FMT.format(LocalDateTime.now());

        tableModel.addRow(new Object[] { ts, ownerId, vehicle, amount, unit });
        clearForm();
    }

    private void onSave(ActionEvent e) {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Nothing to save");
            return;
        }

        File out = new File(OUTPUT_FILE);
        boolean writeHeader = !out.exists();

        try (FileWriter fw = new FileWriter(out, true)) {
            if (writeHeader) {
                fw.write("timestamp,owner_id,vehicle_info,residency_amount,residency_unit\n");
            }
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String ts = String.valueOf(tableModel.getValueAt(i, 0));
                String ownerId = String.valueOf(tableModel.getValueAt(i, 1));
                String vehicle = escapeCsv(String.valueOf(tableModel.getValueAt(i, 2)));
                String amount = String.valueOf(tableModel.getValueAt(i, 3));
                String unit = String.valueOf(tableModel.getValueAt(i, 4));
                fw.write(ts + "," + ownerId + "," + vehicle + "," + amount + "," + unit + "\n");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save file  " + ex.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this, "Saved to " + OUTPUT_FILE);
    }

    private void clearForm() {
        ownerIdField.setText("");
        vehicleInfoField.setText("");
        residencyAmountSpinner.setValue(1);
        residencyUnitBox.setSelectedIndex(0);
    }

    private static String escapeCsv(String s) {
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }
    
// optional entry point for quick run without landing page

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Owner("owner"));
    }
}