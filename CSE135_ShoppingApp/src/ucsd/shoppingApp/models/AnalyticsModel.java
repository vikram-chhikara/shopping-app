package ucsd.shoppingApp.models;

public class AnalyticsModel {
	private String rowName;
	private String product;
	private double price;

	public AnalyticsModel(){
	}
	
	public AnalyticsModel(String rName, String prod, double pri) {
		this.rowName = rName;
		this.product = prod;
		this.price = pri;
	}

	public String getRowName() {
		return rowName;
	}

	public void setRowName(String rowName) {
		this.rowName = rowName;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}
	
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
