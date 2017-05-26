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
		
		//get Table
		System.out.println("Getting table");
		if(sort.equals("a")) {
			aList = aDB.getPersonTable();
		} else {
			aList = new ArrayList<AnalyticsModel>();
		}
		request.getSession().setAttribute("alist", aList);
		
		//Get row list 
		ArrayList<String> rowList = new ArrayList<String>();
		Connection conn = null;
		if(row == null || row.equals("c")) {
			conn = ConnectionManager.getConnection();
			PersonDAO pDB = new PersonDAO(conn);
			
			try {
				rowList = pDB.getPersonList();
			}
			catch(SQLException e) {
				System.out.println("Customer Row names access failure");
			} finally {
				if (conn != null) {
	                try {
	                    conn.close();
	                } catch (SQLException e) { } // Ignore
	                conn = null;
	            }
			}
		} else {
			rowList = aDB.getStateList();
		}
		request.getSession().setAttribute("rowList", rowList);

		//and product list
		try {
			conn = ConnectionManager.getConnection();
			ProductDAO prodDB = new ProductDAO(conn);
			rowList = prodDB.getProductList();
			request.getSession().setAttribute("prodList", rowList);
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
