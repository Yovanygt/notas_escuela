public class Grado {
    private int id;           // ID del grado
    private String nombre;    // Nombre del grado

    // Constructor que inicializa el ID y el nombre del grado
    public Grado(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Método para obtener el ID del grado
    public int getId() {
        return id;
    }

    // Método toString para mostrar el nombre del grado en el JComboBox
    @Override
    public String toString() {
        return nombre; // Esto es lo que se mostrará en el JComboBox
    }
}
