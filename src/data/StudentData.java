package data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import domain.Student;

public class StudentData {

    public static ArrayList<Student> studentList = new ArrayList<>();
    private static final String fileName = "./src/files/students.json";
    private static JSONUtils<Student> jsonUtils = new JSONUtils<>(fileName);

    // Guarda un estudiante en el archivo JSON
    public static boolean saveStudent(Student s) {
        try {
            jsonUtils.saveElement(s);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Obtiene la lista de estudiantes desde el archivo JSON
    public static List<Student> getStudentList() {
        try {
            return jsonUtils.getElements(Student.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Actualiza el archivo JSON con la lista de estudiantes proporcionada
    public static void updateJson(List<Student> list) {
        jsonUtils.updateJson(list);
    }

    // Actualiza la información de un estudiante en la lista y en el archivo JSON
    public static boolean updateStudent(Student updatedStudent) {
        ArrayList<Student> list = (ArrayList<Student>) StudentData.getStudentList();
        for (Student s : list) {
            if (s.getCarnet().equals(updatedStudent.getCarnet())) {
                s.setNombre(updatedStudent.getNombre());
                s.setCorreoElectronico(updatedStudent.getCorreoElectronico());
                s.setTelefono(updatedStudent.getTelefono());
                s.setEstaActivo(updatedStudent.isEstaActivo());
                s.setDineroDisponible(updatedStudent.getDineroDisponible());
                StudentData.updateJson(list);
                return true;
            }
        }
        return false;
    }

    public static boolean updateMoney(String updatedStudent, Double money) {
        ArrayList<Student> list = (ArrayList<Student>) StudentData.getStudentList();
        for (Student s : list) {
            if (s.getCarnet().equals(updatedStudent)) {

                s.setDineroDisponible(money);
                StudentData.updateJson(list);
                return true;
            }
        }
        return false;
    }

    // Elimina el estudiante actualmente seleccionado de la lista y actualiza el archivo JSON
    public static void delete() {
        if (Logic.getCurrentStudent() != null) {
            ArrayList<Student> list = (ArrayList<Student>) StudentData.getStudentList();
            list.removeIf(student -> student.getCarnet().equals(Logic.getCurrentStudent().getCarnet()));
            StudentData.updateJson(list);
        }
    }
    public static Student searchStudent(String canet){
        Student s = new Student();

        for(Student stu : getStudentList()){
            if(stu.getCarnet().equals(canet)){
                s = stu;
                break;
            }
        }

        return s;
    }
}
