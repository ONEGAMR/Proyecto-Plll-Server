package domain;

public class Meal {
	private String name;
	private int price;
	private String imagePath; // Nuevo campo

	public Meal(String name, int price, String imagePath) {
		this.name = name;
		this.price = price;
		this.imagePath = imagePath;
	}
	public Meal() { }


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

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
}