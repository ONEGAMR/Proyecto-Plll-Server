package domain;
public class Orders extends Meal{

    private String name;
    private String status;
    private int cantidad;
    private Double totalOrder;
    private String idStudent;

    public Orders(String name, int cantidad, Double totalOrder, String status, String idStudent) {
        super();
        this.name = name;
        this.status = status;
        this.cantidad = cantidad;
        this.totalOrder = totalOrder;
        this.idStudent = idStudent;
    }

    public Orders(String name, int cantidad, Double totalOrder, String status) {
        super();
        this.name = name;
        this.status = status;
        this.cantidad = cantidad;
        this.totalOrder = totalOrder;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Double getTotalOrder() {
        return totalOrder;
    }

    public void setTotalOrder(Double totalOrder) {
        this.totalOrder = totalOrder;
    }

    public String getIdStudent() {
        return idStudent;
    }
    public void setIdStudent(String idStudent) {
        this.idStudent = idStudent;
    }

    @Override
    public String toString() {
        return
               name + "," + cantidad + "," + totalOrder;
    }
}
