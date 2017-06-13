package ucsd.shoppingApp.models;

public class SaleModel {
	private int prodID;
	private int stateID;
	private int categoryID;
	private double price;
	private java.sql.Timestamp time;
	private double stateSum;
	private double prodSum;

	public SaleModel(){
	}
	
	public SaleModel(int prod, int state, int cat, double pri, java.sql.Timestamp t) {
		this.prodID = prod;
		this.stateID = state;
		this.categoryID = cat;
		this.price = pri;
		this.time = t;
	}
	
	public SaleModel(int prod, int state, int cat, double pri, double ss, double ps) {
		this.prodID = prod;
		this.stateID = state;
		this.categoryID = cat;
		this.price = pri;
		this.time = null;
		this.stateSum = ss;
		this.prodSum = ps;
	}

	public int getProdID() {
		return prodID;
	}

	public void setProdID(int prodID) {
		this.prodID = prodID;
	}

	public int getStateID() {
		return stateID;
	}

	public void setStateID(int stateID) {
		this.stateID = stateID;
	}

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public java.sql.Timestamp getTime() {
		return time;
	}

	public void setTime(java.sql.Timestamp time) {
		this.time = time;
	}
	
	public double getStateSum() {
		return stateSum;
	}

	public void setStateSum(double stateSum) {
		this.stateSum = stateSum;
	}

	public double getProdSum() {
		return prodSum;
	}

	public void setProdSum(double prodSum) {
		this.prodSum = prodSum;
	}

	@Override
	public boolean equals(Object o)
	{
		//If the compared object is null, the two are not the same
		if(o == null)
			return false;
		
		//Checks if object is correct
		if(o instanceof SaleModel) {
			//Check if values are correct
			if(this.getProdID() == ((SaleModel)o).getProdID() && this.getStateID() == ((SaleModel)o).getStateID())
				return true;
		}
		
		return false;
	}
}
