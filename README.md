# Proyecto de Gestión Escolar

## Tabla de Contenidos
- [Descripción](#descripción)
- [Código Fuente](#código-fuente)
  - [Formulario Estudiantes](#formulario-estudiantes)
  - [Formulario Notas](#formulario-notas)
  - [Validación de Notas](#validación-de-notas)
- [Datos de Pruebas](#datos-de-pruebas)
- [Capturas de Pantalla](#capturas-de-pantalla)
- [Documentación](#documentación)
- [Contribuciones](#contribuciones)
- [Licencia](#licencia)
- [Enlace Externo](#enlace-externo)

## Descripción

Este proyecto permite gestionar estudiantes, notas y validar resultados académicos. Proporciona formularios para registrar estudiantes, ingresar notas y verificar los resultados de los estudiantes.

## Código Fuente

### Formulario Estudiantes
```bash
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FormularioEstudiantes extends JFrame {
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JComboBox<Grado> comboBoxGrados;

    public FormularioEstudiantes() {
        setTitle("Registro de Estudiantes");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

        txtCodigo = new JTextField();
        txtNombre = new JTextField();
        comboBoxGrados = new JComboBox<>();

        cargarGrados();

        add(new JLabel("Código:"));
        add(txtCodigo);
        add(new JLabel("Nombre:"));
        add(txtNombre);
        add(new JLabel("Grado:"));
        add(comboBoxGrados);
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardarEstudiante());
        add(btnGuardar);

        setVisible(true);
    }

    private void cargarGrados() {
        List<Grado> grados = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3307/escuela", "root", "1234");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT grado_id, nombre_grado FROM escuela.grados")) {
            
            while (rs.next()) {
                Grado grado = new Grado(rs.getInt("grado_id"), rs.getString("nombre_grado"));
                grados.add(grado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Grado grado : grados) {
            comboBoxGrados.addItem(grado);
        }
    }

    private void guardarEstudiante() {
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        Grado gradoSeleccionado = (Grado) comboBoxGrados.getSelectedItem();

        if (gradoSeleccionado != null && !codigo.isEmpty() && !nombre.isEmpty()) {
            if (codigoYaExiste(codigo)) {
                JOptionPane.showMessageDialog(this, "El código de estudiante ya existe. Por favor, use otro código.", "Código Duplicado", JOptionPane.WARNING_MESSAGE);
                return; // Salir del método si el código ya existe
            }

            int gradoId = gradoSeleccionado.getId();
            String sql = "INSERT INTO escuela.estudiante (codigo, nombre, grado_id) VALUES (?, ?, ?)";

            try (Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3307/escuela", "root", "1234");
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, codigo);
                pstmt.setString(2, nombre);
                pstmt.setInt(3, gradoId);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Estudiante guardado exitosamente.");
                    // Limpiar campos después de guardar
                    txtCodigo.setText("");
                    txtNombre.setText("");
                    comboBoxGrados.setSelectedIndex(0);
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo guardar el estudiante.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al guardar el estudiante: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor completa todos los campos.");
        }
    }

    private boolean codigoYaExiste(String codigo) {
        String sql = "SELECT COUNT(*) FROM escuela.estudiante WHERE codigo = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3307/escuela", "root", "1234");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, codigo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Si el conteo es mayor a 0, el código ya existe
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Si no hay coincidencias, el código no existe
    }

    public static void main(String[] args) {
        new FormularioEstudiantes();
    }
}

