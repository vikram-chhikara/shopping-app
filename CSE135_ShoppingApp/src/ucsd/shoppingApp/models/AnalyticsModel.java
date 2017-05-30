package ucsd.shoppingApp.models;

import java.util.ArrayList;

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
	
	public static ArrayList<AnalyticsModel> moveNull(ArrayList<AnalyticsModel> am) {
		AnalyticsModel aMove;
		
		int r_size = am.size();
		for(int amove = 0, a = 0; amove < r_size && a < r_size; amove++, a++) {
			aMove = am.get(amove);
			if(aMove.getPrice() != 0.0) {
				break;
			}
			am.remove(amove);
			am.add(aMove);
			amove--;
		}
		
		return am;
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
			if(this.getRowName().equals(((AnalyticsModel)o).getRowName()) && this.getProduct().equals(((AnalyticsModel)o).getProduct()))
				return true;
		}
		
		return false;
	}
}
