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
	private static String GET_SIMILARITY = "(WITH subquery AS( "
			+ "SELECT pr.id as prod_id, pr.product_name, p.id as person_id, p.person_name, SUM(pi.price*pi.quantity) as price "
			+ "FROM (person p JOIN shopping_cart s on  p.id = s.person_id) "
			+ "	JOIN (product pr JOIN products_in_cart pi ON pr.id = pi.product_id) on s.id = pi.cart_id "
			+ "	GROUP BY pr.id, pr.product_name, p.id,p.person_name "
			+ "	ORDER BY pr.product_name) "
			+ "SELECT q1.prod_id as id_1, q1.product_name as p1, q2.prod_id as id_2, q2.product_name as p2, "
			+ "SUM(q1.price*q2.price) as Cosine_Similarity "
			+ "FROM subquery q1 JOIN subquery q2 on q1.person_id = q2.person_id "
			+ "WHERE q1.prod_id < q2.prod_id "
			+ "GROUP BY  q1.prod_id, q1.product_name, q2.prod_id, q2.product_name ORDER BY Cosine_Similarity desc "
			+ "LIMIT 100)";
	
	public SimilarProductsDAO(Connection con) {
		this.con = con;
	}
	
	public ArrayList<SimilarProductsModel> getSimilarity() {
		System.out.println("getting query");
		ArrayList<SimilarProductsModel> table = new ArrayList<SimilarProductsModel>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String prod1 = "";
		String prod2 = "";
		Double pri = 0.0;
		
		try {
			pstmt = con.prepareStatement(GET_SIMILARITY);
			System.out.println(pstmt);
			System.out.println("QUERY");
			rs = pstmt.executeQuery();
			System.out.println("QUERY DONE");
			
			while (rs.next()) {
				prod1 = rs.getString("p1");
				prod2 = rs.getString("p2");
				pri = rs.getDouble("Cosine_Similarity");
				
				SimilarProductsModel a = new SimilarProductsModel(prod1,prod2,pri);
				table.add(a);
			}
		} catch (Exception e) {
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
		return table;
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
