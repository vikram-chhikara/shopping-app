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
			+ "FROM person p, product pr, shopping_cart s, products_in_cart pi "
			+ "WHERE p.id = s.id and s.id = pi.cart_id and pr.id = pi.product_id "
			+ "ORDER BY p.person_name";
	
	private Connection con;

	public SalesDAO(Connection con) {
		this.con = con;
	}

	public ArrayList<String> getStateList() {
		ArrayList<String> table = new ArrayList<String>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(GET_STATES);
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				table.add(rs.getString("state_name"));
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
	
	public ArrayList<AnalyticsModel> getPersonTable() {
		ArrayList<AnalyticsModel> table = new ArrayList<AnalyticsModel>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(GET_CUST_PRODS);
			//pstmt.setString(1, "blah");
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				AnalyticsModel a = new AnalyticsModel(rs.getString("person_name"),rs.getString("product_name"),rs.getDouble("price"));
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
