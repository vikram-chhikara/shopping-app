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

import java.util.HashMap;
import java.util.List;


import ucsd.shoppingApp.ConnectionManager;
import ucsd.shoppingApp.PersonDAO;
import ucsd.shoppingApp.ProductDAO;
import ucsd.shoppingApp.SimilarProductsDAO;
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
		
		HashMap<String,List<SimilarProductsModel>> pc_map= new HashMap<String,List<SimilarProductsModel>>();

		ArrayList<SimilarProductsModel> result = new ArrayList<SimilarProductsModel>();
		result = aDB.getProdCustomer();
		
		for (SimilarProductsModel model : result){
			List<SimilarProductsModel> li = new ArrayList<>();
			//li.add(model.getPerson());
			if( !(pc_map.containsKey(model.getProduct())) ){
				li.add(model);
			}
			else{
				li = pc_map.get(model.getProduct());
				li.add(model);
			}
			pc_map.put(model.getProduct(), li);
		}
		request.getSession().setAttribute("prod_cust_list", pc_map);
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

