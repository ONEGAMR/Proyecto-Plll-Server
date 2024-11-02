package domain;

public class Meal {
	private String name;
	private String status;
	private int price;
	private int cantidad;
	private int totalOrder;
	private String imagePath; // Nuevo campo

	public Meal(String name, int cantidad, int totalOrder, String status, String imagePath) {
		this.name = name;
		this.cantidad = cantidad;
		this.totalOrder = totalOrder;
		this.status = status;
		this.imagePath = imagePath;
	}

	public Meal(String name, int cantidad, int totalOrder, String status) {
		this.name = name;
		this.cantidad = cantidad;
		this.totalOrder = totalOrder;
		this.status = status;
	}

	public Meal(String name, int price, String imagePath) {
		this.name = name;
		this.price = price;
		this.imagePath = imagePath;
	}
	public Meal() {
	}

	// Añadir getters y setters para imagePath
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	// Modificar el método toString para incluir imagePath
	@Override
	public String toString() {
		return "Meal [name=" + name + ", price=" + price + ", imagePath=" + imagePath + "]";
	}

	public String toStringMealData() {
		return name + "," + price + "," + imagePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public int getTotalOrder() {
		return totalOrder;
	}

	public void setTotalOrder(int totalOrder) {
		this.totalOrder = totalOrder;
	}

	public String toStringMealOrder() {
		return  name + "," + cantidad + "," + totalOrder + "," + status;
	}
}