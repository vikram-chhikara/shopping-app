package ucsd.shoppingApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ucsd.shoppingApp.models.AnalyticsModel;

public class SalesDAO {
	/* Get row names */
	private static String GET_STATES_ALPHA = "SELECT tot.state_name, SUM(tot.price) AS price "
			+ "FROM (SELECT s.state_name, COALESCE((pr.price * quantity), 0) AS price "
			+ "FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN "
			+ "((products_in_cart pc JOIN product pr ON pc.product_id = pr.id) "
			+ "JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = person_id) as tot "
			+ "GROUP BY tot.state_name ORDER BY tot.state_name";
	private static String GET_STATES_TOP = "SELECT tot.state_name, SUM(tot.price) AS price "
			+ "FROM (SELECT s.state_name, COALESCE((pr.price * quantity), 0) AS price "
			+ "FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN "
			+ "((products_in_cart pc JOIN product pr ON pc.product_id = pr.id) "
			+ "JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = person_id) as tot "
			+ "GROUP BY tot.state_name ORDER BY price DESC LIMIT 20 OFFSET ?";
	private static final String GET_PEOPLE_ALPHA = "SELECT p.id, p.person_name, SUM(pi.price*pi.quantity) as price "
			+ "FROM (person p LEFT OUTER JOIN shopping_cart s on  p.id = s.person_id) "
			+ "LEFT OUTER JOIN (product pr LEFT OUTER JOIN products_in_cart pi ON "
			+ "pr.id = pi.product_id) on s.id = pi.cart_id "
			+ "GROUP BY p.id, p.person_name ORDER BY p.person_name";
	private static final String GET_PEOPLE_TOP = "SELECT p.id, p.person_name, SUM(pi.price*pi.quantity) as price "
			+ "FROM (person p LEFT OUTER JOIN shopping_cart s on  p.id = s.person_id) "
			+ "LEFT OUTER JOIN (product pr LEFT OUTER JOIN products_in_cart pi "
			+ "ON pr.id = pi.product_id) on s.id = pi.cart_id GROUP BY p.id, p.person_name ORDER BY price DESC";
	
	/* Fill in data */
	private static String GET_CUST_PRODS = "SELECT p.id, p.person_name, pr.product_name, SUM(pi.price*pi.quantity) as price "
			+ "FROM (person p LEFT OUTER JOIN shopping_cart s on  p.id = s.person_id) "
			+ "JOIN (product pr JOIN products_in_cart pi "
			+ "ON pr.id = pi.product_id) on s.id = pi.cart_id GROUP BY p.id, p.person_name, pr.product_name "
			+ "ORDER BY p.person_name";
	private static String GET_STATE_PRODS = "SELECT tot.state_name, product_name, SUM(tot.price) as price "
			+ "FROM (SELECT s.state_name, pr.product_name, (pr.price * quantity) AS price "
			+ "FROM (state s JOIN person p ON p.state_id = s.id) JOIN "
			+ "((products_in_cart pc JOIN product pr ON pc.product_id = pr.id) "
			+ "JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = person_id) as tot "
			+ "GROUP BY tot.state_name, product_name";
	
	private Connection con;

	public SalesDAO(Connection con) {
		this.con = con;
	}

	/** List of states for row ordering */
	public ArrayList<AnalyticsModel> getStateList(String o) {
		ArrayList<AnalyticsModel> table = new ArrayList<AnalyticsModel>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String row = "";
		String prod = "";
		Double pri = 0.0;
		
		try {
			if(o.equals("t")) {
				pstmt = con.prepareStatement(GET_STATES_TOP);
				pstmt.setInt(1, 0);
				
				System.out.println(pstmt);
			} else {
				pstmt = con.prepareStatement(GET_STATES_ALPHA);
			}
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				row = rs.getString("state_name");
				pri = rs.getDouble("price");
				
				AnalyticsModel a = new AnalyticsModel(row,prod,pri);
				table.add(a);
			}
			
			//move null to bottom if top-k sorting
			if(o.equals("t")) {
				table = AnalyticsModel.moveNull(table);
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
	
	/** List of customers for row ordering */
	public ArrayList<AnalyticsModel> getPersonList(String o) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<AnalyticsModel> result = new ArrayList<AnalyticsModel>();
		
		String row = "";
		String prod = "";
		Double pri = 0.0;
		
		try {
			if(o.equals("t")) {
				pstmt = con.prepareStatement(GET_PEOPLE_TOP);
			} else {
				pstmt = con.prepareStatement(GET_PEOPLE_ALPHA);
			}
			
			rs = pstmt.executeQuery();

			while (rs.next()) {
				row = rs.getString("person_name");
				pri = rs.getDouble("price");
				
				if(pri == null) {
					pri = 0.0;
				}
				
				AnalyticsModel a = new AnalyticsModel(row,prod,pri);
				result.add(a);
			}
			
			//move null to bottom if top-k sorting
			if(o.equals("t")) {
				result = AnalyticsModel.moveNull(result);
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error getting row names");
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	/** List people or state and associated prices per product */
	public HashMap<String, HashMap<String, Double>> getTable(String type) {
		HashMap<String, HashMap<String, Double>> table = new HashMap<String, HashMap<String, Double>>();
		HashMap<String, Double> prodpri;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String row = "";
		String prod = "";
		Double pri = 0.0;
		
		try {
			if(type.equals("person"))
				pstmt = con.prepareStatement(GET_CUST_PRODS);
			else
				pstmt = con.prepareStatement(GET_STATE_PRODS);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				if(type.equals("person"))
					row = rs.getString("person_name");
				else
					row = rs.getString("state_name");
				prod = rs.getString("product_name");
				pri = rs.getDouble("price");
				
				if(prod == null) {
					prod = "";
				}
				if(pri == null) {
					pri = 0.0;
				}
				
				if(table.containsKey(row)) {
					table.get(row).put(prod, pri);
				} else {
					prodpri = new HashMap<String, Double>();
					prodpri.put(prod, pri);
					table.put(row, prodpri);
				}
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
