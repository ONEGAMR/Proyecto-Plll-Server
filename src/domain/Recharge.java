package domain;

import java.time.LocalDate;

public class Recharge {

	private String carnet;
	private LocalDate date;
	private double monto;

	public Recharge() {}

	public Recharge(String carnet, double monto, LocalDate date) {
		this.carnet = carnet;
		this.monto = monto;
		this.date = date;
	}

	public LocalDate getDate() {
		return date;
	}

	public String getCarnet() {
		return carnet;
	}

	public void setCarnet(String carnet) {
		this.carnet = carnet;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
	public void setMonto(double monto) {
		this.monto = monto;
	}

	public double getMonto() {
		return monto;
	}

	public String toStringRecharge() {

		return monto + "," + date;
	}
}
