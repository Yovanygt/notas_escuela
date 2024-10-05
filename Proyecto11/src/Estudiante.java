public class Estudiante {
    private int codigoEstudiante;
    private String nombre;
    private String grado;

    // Constructor
    public Estudiante(int codigoEstudiante, String nombre, String grado) {
        this.codigoEstudiante = codigoEstudiante;
        this.nombre = nombre;
        this.grado = grado;
    }

    // Getters y Setters
    public int getCodigoEstudiante() {
        return codigoEstudiante;
    }

    public void setCodigoEstudiante(int codigoEstudiante) {
        this.codigoEstudiante = codigoEstudiante;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    @Override
    public String toString() {
        return "Estudiante{" +
                "codigoEstudiante=" + codigoEstudiante +
                ", nombre='" + nombre + '\'' +
                ", grado='" + grado + '\'' +
                '}';
    }

    public int getId() {
        return codigoEstudiante; // Devuelve el ID correcto del estudiante
    }
}
