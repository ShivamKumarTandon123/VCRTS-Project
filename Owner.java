/*
 Section  Imports
 These imports bring in the GUI classes from Swing and AWT,
 the table model, time utilities for timestamps, and file utilities
 for saving entries to a CSV file.
*/
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
 Section  Class header
 This class represents the Owner window. It is a normal Swing JFrame.
*/
public class Owner extends JFrame {

    /*
     Section  Fields that hold user inputs and data
     ownerName is just used for the greeting
     Text fields and controls collect the values we need
     tableModel holds many entries so the program can accept multiple owners
     */
    private final String ownerName;

    private JTextField ownerIdField;
    private JTextField vehicleInfoField;
    private JSpinner residencyAmountSpinner;
    private JComboBox<String> residencyUnitBox;

    private DefaultTableModel tableModel;
    private JTable table;

    /*
     Section  Constants for saving
     OUTPUT_FILE is the CSV file to store entries
     TS_FMT makes a readable timestamp for each row
     */
    private static final String OUTPUT_FILE = "owner_entries.csv";
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /*
     Section  Constructor
     Builds the entire window. It creates the layout, inputs, table and buttons.
    */
    public Owner(String ownerName) {
        this.ownerName = ownerName;

        // Window setup
        setTitle("Owner Interface");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 560);
        setLocationRelativeTo(null);

        /*
         Section  Root panel
         A vertical BoxLayout stacks header, form, buttons and the table.
         */
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(187, 213, 237));

        /*
         Section  Header
         Friendly title that shows who is logged in.
         */
        JLabel header = new JLabel("Owner console  Welcome " + ownerName, SwingConstants.CENTER);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        root.add(header);
        root.add(Box.createVerticalStrut(10));

        /*
         Section  Form panel
         GridBagLayout gives a clean two column form.
         Left column has labels. Right column has inputs.
         */
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Create inputs
        ownerIdField = new JTextField();
        vehicleInfoField = new JTextField();
        residencyAmountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        residencyUnitBox = new JComboBox<>(new String[] {"hours", "days"});

        // Row counter for the grid
        int r = 0;

        // Owner ID row
        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Owner ID"), gc);
        gc.gridx = 1; gc.gridy = r++; form.add(ownerIdField, gc);

        // Vehicle info row
        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Vehicle info"), gc);
        gc.gridx = 1; gc.gridy = r++; form.add(vehicleInfoField, gc);

        /*
         Section  Residency time input
         Combines a numeric amount with a unit picker.
         */
        JPanel residencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        residencyPanel.setOpaque(false);
        residencyPanel.add(residencyAmountSpinner);
        residencyPanel.add(residencyUnitBox);

        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Approx residency time"), gc);
        gc.gridx = 1; gc.gridy = r++; form.add(residencyPanel, gc);

        /*
         Section  Buttons under the form
         Add entry to list puts the current inputs into the table
         Save list to file writes all rows to a CSV with timestamps
         Clear form empties inputs for the next entry
         Back closes this window and returns to LandingPage if present
         */
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

        // Place the form on the root panel
        root.add(form);
        root.add(Box.createVerticalStrut(12));

        /*
         Section  Table for multiple entries
         DefaultTableModel holds rows of owner data.
         A JScrollPane lets it scroll when many entries are added.
         */
        tableModel = new DefaultTableModel(new Object[] {
                "Timestamp", "Owner ID", "Vehicle info", "Residency amount", "Unit"
        }, 0);
        table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(760, 260));
        root.add(tableScroll);

        /*
         Section  Hook up button actions
         Each button calls a small method that does one job.
         */
        addButton.addActionListener(this::onAdd);
        saveButton.addActionListener(this::onSave);
        clearButton.addActionListener(e -> clearForm());
        backButton.addActionListener(e -> {
            dispose();
            try {
               
                new LandingPage().setVisible(true);
            } catch (Throwable t) {
      
            }
        });

        /*
         Section  Show the window
         */
        setContentPane(root);
        setVisible(true);
    }

    /*
     Section  Add action
     Validates inputs, creates a timestamp and appends a new row to the table.
    */
    private void onAdd(ActionEvent e) {
        String ownerId = ownerIdField.getText().trim();
        String vehicle = vehicleInfoField.getText().trim();
        int amount = (Integer) residencyAmountSpinner.getValue();
        String unit = String.valueOf(residencyUnitBox.getSelectedItem());

        // basic validation
        if (ownerId.isEmpty() || vehicle.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Owner ID and Vehicle info");
            return;
        }

        // make a friendly timestamp
        String ts = TS_FMT.format(LocalDateTime.now());

        // add a row to the table
        tableModel.addRow(new Object[] { ts, ownerId, vehicle, amount, unit });

        // reset the form for the next entry
        clearForm();
    }

    /*
     Section  Save action
     Writes all table rows to a CSV file.
     Adds a header the first time the file is created.
    */
    private void onSave(ActionEvent e) {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Nothing to save");
            return;
        }

        File out = new File(OUTPUT_FILE);
        boolean writeHeader = !out.exists();

        try (FileWriter fw = new FileWriter(out, true)) {
            // header only once
            if (writeHeader) {
                fw.write("timestamp,owner_id,vehicle_info,residency_amount,residency_unit\n");
            }
            // write each row
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

    /*
     Section  Clear form helper
     Restores inputs to simple defaults.
    */
    private void clearForm() {
        ownerIdField.setText("");
        vehicleInfoField.setText("");
        residencyAmountSpinner.setValue(1);
        residencyUnitBox.setSelectedIndex(0);
    }

    /*
     Section  CSV helper
     Escapes commas, quotes and line breaks in the vehicle info field.
    */
    private static String escapeCsv(String s) {
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }

    /*
     Section  Quick test runner
    */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Owner("owner"));
    }
}
