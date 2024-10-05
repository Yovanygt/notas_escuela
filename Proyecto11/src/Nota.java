import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Nota {
    private String estudianteCodigo; // Código del estudiante
    private String materia;           // Materia de la nota
    private float nota;               // Valor de la nota
    private int bimestre;             // Bimestre de la nota

    // Constructor
    public Nota(String estudianteCodigo, String materia, float nota, int bimestre) {
        this.estudianteCodigo = estudianteCodigo;
        this.materia = materia;
        this.nota = nota;
        this.bimestre = bimestre;
    }

    // Getters y Setters
    public String getEstudianteCodigo() {
        return estudianteCodigo;
    }

    public void setEstudianteCodigo(String estudianteCodigo) {
        this.estudianteCodigo = estudianteCodigo;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public float getNota() {
        return nota;
    }

    public void setNota(float nota) {
        this.nota = nota;
    }

    public int getBimestre() {
        return bimestre;
    }

    public void setBimestre(int bimestre) {
        this.bimestre = bimestre;
    }

    // Método para guardar una nueva nota
    public void guardarNota() {
        String sql = "INSERT INTO escuela.notas (estudiante_codigo, materia, nota, bimestre) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3307/escuela", "root", "1234");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, this.estudianteCodigo);
            pstmt.setString(2, this.materia);
            pstmt.setFloat(3, this.nota);
            pstmt.setInt(4, this.bimestre);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Nota guardada exitosamente.");
            } else {
                System.out.println("No se pudo guardar la nota.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al guardar la nota: " + e.getMessage());
        }
    }

    // Método para obtener las notas de un estudiante
    public static List<Nota> obtenerNotasPorEstudiante(String estudianteCodigo) {
        List<Nota> notas = new ArrayList<>();
        String sql = "SELECT materia, nota, bimestre FROM escuela.notas WHERE estudiante_codigo = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3307/escuela", "root", "1234");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estudianteCodigo);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String materia = rs.getString("materia");
                float nota = rs.getFloat("nota");
                int bimestre = rs.getInt("bimestre");
                notas.add(new Nota(estudianteCodigo, materia, nota, bimestre));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener notas: " + e.getMessage());
        }
        return notas;
    }
}
