package ucsd.shoppingApp.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;


import ucsd.shoppingApp.ConnectionManager;
import ucsd.shoppingApp.SimilarProductsDAO;
import ucsd.shoppingApp.models.AnalyticsModel;
import ucsd.shoppingApp.models.SimilarProductsModel;

/**
 * Servlet implementation class SimilarProductsController
 */
@WebServlet("/SimilarProductsController")
public class SimilarProductsController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private SimilarProductsDAO aDB = null;
    private ArrayList<SimilarProductsModel> aList = null;
	private Connection con = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SimilarProductsController() {
        super();
        // TODO Auto-generated constructor stub
    }
	@Override
	public void init() throws ServletException {
		con = ConnectionManager.getConnection();
		aDB = new SimilarProductsDAO(con);
		aList = new ArrayList<SimilarProductsModel>();
		
		super.init();
	}
	
	/** TODO
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String forward = "./similar_products.jsp";
		
		System.out.println("DO table");
		
		if(request.getParameter("refresh") != null && Integer.parseInt(request.getParameter("refresh")) == 1) {
			request.getSession().setAttribute("similar_table", null);
			request.setAttribute("refresh", 0);
			request.getRequestDispatcher(forward).forward(request, response);
			return;
		}

		ArrayList<SimilarProductsModel> result = new ArrayList<SimilarProductsModel>();
		result = aDB.getSimilarity();
		request.getSession().setAttribute("similar_table", result);
		
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

