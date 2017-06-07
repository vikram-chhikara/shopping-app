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
	
    private HashMap<String, HashMap<String, Double>> tableVals = null;
	
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
		tableVals = new HashMap<String, HashMap<String, Double>>();

		super.init();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String forward = "./salesAnalytics.jsp";
		
		//Refresh page if linked from menu
		if(request.getParameter("clean") != null && Integer.parseInt(request.getParameter("clean")) == 1) {
			request.getSession().setAttribute("rowChoice", "states");
			request.getSession().setAttribute("orderChoice", "t");
			request.getSession().setAttribute("catFilter", 0);
			request.getSession().setAttribute("pageCount", 0);
			request.getSession().setAttribute("columnCount", 0);
			request.getSession().setAttribute("nextClick", 0);
			request.getSession().setAttribute("alist", new HashMap<String, HashMap<String, Double>>());
			request.getSession().setAttribute("rowList", new ArrayList<AnalyticsModel>());
			request.getSession().setAttribute("prodList", new ArrayList<AnalyticsModel>());
			request.setAttribute("clean", 0);
			request.getRequestDispatcher(forward).forward(request, response);
			return;
		} else if(request.getParameter("clean") != null && Integer.parseInt(request.getParameter("clean")) == 2) {
			//Do Non-full refresh
		}

		//check dropdown menu(s)
		String row = "states";
		String sort = "t";
		int cat;
		
		//keep track of row values and sorting methods/filters
		if(request.getSession().getAttribute("nextClick") == null || Integer.parseInt(request.getSession().getAttribute("nextClick").toString()) == 0) {
			if(request.getParameter("catFilter") != null) {
				cat = Integer.parseInt(request.getParameter("catFilter"));
			} else {
				cat = 0;
			}
			request.getSession().setAttribute("catFilter", cat);
		} else {
			row = request.getSession().getAttribute("rowChoice").toString();
			sort = request.getSession().getAttribute("orderChoice").toString();
			cat = Integer.parseInt(request.getSession().getAttribute("catFilter").toString());
		}
		
		int pagecount = 0;
		//page count for row display
		if(request.getSession().getAttribute("pageCount") != null) {
			String pc = request.getSession().getAttribute("pageCount").toString();
			pagecount = Integer.parseInt(pc);
			if(request.getParameter("nextRow") != null) {
				pagecount++;
				request.getSession().setAttribute("nextClick", 1);
			}
			request.getSession().setAttribute("pageCount", pagecount);
		}
		
		//col count for column display
		int colcount = 0;
		if(request.getSession().getAttribute("columnCount") != null) {
			String pc = request.getSession().getAttribute("columnCount").toString();
			colcount = Integer.parseInt(pc);
			
			if(request.getParameter("nextCol") != null) {
				colcount++;
				request.getSession().setAttribute("nextClick", 1);
			}
			request.getSession().setAttribute("columnCount", colcount);
		}
		
		//get Table
		if(row.equals("customers")) {
			tableVals = aDB.getTable("person", 0);
		} else if (row.equals("states")) {
			tableVals = aDB.getTable("state", cat);
		}
		else {
			tableVals = new HashMap<String, HashMap<String, Double>>();
		}
		request.getSession().setAttribute("alist", tableVals);
		
		//Get row list 
		ArrayList<AnalyticsModel> rowList = new ArrayList<AnalyticsModel>();
		Connection conn = null;
		if(row == null || row.equals("customers")) {
			rowList = aDB.getPersonList(sort, pagecount, cat);
		} else {
			rowList = aDB.getStateList(sort, pagecount, cat);
		}
		request.getSession().setAttribute("rowList", rowList);

		//and product list
		ArrayList<AnalyticsModel> colList = new ArrayList<AnalyticsModel>();
		try {
			conn = ConnectionManager.getConnection();
			ProductDAO prodDB = new ProductDAO(conn);
			
			colList = prodDB.getProductList(sort, cat, colcount);
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
