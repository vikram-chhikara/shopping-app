package ucsd.shoppingApp.models;

public class AnalyticsModel {
	private String rowName;
	private String product;
	private double price;
	private int ID;

	public AnalyticsModel(){
	}
	
	public AnalyticsModel(String rName, String prod, double pri, int id) {
		this.rowName = rName;
		this.product = prod;
		this.price = pri;
		this.ID = id;
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
	
	public int getID() {
		return ID;
	}

	public void setID(int id) {
		this.ID = id;
	}

	@Override
	public boolean equals(Object o)
	{
		//If the compared object is null, the two are not the same
		if(o == null)
			return false;
		
		//Checks if object is correct
		if(o instanceof AnalyticsModel) {
			//Check if values are correct
			if(this.getID() == ((AnalyticsModel)o).getID())
				return true;
		}
		
		return false;
	}
}

