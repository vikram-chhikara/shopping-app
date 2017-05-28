package ucsd.shoppingApp.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
    private ArrayList<AnalyticsModel> aList = null;
	private Connection con = null;
	
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
		aList = new ArrayList<AnalyticsModel>();
		
		super.init();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String forward = "./salesAnalytics.jsp";

		//check dropdown menu(s)
		String row = request.getParameter("rowChoice");
		request.setAttribute("rowChoice", row);
		String sort = request.getParameter("orderChoice");
		request.setAttribute("orderChoice", sort);
		int cat = Integer.parseInt(request.getParameter("catFilter"));
		request.setAttribute("catFilter", cat);
		
		//get Table
		System.out.println("Getting table");
		if(row.equals("c")) {
			aList = aDB.getPersonAlphaTable();
		} else if (row.equals("s")) {
			aList = aDB.getStateAlphaTable();
		}
		else {
			aList = new ArrayList<AnalyticsModel>();
		}
		request.getSession().setAttribute("alist", aList);
		
		//Get row list 
		ArrayList<AnalyticsModel> rowList = new ArrayList<AnalyticsModel>();
		Connection conn = null;
		if(row == null || row.equals("c")) {
			rowList = aDB.getPersonList(sort);
		} else {
			rowList = aDB.getStateList(sort);
		}
		request.getSession().setAttribute("rowList", rowList);

		//and product list
		ArrayList<String> colList = new ArrayList<String>();
		try {
			conn = ConnectionManager.getConnection();
			ProductDAO prodDB = new ProductDAO(conn);
			colList = prodDB.getProductList();
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
		// TODO Auto-generated method stub
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
