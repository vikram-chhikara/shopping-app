package ucsd.shoppingApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import ucsd.shoppingApp.models.AnalyticsModel;
import ucsd.shoppingApp.models.SaleModel;

public class SalesDAO {
	/* Get row names */
	private static String GET_STATES_ALPHA = "SELECT state_id, tot.state_name, SUM(tot.price) AS price "
			+ "FROM (SELECT s.id as state_id, s.state_name, COALESCE((pr.price * quantity), 0) AS price "
			+ "FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN "
			+ "((products_in_cart pc JOIN product pr ON pc.product_id = pr.id) "
			+ "JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = person_id) as tot "
			+ "GROUP BY tot.state_name, state_id ORDER BY tot.state_name LIMIT 20 OFFSET ?";
	private static String GET_STATES_TOP = "SELECT state_id, tot.state_name, SUM(tot.price) AS price "
			+ "FROM (SELECT s.id as state_id, s.state_name, COALESCE((pr.price * quantity), 0) AS price "
			+ "FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN "
			+ "((products_in_cart pc JOIN product pr ON pc.product_id = pr.id) "
			+ "JOIN shopping_cart sc ON pc.cart_id = sc.id) ON p.id = person_id) as tot "
			+ "GROUP BY tot.state_name, state_id ORDER BY price DESC LIMIT 50 OFFSET ?";
	private static final String GET_PEOPLE_ALPHA = "SELECT p.id, p.person_name, COALESCE(SUM(pi.price*pi.quantity),0) as price "
			+ "FROM (person p LEFT OUTER JOIN shopping_cart s on  p.id = s.person_id) "
			+ "LEFT OUTER JOIN (product pr LEFT OUTER JOIN products_in_cart pi ON "
			+ "pr.id = pi.product_id) on s.id = pi.cart_id "
			+ "GROUP BY p.id, p.person_name ORDER BY p.person_name LIMIT 20 OFFSET ?";
	private static final String GET_PEOPLE_TOP = "SELECT p.id, p.person_name, COALESCE(SUM(pi.price*pi.quantity),0) as price "
			+ "FROM (person p LEFT OUTER JOIN shopping_cart s on  p.id = s.person_id) "
			+ "LEFT OUTER JOIN (product pr LEFT OUTER JOIN products_in_cart pi "
			+ "ON pr.id = pi.product_id) on s.id = pi.cart_id GROUP BY p.id, p.person_name "
			+ "ORDER BY price DESC LIMIT 20 OFFSET ?";
	//Project Part 3
	private static final String GET_PRECOMP_STATES = "SELECT state_id, state_name, SUM(price) as price "
			+ "FROM State_Precomputed GROUP BY state_id, state_name ORDER BY price DESC"; // SELECT * FROM State_Precomputed
	
	/* Get row names with extra category filter */
	private static String GET_STATES_TOP_FILTER = "SELECT state_id, tot.state_name, SUM(tot.price) AS price "
			+ "FROM (SELECT s.id as state_id, s.state_name, COALESCE((pr.price * quantity), 0) AS price "
			+ "FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN "
			+ "((products_in_cart pc JOIN product pr ON pc.product_id = pr.id) "
			+ "JOIN shopping_cart sc ON pc.cart_id = sc.id AND category_id = ?) ON p.id = person_id) as tot "
			+ "GROUP BY tot.state_name, state_id ORDER BY price DESC LIMIT 50 OFFSET ?";
	private static String GET_CUST_TOP_FILTER = "SELECT p.id, p.person_name, COALESCE(SUM(pi.price*pi.quantity),0) as price "
			+ "FROM (person p LEFT OUTER JOIN shopping_cart s on  p.id = s.person_id) "
			+ "LEFT OUTER JOIN (product pr JOIN products_in_cart pi "
			+ "ON pr.id = pi.product_id AND category_id = ?) on s.id = pi.cart_id "
			+ "GROUP BY p.id, p.person_name "
			+ "ORDER BY price DESC LIMIT 20 OFFSET ?";
	private static String GET_STATES_ALPHABETICAL_FILTER = "SELECT tot.state_name, SUM(tot.price) AS price "
			+ "FROM (SELECT s.state_name, COALESCE((pr.price * quantity), 0) AS price "
			+ "FROM (state s LEFT OUTER JOIN person p ON p.state_id = s.id) LEFT OUTER JOIN "
			+ "((products_in_cart pc JOIN product pr ON pc.product_id = pr.id) "
			+ "JOIN shopping_cart sc ON pc.cart_id = sc.id AND category_id = ?) ON p.id = person_id) as tot "
			+ "GROUP BY tot.state_name ORDER BY state_name LIMIT 50 OFFSET ?";
	private static String GET_CUST_ALPHA_FILTER = "SELECT p.id, p.person_name, COALESCE(SUM(pi.price*pi.quantity),0) as price "
			+ "FROM (person p LEFT OUTER JOIN shopping_cart s on  p.id = s.person_id) "
			+ "LEFT OUTER JOIN (product pr JOIN products_in_cart pi "
			+ "ON pr.id = pi.product_id AND category_id = ?) on s.id = pi.cart_id "
			+ "GROUP BY p.id, p.person_name "
			+ "ORDER BY person_name LIMIT 20 OFFSET ?";
	//Project Part 3
	private static String GET_PRECOMP_STATES_CAT = "SELECT * FROM State_Precomputed WHERE category_id = ? ORDER BY price DESC";
	
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
	private static String GET_LIMITED_TABLE = "(Select * FROM States_Products_Precomputed spp "
			+ "WHERE spp.product_id IN (SELECT pp.product_id FROM Products_Precomputed pp ORDER BY price DESC LIMIT 50))";
	private static String GET_CAT_LIMITED_TABLE = "Select * FROM States_Products_Precomputed spp "
			+ "WHERE spp.product_id IN (SELECT pp.product_id FROM Products_Precomputed pp WHERE pp.category_id = ? ORDER BY price DESC LIMIT 50)";
	
	/** Log Table Update */
	private static String USER_REFRESH = "UPDATE logOwner SET last_refresh = (now() AT TIME ZONE 'UTC') WHERE user_id = ?";
	private static String USER_TIME = "SELECT * FROM logOwner WHERE user_id = ?";
	private static String GET_LOG_TABLE = "WITH tot_table AS (SELECT logTest.state_id, logTest.prod_id, logTest.category_id, "
			+ "SUM(logTest.price) as price FROM logTest, logOwner "
			+ "WHERE logTest.bought_time > logOwner.last_refresh AND logOwner.user_id = ? "
			+ "GROUP BY state_id, prod_id, logTest.category_id ORDER BY price), "
			+ "state_pri AS (SELECT state_id, SUM(price) AS price FROM tot_table GROUP BY state_id), "
			+ "prod_pri AS (SELECT prod_id, SUM(price) AS price FROM tot_table GROUP BY prod_id) "
			+ "SELECT tt.state_id, tt.prod_id, tt.category_id, tt.price, sp.price AS state_sum, pp.price AS prod_sum "
			+ "from state_pri sp CROSS JOIN prod_pri pp "
			+ "LEFT OUTER JOIN tot_table tt ON ( pp.prod_id = tt.prod_id and sp.state_id = tt.state_id) ";
	
	/* Update Precomputed Tables from the Log Table */
	private static String STATE_PRECOMP_UPDATE = "UPDATE State_Precomputed "
			+ "SET price = State_Precomputed.price + lt.price, time = (now() AT TIME ZONE 'UTC') "
			+ "FROM (SELECT state_id, category_id, SUM(price) AS price FROM logTest GROUP BY state_id, category_id) "
			+ "AS lt JOIN logTest lt1 ON lt.state_id = lt1.state_id AND lt.category_id = lt1.category_id "
			+ "WHERE State_Precomputed.state_id = lt.state_id AND State_Precomputed.category_id = lt.category_id "
			+ "AND State_Precomputed.time < lt1.bought_time";
	private static String PROD_PRECOMP_UPDATE = "UPDATE Products_Precomputed "
			+ "SET price = Products_Precomputed.price + (lt.price) "
			+ "FROM (SELECT prod_id, SUM(price) AS price FROM logTest GROUP BY prod_id) as lt "
			+ "JOIN logTest lt1 ON lt.prod_id = lt1.prod_id WHERE Products_Precomputed.product_id = lt.prod_id";
	private static String CELL_PRECOMP_UPDATE = "UPDATE States_Products_Precomputed "
			+ "SET price = States_Products_Precomputed.price + (lt.price) "
			+ "FROM (SELECT prod_id, state_id, SUM(price) AS price FROM logTest GROUP BY prod_id, state_id) as lt "
			+ "JOIN logTest lt1 ON lt.prod_id = lt1.prod_id AND lt.state_id = lt1.state_id "
			+ "WHERE States_Products_Precomputed.product_id = lt.prod_id "
			+ "AND States_Products_Precomputed.state_id = lt.state_id "
			+ "AND States_Products_Precomputed.time < lt1.bought_time";
	
	private static String DELETE_LOG = "DELETE FROM logTest WHERE bought_time < (now() AT TIME ZONE 'UTC')";
	
	private Connection con;

	public SalesDAO(Connection con) {
		this.con = con;
	}

	/** Project Part 3*/
	/*
	public HashMap<Integer, Double> getLogTable(int userid) {
		HashMap<Integer, Double> table = new HashMap<Integer, Double>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		int prodID = 0;
		int stateID = 0;
		int catID = 0;
		Double pri = 0.0;
		java.sql.Timestamp t = null;
		
		try {
			pstmt = con.prepareStatement(GET_LOG_TABLE);
			pstmt.setInt(1, userid);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				prodID = rs.getInt("prod_id");
				stateID = rs.getInt("state_id");
				catID = rs.getInt("category_id");
				pri = rs.getDouble("price");
				t = rs.getTimestamp("bought_time");
				
				SaleModel s = new SaleModel(prodID, stateID, catID, pri, t);
				table.add(s);
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
	}*/
	
	public ArrayList<SaleModel> getLogTable(int userid) {
		ArrayList<SaleModel> table = new ArrayList<SaleModel>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		int prodID = 0;
		int stateID = 0;
		int catID = 0;
		Double pri = 0.0;
		Double spri = 0.0;
		Double ppri = 0.0;
		
		try {
			pstmt = con.prepareStatement(GET_LOG_TABLE);
			pstmt.setInt(1, userid);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				prodID = rs.getInt("prod_id");
				stateID = rs.getInt("state_id");
				catID = rs.getInt("category_id");
				pri = rs.getDouble("price");
				spri = rs.getDouble("state_sum");
				ppri = rs.getDouble("prod_sum");
				
				SaleModel s = new SaleModel(prodID, stateID, catID, pri, spri, ppri);
				table.add(s);
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
	
	public java.sql.Timestamp lastTimeAndClear(int userid) {
		System.out.println("Find last user refresh");
		java.sql.Timestamp lt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = con.prepareStatement(USER_TIME);
			pstmt.setInt(1, userid);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				lt = rs.getTimestamp("last_refresh");
			}
			
			refresh(userid);
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
		return lt;
	}
	
	public void refresh(int userid) {
		System.out.println("Update log table");
		PreparedStatement pstmt = null;
		int rs = 0;
		
		try {
			pstmt = con.prepareStatement(USER_REFRESH);
			pstmt.setInt(1, userid);
			
			rs = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public LinkedHashMap<Integer, AnalyticsModel> getStatePrecomp(int cat) {
		LinkedHashMap<Integer, AnalyticsModel> table = new LinkedHashMap<Integer, AnalyticsModel>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String row = "";
		String prod = "";
		Double pri = 0.0;
		int amID = 0;
		
		try {
			if(cat == 0) {
				pstmt = con.prepareStatement(GET_PRECOMP_STATES);
			}
			else {
				pstmt = con.prepareStatement(GET_PRECOMP_STATES_CAT);
				pstmt.setInt(1, cat);
			}
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				row = rs.getString("state_name");
				pri = rs.getDouble("price");
				amID = rs.getInt("state_id");
				
				AnalyticsModel a = new AnalyticsModel(row,prod,pri, amID);
				table.put(amID, a);
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
	
	public ArrayList<AnalyticsModel> getStateList(int cat) {
		ArrayList<AnalyticsModel> table = new ArrayList<AnalyticsModel>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String row = "";
		String prod = "";
		Double pri = 0.0;
		int amID = 0;
		
		try {
			if(cat == 0) {
				pstmt = con.prepareStatement(GET_PRECOMP_STATES);
			}
			else {
				pstmt = con.prepareStatement(GET_PRECOMP_STATES_CAT);
				pstmt.setInt(1, cat);
			}
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				row = rs.getString("state_name");
				pri = rs.getDouble("price");
				amID = rs.getInt("state_id");
				
				AnalyticsModel a = new AnalyticsModel(row,prod,pri, amID);
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
	
	/** List of states for row ordering */
	public ArrayList<AnalyticsModel> getStateList(String o, int off, int cat) {
		ArrayList<AnalyticsModel> table = new ArrayList<AnalyticsModel>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String row = "";
		String prod = "";
		Double pri = 0.0;
		int amID = 0;
		
		try {
			if(o.equals("t")) {
				if(cat == 0) {
					pstmt = con.prepareStatement(GET_STATES_TOP);
					pstmt.setInt(1, 20 * off);
				}
				else {
					pstmt = con.prepareStatement(GET_STATES_TOP_FILTER);
					pstmt.setInt(1, cat);
					pstmt.setInt(2, 20 * off);
				}
			} else {
				if(cat == 0) {
					pstmt = con.prepareStatement(GET_STATES_ALPHA);
					pstmt.setInt(1, 20 * off);
				}
				else {
					pstmt = con.prepareStatement(GET_STATES_ALPHABETICAL_FILTER);
					pstmt.setInt(1, cat);
					pstmt.setInt(2, 20 * off);
				}
			}
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				row = rs.getString("state_name");
				pri = rs.getDouble("price");
				amID = rs.getInt("state_id");
				
				AnalyticsModel a = new AnalyticsModel(row,prod,pri, amID);
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
	
	/** Precomputed Cell Table */
	public HashMap<Integer, HashMap<Integer, Double>> getLimitedTable(int cat) {
		HashMap<Integer, HashMap<Integer, Double>> table = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, Double> prodpri;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String row = "";
		String prod = "";
		int stateID, prodID;
		Double pri = 0.0;
		
		try {
			if(cat == 0) {
					pstmt = con.prepareStatement(GET_LIMITED_TABLE);
			}
			else {
				pstmt = con.prepareStatement(GET_CAT_LIMITED_TABLE);
				pstmt.setInt(1, cat);
			}
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				row = rs.getString("state_name");
				prod = rs.getString("product_name");
				pri = rs.getDouble("price");
				stateID = rs.getInt("state_id");
				prodID = rs.getInt("product_id");
				
				if(table.containsKey(stateID)) {
					table.get(stateID).put(prodID, pri);
				} else {
					prodpri = new HashMap<Integer, Double>();
					prodpri.put(prodID, pri);
					table.put(stateID, prodpri);
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
	
	/** List of customers for row ordering */
	/*
	public ArrayList<AnalyticsModel> getPersonList(String o, int off, int cat) {
		long tableTime = System.nanoTime();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<AnalyticsModel> result = new ArrayList<AnalyticsModel>();
		
		String row = "";
		String prod = "";
		Double pri = 0.0;
		
		try {
			if(o.equals("t")) {
				if(cat == 0) {
					pstmt = con.prepareStatement(GET_PEOPLE_TOP);
					pstmt.setInt(1, 20 * off);
				} else {
					pstmt = con.prepareStatement(GET_CUST_TOP_FILTER);
					pstmt.setInt(1, cat);
					pstmt.setInt(2, 20 * off);
				}
			} else {
				if(cat == 0) {
					pstmt = con.prepareStatement(GET_PEOPLE_ALPHA);
					pstmt.setInt(1, 20 * off);
				} else {
					pstmt = con.prepareStatement(GET_CUST_ALPHA_FILTER);
					pstmt.setInt(1, cat);
					pstmt.setInt(2, 20 * off);
				}
			}
			
			rs = pstmt.executeQuery();

			long deltaTime = System.nanoTime() - tableTime;
		    System.out.println("Query (" + pstmt + ") Time: " + (deltaTime/1000000));
			
			
			while (rs.next()) {
				row = rs.getString("person_name");
				pri = rs.getDouble("price");
				
				AnalyticsModel a = new AnalyticsModel(row,prod,pri);
				result.add(a);
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
	*/
	
	
	
	/** List people or state and associated prices per product */
	public HashMap<String, HashMap<String, Double>> getTable(String type, int cat) {
		HashMap<String, HashMap<String, Double>> table = new HashMap<String, HashMap<String, Double>>();
		HashMap<String, Double> prodpri;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String row = "";
		String prod = "";
		Double pri = 0.0;
		
		try {
			if(type.equals("person")) {
				//if(cat == 0)
					pstmt = con.prepareStatement(GET_CUST_PRODS);
			}
			else {
				pstmt = con.prepareStatement(GET_STATE_PRODS);
			}
			
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
	
	public void updatePrecomp(int cat) {
		Statement stmt = null;
		int rs = 0;
		
		try {
			con.commit();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			
			updateStatePrecomp(cat);
			updateProdPrecomp(cat);
			updateCellPrecomp(cat);
			
			rs = stmt.executeUpdate(DELETE_LOG);
			con.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (Exception ef) {
				e.printStackTrace();
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void updateStatePrecomp(int cat) {
		System.out.println("Update state precomp table");
		Statement stmt = null;
		int rs = 0;
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeUpdate(STATE_PRECOMP_UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void updateProdPrecomp(int cat) {
		System.out.println("Update prod precomp table");
		Statement stmt = null;
		int rs = 0;
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeUpdate(PROD_PRECOMP_UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void updateCellPrecomp(int cat) {
		System.out.println("Update cell precomp table");
		Statement stmt = null;
		int rs = 0;
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeUpdate(CELL_PRECOMP_UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
