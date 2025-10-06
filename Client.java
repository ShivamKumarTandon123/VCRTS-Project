import java.awt.*;
import javax.swing.*;
public class Client extends JFrame {
  private String clientName;
  public Client(String clientName) {
      this.clientName = clientName;
      setTitle("Client Interface");
      setSize(700, 500);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setLocationRelativeTo(null);
      
      // Main panel
      JPanel mainPanel = new JPanel();
      mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
      mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));// padding top,left,bottom, right
      mainPanel.setBackground(new Color(187, 213, 237));
      add(mainPanel);

      // Welcome message
      JLabel label = new JLabel("Welcome, " + clientName + "!", SwingConstants.CENTER);
      label.setAlignmentX(Component.LEFT_ALIGNMENT);

      // Back button panel
      JPanel boxPanel = new JPanel();
      boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.X_AXIS));
      boxPanel.setOpaque(false);
      boxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

      // Back button
      JButton backButton = new JButton("Log out");
      backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
      backButton.addActionListener(e -> {
          dispose(); 
          new LandingPage(); 
      });
      boxPanel.add(backButton);

      // Add fields to mainPanel
      mainPanel.add(boxPanel);
      mainPanel.add(Box.createVerticalStrut(20)); 
      mainPanel.add(label);

      setVisible(true);
    }
  
  public void submitJob() {
      System.out.println("Client job submitted!");
  }
}
