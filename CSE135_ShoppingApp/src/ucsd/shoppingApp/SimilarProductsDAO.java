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
	private static String GET_SIMILARITY = "(WITH subquery AS( "
			+ "SELECT pr.id as prod_id, pr.product_name, p.id as person_id, p.person_name, SUM(pi.price*pi.quantity) as price "
			+ "FROM (person p JOIN shopping_cart s on  p.id = s.person_id) "
			+ "JOIN (product pr JOIN products_in_cart pi ON pr.id = pi.product_id) on s.id = pi.cart_id "
			+ "GROUP BY pr.id, pr.product_name, p.id,p.person_name ORDER BY pr.product_name), "
			+ "prodcount AS(SELECT subquery.prod_id, COUNT(subquery.prod_id) AS co FROM subquery GROUP BY prod_id) "
			+ "SELECT q1.prod_id as id_1, q1.product_name as p1, q2.prod_id as id_2, q2.product_name as p2, "
			+ "(SUM(q1.price*q2.price)/(prodc.co*prodoc.co)) as Cosine_Similarity, prodc.co, prodoc.co "
			+ "FROM subquery q1 JOIN subquery q2 on q1.person_id = q2.person_id, prodcount prodc, prodcount prodoc "
			+ "WHERE q1.prod_id < q2.prod_id AND prodc.prod_id = q1.prod_id AND prodoc.prod_id = q2.prod_id "
			+ "GROUP BY  q1.prod_id, q1.product_name, q2.prod_id, q2.product_name, prodc.co, prodoc.co "
			+ "ORDER BY Cosine_Similarity desc LIMIT 100)";
	
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
}
