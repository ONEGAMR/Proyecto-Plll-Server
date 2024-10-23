package domain;

public class Meal {

	private String name;
	private String status;
	private int price;
	private int cantidad;
	private int totalOrder;

	public Meal(String name, int price) {
		this.name = name;
		this.price = price;
	}
	public Meal() {
	}

	public Meal(String name, int cantidad, int totalOrder, String status) {
		this.name = name;
		this.cantidad = cantidad;
		this.totalOrder = totalOrder;
		this.status = status;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Meal [name=" + name + ", price=" + price + "]";
	}

	public String toStringMealData() {
		return  name + "," + price;
	}

	public String toStringMealOrder() {
		return  name + "," + cantidad + "," + totalOrder + "," + status;
	}




}
