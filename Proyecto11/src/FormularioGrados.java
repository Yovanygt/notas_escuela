import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class FormularioGrados extends JFrame {
    private JTextField txtNombreGrado;

    public FormularioGrados() {
        setTitle("Registro de Grados");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        txtNombreGrado = new JTextField(15);
        JButton btnGuardar = new JButton("Guardar");

        btnGuardar.addActionListener(e -> {
            String nombreGrado = txtNombreGrado.getText();
            guardarGrado(nombreGrado);
        });

        add(new JLabel("Nombre del Grado:"));
        add(txtNombreGrado);
        add(btnGuardar);

        setVisible(true);
    }

    private void guardarGrado(String nombreGrado) {
        String sql = "INSERT INTO escuela.grados (nombre_grado) VALUES (?)";

        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3307/escuela", "root", "1234");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombreGrado);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Grado guardado exitosamente.");
            } else {
                System.out.println("No se pudo guardar el grado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new FormularioGrados();
    }
}
