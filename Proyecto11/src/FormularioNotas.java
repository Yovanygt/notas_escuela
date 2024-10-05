import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class FormularioNotas extends JFrame {
    private JComboBox<String> comboEstudiantes;
    private JTextField txtMateria;
    private JTextField txtNota;
    private JComboBox<String> comboBimestre;  // ComboBox para seleccionar bimestre
    private JButton btnGuardar;

    public FormularioNotas() {
        // Configuración del formulario
        setTitle("Formulario de Notas");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // ComboBox para seleccionar estudiantes
        comboEstudiantes = new JComboBox<>();
        comboEstudiantes.setBounds(50, 30, 300, 30);
        cargarEstudiantes();
        add(comboEstudiantes);

        // Campo para ingresar materia
        JLabel lblMateria = new JLabel("Materia:");
        lblMateria.setBounds(50, 80, 100, 30);
        add(lblMateria);
        
        txtMateria = new JTextField();
        txtMateria.setBounds(150, 80, 200, 30);
        add(txtMateria);

        // Campo para ingresar nota
        JLabel lblNota = new JLabel("Nota:");
        lblNota.setBounds(50, 130, 100, 30);
        add(lblNota);
        
        txtNota = new JTextField();
        txtNota.setBounds(150, 130, 200, 30);
        add(txtNota);

        // Campo para seleccionar bimestre
        JLabel lblBimestre = new JLabel("Bimestre:");
        lblBimestre.setBounds(50, 180, 100, 30);
        add(lblBimestre);
        
        comboBimestre = new JComboBox<>(new String[] {"1", "2", "3", "4"}); // Ejemplo de bimestres
        comboBimestre.setBounds(150, 180, 200, 30);
        add(comboBimestre);

        // Botón para guardar
        btnGuardar = new JButton("Guardar Nota");
        btnGuardar.setBounds(150, 230, 150, 30);
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarNota();
            }
        });
        add(btnGuardar);
    }

    // Cargar estudiantes en el ComboBox
    private void cargarEstudiantes() {
        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3307/escuela", "root", "1234");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT codigo, nombre FROM escuela.estudiante")) {
             
            while (rs.next()) {
                comboEstudiantes.addItem(rs.getString("codigo") + " - " + rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Guardar nota en la base de datos
    private void guardarNota() {
        String[] seleccion = comboEstudiantes.getSelectedItem().toString().split(" - ");
        String estudianteCodigo = seleccion[0]; // Aquí tomas el código
        String materia = txtMateria.getText();
        float nota = Float.parseFloat(txtNota.getText());
        String bimestre = comboBimestre.getSelectedItem().toString(); // Obtener bimestre

        // Primero, obtenemos el ID del estudiante basado en el código
        int estudianteId = 0;
        String sqlGetId = "SELECT id FROM escuela.estudiante WHERE codigo = ?";
        
        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3307/escuela", "root", "1234");
             PreparedStatement pstmtGetId = conn.prepareStatement(sqlGetId)) {
            
            pstmtGetId.setString(1, estudianteCodigo);
            ResultSet rs = pstmtGetId.executeQuery();
            if (rs.next()) {
                estudianteId = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql = "INSERT INTO escuela.notas (estudiante_id, materia, nota, bimestre) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3307/escuela", "root", "1234");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, estudianteId); // Aquí utilizamos el ID que obtuvimos
            pstmt.setString(2, materia);
            pstmt.setFloat(3, nota);
            pstmt.setString(4, bimestre);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Nota guardada con éxito.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar la nota: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FormularioNotas form = new FormularioNotas();
            form.setVisible(true);
        });
    }
}
