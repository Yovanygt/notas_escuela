import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ValidacionNotass {
    private String estudianteCodigo;

    // Constructor
    public ValidacionNotass(String estudianteCodigo) {
        this.estudianteCodigo = estudianteCodigo;
    }

    // Método para validar las notas del estudiante y calcular el promedio
    public void validarNotas() {
        List<Nota> notas = Nota.obtenerNotasPorEstudiante(estudianteCodigo);
        if (notas.isEmpty()) {
            System.out.println("No se encontraron notas para el estudiante con código: " + estudianteCodigo);
            return;
        }

        float sumaNotas = 0;
        int contador = 0;

        for (Nota nota : notas) {
            sumaNotas += nota.getNota();
            contador++;
            System.out.println("Materia: " + nota.getMateria() + ", Bimestre: " + nota.getBimestre() + ", Nota: " + nota.getNota());
        }

        // Calcular promedio
        float promedio = sumaNotas / contador;
        System.out.println("Promedio: " + promedio);
        System.out.println(promedio >= 60 ? "Resultado: Aprobado" : "Resultado: Reprobado");
    }

    public static void main(String[] args) {
        // Ejemplo de uso
        ValidacionNotass validacion = new ValidacionNotass("C001");
        validacion.validarNotas();
    }
}
