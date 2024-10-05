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

```java
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

```
### Formulario Notas

```java
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

```
### Formulario Notas
