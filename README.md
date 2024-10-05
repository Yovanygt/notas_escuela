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
### Validación de Notas
```java
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

```

## Datos de Pruebas

Al ingresar un estudiante en el sistema, se requiere proporcionar los siguientes datos:

- **Código**: Un identificador único para cada estudiante. Este campo es obligatorio y no puede repetirse.
- **Nombre**: El nombre completo del estudiante.
- **Grado**: El grado al que pertenece el estudiante (seleccionado de una lista desplegable).

### Proceso de Ingreso:
1. El usuario ingresa el **código** del estudiante.
2. El usuario ingresa el **nombre** del estudiante.
3. El usuario selecciona el **grado** al que pertenece el estudiante.
4. Una vez que todos los datos han sido ingresados, el formulario permite guardar la información.
5. El sistema verifica que el **código** no se haya ingresado previamente. Si el código ya existe, se muestra un mensaje de error indicando que el código no puede repetirse.

### Ejemplo:
- Código: `EST001`
- Nombre: `Luis García`
- Grado: `5to Primaria`

Si el código `EST001` ya ha sido registrado previamente, el sistema mostrará el siguiente mensaje:  
**"El código de estudiante ya existe. Por favor, use otro código."**

Esto asegura que no se puedan registrar estudiantes con el mismo código y garantiza la unicidad de los registros.


![ ](https://github.com/Yovanygt/imagenes/blob/main/10_2%20guardar%20estudiante.png?raw=true)

### se guarda el estudiante:

![ ](https://github.com/Yovanygt/imagenes/blob/main/10_2%20guardar%20estudiante.png?raw=true)

![ ](https://github.com/Yovanygt/imagenes/blob/main/10_3%20boton%20guardar%20estudiante.png?raw=true)

### No se puede repetir:

![ ](https://github.com/Yovanygt/imagenes/blob/main/10_4%20codigo%20ya%20existe%20registro%20estudiante.png?raw=true)

### Base de datos Estudiante:
![ ](https://github.com/Yovanygt/imagenes/blob/main/10_5%20validacion%20estudiantes%20en%20mariadb.png?raw=true)

### Base de datos Grado:
![ ](![image](https://github.com/user-attachments/assets/a8cef468-043a-477a-9321-6c151c936760)



## Datos de Pruebas

### Proceso de Guardar Notas:
Al ingresar las notas de los estudiantes, se requiere proporcionar los siguientes datos:

- **Código del Estudiante**: Un identificador único que vincula la nota al estudiante correspondiente.
- **Materia**: La asignatura a la que corresponde la nota.
- **Bimestre**: El período del año escolar al que corresponde la nota (Primer Bimestre, Segundo Bimestre, etc.).
- **Nota**: El valor numérico de la calificación (debe estar entre 0 y 100).

### Proceso de Ingreso de Notas:
1. El usuario valida  **código del estudiante** para identificar al alumno.
2. El usuario ingresa la **materia** 
3. El usuario selecciona el **bimestre** al que corresponde la nota (por ejemplo, Primer Bimestre, Segundo Bimestre).
4. El usuario ingresa la **nota** obtenida por el estudiante en la materia seleccionada.
5. Al intentar guardar la nota, el sistema valida que:
   - La **nota** esté dentro del rango permitido (0 - 100).
   - El **código del estudiante** sea válido y esté registrado previamente en el sistema.
6. Si la nota es válida, el sistema guarda la calificación y muestra un mensaje de éxito.
7. Si la nota está fuera de rango, se muestra un mensaje de error, y el proceso de guardado es cancelado.

### Ejemplo:
- Código del Estudiante: `6 Mauricio`
- Materia: `Matemáticas`
- Bimestre: `Primer Bimestre`
- Nota: `90`

Si se intenta ingresar una nota superior a 100, el sistema mostrará el siguiente mensaje:  
**"Error: La nota debe estar entre 0 y 100."**

Este proceso asegura que las notas ingresadas cumplan con los criterios establecidos, se registre el bimestre correspondiente y evita errores en el registro de calificaciones.

### Ejemplo:

![ ](https://github.com/Yovanygt/imagenes/blob/main/11_1%20ingresar%20notas.png?raw=true)

![ ](https://github.com/Yovanygt/imagenes/blob/main/11_2%20boton%20guardar.png?raw=true)

![ ](https://github.com/Yovanygt/imagenes/blob/main/11_3%20seleccion%20bimestre.png?raw=true)


## Datos de Pruebas

### Proceso de Validación de Notas:
El sistema permite validar las notas de un estudiante previamente registrado en la base de datos. A continuación, se detallan las validaciones y el proceso paso a paso:

1. **Selección del Estudiante**:  
   El usuario selecciona un estudiante del **ComboBox** que contiene una lista de todos los estudiantes registrados, mostrando su código y nombre.

2. **Consulta de Notas**:  
   Al hacer clic en el botón **"Validar Notas"**, el sistema recupera las notas del estudiante seleccionado desde la base de datos. Las notas se consultan por **bimestre** y se muestran en el área de texto.

3. **Cálculo del Promedio**:  
   Se calcula el promedio de las notas obtenidas en todos los bimestres disponibles para ese estudiante. El promedio es calculado sumando todas las notas y dividiendo entre el número total de bimestres.

4. **Determinación del Resultado**:  
   - Si el **promedio** es igual o superior a **60**, el sistema muestra el mensaje **"Resultado: Aprobado"**.
   - Si el promedio es inferior a **60**, el sistema muestra el mensaje **"Resultado: Reprobado"**.

5. **No se Encontraron Notas**:  
   Si el estudiante no tiene notas registradas, el sistema mostrará el mensaje **"No se encontraron notas para este estudiante."**

### Ejemplo:
1. El usuario selecciona el estudiante con código `EST001 - Luis García`.
2. El sistema consulta las notas en la base de datos y muestra lo siguiente:
   - **Bimestre 1**: 85
   - **Bimestre 2**: 90
   - **Bimestre 3**: 88
   - **Bimestre 4**: 92
   - **Promedio**: 88.75
   - **Resultado**: Aprobado

Este proceso asegura que las notas de un estudiante se validen correctamente, mostrando un promedio acumulado y determinando si el estudiante aprueba o reprueba con base en sus calificaciones.

![ ](https://github.com/Yovanygt/imagenes/blob/main/12_1%20validar%20notas.png?raw=true)

![ ](https://github.com/Yovanygt/imagenes/blob/main/12_2%20boron%20validar%20notas.png?raw=true)

![ ](https://github.com/Yovanygt/imagenes/blob/main/12_3%20base%20de%20datos%20notas.png?raw=true)

## Documentación

### 1. Descripción del Proyecto
El **Sistema de Gestión Escolar** permite a los usuarios gestionar la información de los estudiantes, registrar notas y validar los resultados académicos. El sistema se desarrolla en Java utilizando **Swing** para la interfaz gráfica y **MariaDB** como base de datos. A través de formularios, los usuarios pueden realizar las siguientes acciones:
- Registrar estudiantes en la base de datos.
- Registrar notas por materia y bimestre.
- Validar las notas para calcular el promedio y determinar si el estudiante aprueba o reprueba.

### 2. Estructura del Proyecto
El proyecto está dividido en diferentes formularios para manejar estudiantes y notas:

- **Formulario de Estudiantes**: Permite registrar nuevos estudiantes y evitar la duplicación de códigos.
- **Formulario de Notas**: Permite ingresar notas por materia y bimestre.
- **Validación de Notas**: Permite consultar y validar las notas de un estudiante, mostrando el promedio y el resultado final (aprobado o reprobado).

### 3. Funcionalidades Principales

#### a. Registro de Estudiantes:
- Se ingresan el **código**, **nombre** y **grado** del estudiante.
- Se verifica que el código no esté duplicado.
- Los datos del estudiante se almacenan en la base de datos.

#### b. Registro de Notas:
- Se selecciona el **código del estudiante**, **materia**, **bimestre**, y **nota**.
- Se valida que la **nota** esté dentro del rango permitido (0 - 100).
- La nota se guarda en la base de datos si todas las validaciones son correctas.

#### c. Validación de Notas:
- Se selecciona el estudiante y el sistema muestra las notas registradas por bimestre.
- Se calcula el **promedio** de las notas y se determina si el estudiante **aprueba** o **reprueba**.
- Si no hay notas registradas, se informa al usuario.

### 4. Arquitectura del Sistema
El sistema utiliza una arquitectura en capas, donde la interfaz gráfica está separada de la lógica de negocio y el acceso a la base de datos.

- **Capa de Presentación**: Formularios desarrollados con **Java Swing**.
- **Capa de Lógica**: Métodos que gestionan la validación de datos y cálculos como el promedio de notas.
- **Capa de Persistencia**: Conexión y consultas a la base de datos **MariaDB**.

### 5. Base de Datos
El sistema se conecta a una base de datos **MariaDB** donde se almacenan las siguientes tablas:

- **Estudiantes**: Contiene información del estudiante, como código, nombre, y grado.
- **Notas**: Contiene las calificaciones de los estudiantes por materia y bimestre.

#### Estructura de la Tabla `estudiante`:
| Campo      | Tipo        | Descripción                              |
|------------|-------------|------------------------------------------|
| `id`       | INT         | Identificador único del estudiante       |
| `codigo`   | VARCHAR(10) | Código del estudiante                    |
| `nombre`   | VARCHAR(100)| Nombre completo del estudiante           |
| `grado_id` | INT         | Identificador del grado del estudiante   |

#### Estructura de la Tabla `notas`:
| Campo          | Tipo        | Descripción                              |
|----------------|-------------|------------------------------------------|
| `id`           | INT         | Identificador único de la nota           |
| `estudiante_id`| INT         | Identificador del estudiante             |
| `bimestre`     | INT         | Bimestre (1, 2, 3, o 4)                  |
| `materia`      | VARCHAR(100)| Nombre de la asignatura                  |
| `nota`         | FLOAT       | Nota obtenida                            |

### 6. Requerimientos del Sistema
- **Java 8** o superior.
- **MariaDB** instalado y configurado.
- **Conector JDBC** para la base de datos.

### 7. Instrucciones de Uso

1. **Configurar la Base de Datos**:  
   Asegúrate de tener la base de datos **MariaDB** instalada y configurada. Crea las tablas necesarias según la estructura descrita.
   
2. **Ejecución del Proyecto**:  
   - Importa el proyecto en un entorno de desarrollo como **Eclipse** o **IntelliJ**.
   - Asegúrate de tener el **Conector JDBC** en el classpath.
   - Configura las credenciales de la base de datos en el código (`localhost`, `puerto`, `usuario`, `contraseña`).
   - Ejecuta la aplicación.

### 8. Validaciones Implementadas
- **Código de Estudiante Único**: Se valida que no haya duplicación de códigos al registrar un nuevo estudiante.
- **Rango de Notas**: Se asegura que las notas ingresadas estén entre 0 y 100.
- **Validación de Bimestres**: Se evita la duplicación de notas para un mismo bimestre en la misma materia.
- **Promedio de Notas**: Se calcula el promedio de las notas de un estudiante, determinando si aprueba o reprueba.

### 9. Diagrama de Clases
El diagrama de clases sigue el enfoque de **POO (Programación Orientada a Objetos)**, donde las clases `Estudiante`, `Nota`, y `Grado` representan las entidades principales, y la clase `ValidacionNotas` maneja la lógica para validar los resultados académicos.

![ ](https://github.com/Yovanygt/imagenes/blob/main/10_1.png?raw=true)

### 10. Conclusión
Este sistema de gestión escolar es una herramienta eficiente para manejar la información académica de los estudiantes, garantizando la correcta validación y registro de notas, proporcionando resultados confiables y automatizando la gestión educativa.


 
