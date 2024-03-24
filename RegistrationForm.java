import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

// Class representing the registration form
public class RegistrationForm extends JDialog {
    private JTextField tfName;
    private JTextField tfEmail;
    private JTextField tfPhone;
    private JTextField tfAddress;
    private JPasswordField pwdpassword;
    private JPasswordField pwdConfirmPwd;
    private JButton jbtRegister;
    private JButton jbtCancel;
    private JPanel panelRegister;
    private JLabel Icon;
    private User user; // Object representing the registered user

    // Constructor for the registration form
    public RegistrationForm(JFrame parent) {
        super(parent);
        setTitle("Create a new account");
        setContentPane(panelRegister);
        setMinimumSize(new Dimension(400, 430));// set the min width & height
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Register action listener for register button
        jbtRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser(); // Call method to handle registration
            }
        });

        // Register action listener for cancel button
        jbtCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the registration form
            }
        });

        setVisible(true); // Make the registration form visible
    }

    // Method to handle user registration
    private void registerUser() {
        // Retrieve user inputs from the text fields and password fields
        String name = tfName.getText();
        String email = tfEmail.getText();
        String phone = tfPhone.getText();
        String address = tfAddress.getText();
        String password = String.valueOf(pwdpassword.getPassword());
        String confirmPassword = String.valueOf(pwdConfirmPwd.getPassword());

        // Validate user inputs
        if (name.isEmpty() || email.isEmpty() || address.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields", "Try Again", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Confirm Password does not match", "Try Again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Add user to the database
        addUserToDatabase(name, email, phone, address, password);
    }

    // Method to add user to the database
    private User addUserToDatabase(String name, String email, String phone, String address, String password) {
        User user = null; // Initialize user object
        final String DB_URL = "jdbc:oracle:thin:@fsktmdbora.upm.edu.my:1521:FSKTM";
        final String USERNAME = "A214943";
        final String PASSWORD = "214943";

        try {
            // Establish connection to the database
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            //Connected to database successfully...

            Statement stat = conn.createStatement();
            String sql = "INSERT INTO users (name, email, phone, address, password)" + "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, password);
            // Insert row into the table
            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                // If registration successful, create user object
                user = new User();
                user.name = name;
                user.email = email;
                user.phone = phone;
                user.address = address;
                user.password = password;
            }
            stat.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user; // Return user object
    }

    // Main method to run the registration form
    public static void main(String[] args) {
        RegistrationForm myForm = new RegistrationForm(null);
        User user = myForm.user;
        if (user != null) {
            System.out.println("Successful registration of: " + user.name);
        } else {
            System.out.println("Registration canceled");
        }
    }
}
