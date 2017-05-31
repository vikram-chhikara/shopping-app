package ucsd.shoppingApp.models;

public class SimilarProductsModel {
	private String product1;
	private String product2;
	private double price;

	public SimilarProductsModel(){
	}
	
	public SimilarProductsModel(String p1, String p2, double pri) {
		this.product1 = p1;
		this.product2 = p2;
		this.price = pri;
	}

	public String getProduct1() {
		return product1;
	}

	public void setProduct1(String pr) {
		this.product1 = pr;
	}

	public String getProduct2() {
		return product2;
	}

	public void setProduct2(String p) {
		this.product2 = p;
	}
	
	public double getPrice() {
		return price;
	}

	public void setPrice(double pri) {
		this.price = pri;
	}
	@Override
	public boolean equals(Object o)
	{
		//If the compared object is null, the two are not the same
		if(o == null)
			return false;
		
		//Checks if object is correct
		if(o instanceof SimilarProductsModel) {
			//Check if values are correct
			if(this.getProduct1().equals(((SimilarProductsModel)o).getProduct1()) && this.getProduct2().equals(((SimilarProductsModel)o).getProduct2()))
				return true;
		}
		
		return false;
	}
}
