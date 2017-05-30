package ucsd.shoppingApp.models;

public class SimilarProductsModel {
	private String product;
	private String person;
	private double price;

	public SimilarProductsModel(){
	}
	
	public SimilarProductsModel(String pr, String p, double pri) {
		this.product = pr;
		this.person = p;
		this.price = pri;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String pr) {
		this.product = pr;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String p) {
		this.person = p;
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
			if(this.getProduct().equals(((SimilarProductsModel)o).getProduct()) && this.getPerson().equals(((SimilarProductsModel)o).getPerson()))
				return true;
		}
		
		return false;
	}
}
