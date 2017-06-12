package ucsd.shoppingApp.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ucsd.shoppingApp.ConnectionManager;
import ucsd.shoppingApp.PersonDAO;
import ucsd.shoppingApp.ProductDAO;
import ucsd.shoppingApp.SalesDAO;
import ucsd.shoppingApp.models.AnalyticsModel;

/**
 * Servlet implementation class SalesAnalyticsController
 */
@WebServlet("/SalesAnalyticsController")
public class SalesAnalyticsController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private SalesDAO aDB = null;
	private Connection con = null;
	
    private HashMap<Integer, HashMap<Integer, Double>> tableVals = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SalesAnalyticsController() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	@Override
	public void init() throws ServletException {
		con = ConnectionManager.getConnection();
		aDB = new SalesDAO(con);
		tableVals = new HashMap<Integer, HashMap<Integer, Double>>();

		super.init();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String forward = "./salesAnalytics.jsp";
		
		int userID = Integer.parseInt(request.getSession().getAttribute("login_id").toString());
		java.sql.Timestamp lastTime;
		
		
		//If refresh = 2, don't do a full refresh
		if(request.getParameter("clean") != null && Integer.parseInt(request.getParameter("clean")) == 2) {
			
		} else {
			//Else do a full refresh
			request.getSession().setAttribute("rowChoice", "states");
			request.getSession().setAttribute("orderChoice", "t");
			request.getSession().setAttribute("catFilter", 0);
			
			/** Update table from the log table */	
			//Make appropriate changes to precomputed tables
			aDB.updatePrecomp();
			
			//set sorting vals
			int cat;
			
			//see if filtering by category
			if(request.getParameter("catFilter") != null) {
				cat = Integer.parseInt(request.getParameter("catFilter"));
			} else {
				cat = 0;
			}
			
			//get Table
			tableVals = aDB.getLimitedTable(cat);
			request.getSession().setAttribute("alist", tableVals);
			
			//Get row list 
			ArrayList<AnalyticsModel> rowList = new ArrayList<AnalyticsModel>();
			rowList = aDB.getStateList(cat);
			request.getSession().setAttribute("rowList", rowList);

			//and product list
			Connection conn = null;
			ArrayList<AnalyticsModel> colList = new ArrayList<AnalyticsModel>();
			try {
				conn = ConnectionManager.getConnection();
				ProductDAO prodDB = new ProductDAO(conn);
				
				colList = prodDB.getPrecomputedProdList(cat);
				request.getSession().setAttribute("prodList", colList);
			}
			catch(SQLException e) {
				System.out.println("Product Column names access failure");
			} finally {
				if (conn != null) {
	                try {
	                    conn.close();
	                } catch (SQLException e) { } // Ignore
	                conn = null;
	            }
			}
		}
		
		//Update logOwner refresh time and retrieve last time it was updated
		lastTime = aDB.lastTimeAndClear(userID);
		
		RequestDispatcher view = request.getRequestDispatcher(forward);
		view.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		super.destroy();
	}

}
