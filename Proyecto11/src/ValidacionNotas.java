import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class ValidacionNotas extends JFrame {
    private JComboBox<String> comboEstudiantes;
    private JTextArea txtResultados;
    private JButton btnValidar;

    public ValidacionNotas() {
        // Configuración del formulario
        setTitle("Validación de Notas");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // ComboBox para seleccionar estudiantes
        comboEstudiantes = new JComboBox<>();
        comboEstudiantes.setBounds(50, 30, 300, 30);
        cargarEstudiantes();
        add(comboEstudiantes);

        // Botón para validar
        btnValidar = new JButton("Validar Notas");
        btnValidar.setBounds(150, 80, 150, 30);
        btnValidar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                validarNotas();
            }
        });
        add(btnValidar);

        // Área de texto para mostrar resultados
        txtResultados = new JTextArea();
        txtResultados.setBounds(50, 130, 300, 120);
        txtResultados.setEditable(false);
        add(txtResultados);
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

    // Validar notas de un estudiante
    private void validarNotas() {
        String[] seleccion = comboEstudiantes.getSelectedItem().toString().split(" - ");
        String estudianteCodigo = seleccion[0];
        
        String sql = "SELECT bimestre, nota FROM escuela.notas WHERE estudiante_id = (SELECT id FROM escuela.estudiante WHERE codigo = ?)";
        
        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3307/escuela", "root", "1234");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, estudianteCodigo);
            ResultSet rs = pstmt.executeQuery();

            StringBuilder resultados = new StringBuilder();
            float sumaNotas = 0;
            int contador = 0;

            while (rs.next()) {
                int bimestre = rs.getInt("bimestre");
                float nota = rs.getFloat("nota");
                resultados.append("Bimestre ").append(bimestre).append(": ").append(nota).append("\n");
                sumaNotas += nota;
                contador++;
            }

            // Calcular promedio y resultado
            if (contador > 0) {
                float promedio = sumaNotas / contador;
                resultados.append("Promedio: ").append(promedio).append("\n");
                resultados.append(promedio >= 60 ? "Resultado: Aprobado" : "Resultado: Reprobado");
            } else {
                resultados.append("No se encontraron notas para este estudiante.");
            }

            txtResultados.setText(resultados.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al validar notas: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ValidacionNotas form = new ValidacionNotas();
            form.setVisible(true);
        });
    }
}
