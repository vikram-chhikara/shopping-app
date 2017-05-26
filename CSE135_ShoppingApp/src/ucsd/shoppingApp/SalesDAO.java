package ucsd.shoppingApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ucsd.shoppingApp.models.AnalyticsModel;

public class SalesDAO {
	private static String GET_STATES = "SELECT state_name FROM state ORDER BY state_name";
	private static String GET_CUST_PRODS = "SELECT p.person_name, pr.product_name, pi.price "
			+ "FROM (person p LEFT OUTER JOIN shopping_cart s on  p.id = s.person_id) "
			+ "LEFT OUTER JOIN (product pr LEFT OUTER JOIN products_in_cart pi ON pr.id = pi.product_id) on s.id = pi.cart_id "
			+ "ORDER BY p.person_name";
	private static String GET_STATE_PRODS = "SELECT s.state_name,pr.product_name, SUM(pr.price) AS price "
			+ "FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN "
			+ "((products_in_cart pc JOIN product pr ON pc.product_id = pr.id) "
			+ "JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = sc.person_id "
			+ "GROUP BY s.state_name, pr.product_name ORDER BY s.state_name";
	private static String GET_TOP_CUST_PRODS = "SELECT t.person_name, SUM(t.price) AS Total "
			+ "FROM (SELECT p.person_name, pr.product_name, pi.price "
			+ "FROM person p, product pr, shopping_cart s, products_in_cart pi "
			+ "WHERE p.id = s.id and s.id = pi.cart_id and pr.id = pi.product_id "
			+ "ORDER BY p.person_name) t GROUP BY t.person_name ORDER BY Total DESC "
			+ "LIMIT 20";
	
	private Connection con;

	public SalesDAO(Connection con) {
		this.con = con;
	}

	public ArrayList<String> getStateList() {
		ArrayList<String> table = new ArrayList<String>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String elem = "";
		
		try {
			pstmt = con.prepareStatement(GET_STATES);
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				elem = rs.getString("state_name");
				if (elem == null) {
					elem = "";
				}
				table.add(elem);
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
	
	public ArrayList<AnalyticsModel> getPersonAlphaTable() {
		ArrayList<AnalyticsModel> table = new ArrayList<AnalyticsModel>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String row = "";
		String prod = "";
		Double pri = 0.0;
		
		try {
			pstmt = con.prepareStatement(GET_CUST_PRODS);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				row = rs.getString("person_name");
				prod = rs.getString("product_name");
				pri = rs.getDouble("price");
				
				if(prod == null) {
					prod = "";
				}
				if(pri == null) {
					pri = 0.0;
				}
				
				AnalyticsModel a = new AnalyticsModel(row,prod,pri);
				table.add(a);

				System.out.println(a.getRowName());
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
	
	public ArrayList<AnalyticsModel> getStateAlphaTable() {
		ArrayList<AnalyticsModel> table = new ArrayList<AnalyticsModel>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String row = "";
		String prod = "";
		Double pri = 0.0;
		
		try {
			pstmt = con.prepareStatement(GET_STATE_PRODS);
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				row = rs.getString("state_name");
				prod = rs.getString("product_name");
				pri = rs.getDouble("price");
				
				if(prod == null) {
					prod = "";
				}
				if(pri == null) {
					pri = 0.0;
				}
				
				//Check table
				
				AnalyticsModel a = new AnalyticsModel(row,prod,pri);
				table.add(a);
				System.out.println(a.getRowName());
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
	
	public ArrayList<AnalyticsModel> getPersonTopTable() {
		ArrayList<AnalyticsModel> table = new ArrayList<AnalyticsModel>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String row = "";
		String prod = "";
		Double pri = 0.0;
		
		try {
			pstmt = con.prepareStatement(GET_TOP_CUST_PRODS);
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				row = rs.getString("person_name");
				prod = rs.getString("product_name");
				pri = rs.getDouble("price");
				
				if(prod == null) {
					prod = "";
				}
				if(pri == null) {
					pri = 0.0;
				}
				
				AnalyticsModel a = new AnalyticsModel(row,prod,pri);
				table.add(a);
				System.out.println(a.getRowName());
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
	
	/*
	private ArrayList<AnalyticsModel> cleanTable(ArrayList<AnalyticsModel> a) {
		ArrayList<AnalyticsModel> retT = new ArrayList<AnalyticsModel>();
		int i = 0;
		
		String prev = (a.get(i)).getRowName();
		
		
		
		return retT;
	}*/
}
