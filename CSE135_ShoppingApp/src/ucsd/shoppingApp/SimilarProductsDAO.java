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
	private static String GET_SIMILARITY = "WITH SALES AS (select pc.product_id, p.product_name, sc.person_id,sum(pc.price*pc.quantity) as amount "
			+ "from products_in_cart pc inner join shopping_cart sc on (sc.id = pc.cart_id and sc.is_purchased = true), product p "
			+ "where pc.product_id = p.id group by pc.product_id,sc.person_id,p.product_name), "
			+ "DENOM AS (SELECT product_id, SUM(amount) as denom_sums FROM SALES GROUP BY product_id) "
			+ "SELECT s1.product_id AS id_1, s1.product_name as p1, s2.product_id AS id_2, s2.product_name AS p2, "
			+ "(SUM (s1.amount*s2.amount)/(d1.denom_sums * d2.denom_sums)) as val "
			+ "FROM SALES s1 JOIN SALES s2 ON (s1.product_id < s2.product_id), DENOM d1, DENOM d2 "
			+ "WHERE s1.person_id = s2.person_id AND d1.product_id = s1.product_id AND d2.product_id = s2.product_id "
			+ "GROUP BY (s1.product_id,s1.product_name, s2.product_id, s2.product_name,d1.denom_sums, d2.denom_sums) "
			+ "ORDER BY val desc LIMIT 100";
	
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
				pri = rs.getDouble("val");
				
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
}
