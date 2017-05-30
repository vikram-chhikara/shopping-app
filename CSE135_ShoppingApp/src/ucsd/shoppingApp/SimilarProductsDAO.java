package ucsd.shoppingApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;

import ucsd.shoppingApp.models.AnalyticsModel;
import ucsd.shoppingApp.models.SimilarProductsModel;

public class SimilarProductsDAO {
	private Connection con;
	private static String GET_PRODUCTS_CUSTOMERS = "SELECT pr.product_name,p.person_name, SUM(pi.price*pi.quantity) as Total"
				+"FROM (person p LEFT OUTER JOIN shopping_cart s on  p.id = s.person_id) LEFT OUTER JOIN "
				+ "(product pr LEFT OUTER JOIN products_in_cart pi ON pr.id = pi.product_id) on s.id = pi.cart_id"
				+ "GROUP BY p.id, p.person_name, pr.product_name ORDER BY pr.product_name";
	public SimilarProductsDAO(Connection con) {
		this.con = con;
	}
	/** Map of Products to their Customers */
	public ArrayList<SimilarProductsModel> getProdCustomer() {
		//HashMap<String,List<SimilarProductsModel>> pc_map= new HashMap<String,List<SimilarProductsModel>>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<SimilarProductsModel> result = new ArrayList<SimilarProductsModel>();
		String pr = "";
		String p = "";
		Double pri = 0.0;
		
		try{
			pstmt = con.prepareStatement(GET_PRODUCTS_CUSTOMERS);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				pr = rs.getString("product_name");
				p = rs.getString("person_name");
				pri = rs.getDouble("Total");
				
				SimilarProductsModel a = new SimilarProductsModel(pr,p,pri);
				result.add(a);
			}
			
		} catch(Exception e){
			e.printStackTrace();

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
		
	}
}
