package domain;

public class Orders extends Meal{

    private String idStudent;

    public Orders(String name, int cantidad, int totalOrder, String status, String idStudent) {
        super(name, cantidad, totalOrder, status);
        this.idStudent = idStudent;
    }
    public String getIdStudent() {
        return idStudent;
    }
    public void setIdStudent(String idStudent) {
        this.idStudent = idStudent;
    }
    @Override
    public String toString() {
        return super.toStringMealOrder() + "Orders{" + "idStudent=" + idStudent + '}';
    }
}
