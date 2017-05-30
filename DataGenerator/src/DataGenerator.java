import java.sql.Connection;
import java.util.HashMap;
import java.util.Random;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Scanner;

public class DataGenerator {
	static int noOfCategories;
	static int noOfProducts;
	static int noOfCustomers;
	static int noOfSales;
	static Connection con = null;
	static int batchSize = 1000;
	static Random rand = new Random();
	
	private static String DROP_TABLES = "DROP TABLE products_in_cart, shopping_cart, product, category, person";
	private static String CREATE_PERSON = "CREATE TABLE person ( id SERIAL PRIMARY KEY, "
			+ "person_name TEXT NOT NULL UNIQUE, "
			+ "role_id INTEGER REFERENCES role (id) NOT NULL, "
			+ "state_id INTEGER REFERENCES state (id) NOT NULL, "
			+ "age INTEGER NOT NULL CHECK(age > 0), "
			+ "created_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now() AT TIME ZONE 'UTC'))";
	
	private static String CREATE_CATEGORY = "CREATE TABLE category ( id SERIAL PRIMARY KEY, "
			+ "category_name TEXT UNIQUE NOT NULL, "
			+ "description TEXT, "
			+ "created_by TEXT NOT NULL, "
			+ "created_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now() AT TIME ZONE 'UTC'), "
			+ "modified_by TEXT, "
			+ "modified_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now() AT TIME ZONE 'UTC'));";
	
	private static String CREATE_PRODUCT = "CREATE TABLE product( id SERIAL PRIMARY KEY, "
			+ "sku_id TEXT NOT NULL UNIQUE, "
			+ "product_name TEXT NOT NULL, "
			+ "price REAL NOT NULL CHECK(price >= 0.0), "
			+ "category_id INTEGER REFERENCES category(id) NOT NULL, "
			+ "created_by TEXT NOT NULL, "
			+ "created_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now() AT TIME ZONE 'UTC'), "
			+ "modified_by TEXT, "
			+ "modified_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now() AT TIME ZONE 'UTC'));";
	
	private static String CREATE_SHOPPING_CART = "CREATE TABLE shopping_cart( id SERIAL PRIMARY KEY, "
			+ "person_id INTEGER REFERENCES person(id) NOT NULL, "
			+ "is_purchased BOOLEAN NOT NULL, "
			+ "purchase_info TEXT, "
			+ "created_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now() AT TIME ZONE 'UTC'), "
			+ "purchased_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now() AT TIME ZONE 'UTC'));";
	
	private static String CREATE_PRODUCT_IN_CART = "CREATE TABLE products_in_cart( id SERIAL PRIMARY KEY, "
			+ "cart_id INTEGER REFERENCES shopping_cart(id) NOT NULL, "
			+ "product_id INTEGER REFERENCES product(id) NOT NULL, "
			+ "price REAL NOT NULL CHECK (price >= 0.0), "
			+ "quantity INTEGER NOT NULL CHECK (quantity > 0));";
	
	private static String INSERT_CUSTOMER = "INSERT INTO person(person_name, role_id, state_id, age) VALUES(?,"
			+ " (SELECT id FROM role WHERE role_name = 'Customer'),"
			+ "?, "
			+ " 25)";
	private static String INSERT_CATEGORY = "INSERT INTO category(category_name, description, created_by, modified_by) VALUES(?, ?, 'Data_Generator', 'Data_Generator') ";
	private static String INSERT_PRODUCT = "INSERT INTO product(sku_id, product_name, price, category_id, created_by, modified_by) VALUES(?, ?, ?, ?, 'Data_Generator', 'Data_Generator') ";
	private static String INSERT_SHOPPING_CART = "INSERT INTO shopping_cart(person_id, is_purchased, purchase_info) VALUES(?, ?, ?) ";
	private static String INSERT_PRODUCTS_IN_CART = "INSERT INTO products_in_cart(cart_id, product_id, price, quantity) VALUES(?, ?, ?, ?)";
	
	int maxCustId = 0;
	int maxCatId = 0;
	int maxProdId = 0;
	
	static HashMap<Integer, Integer> productPrices = null;
	
	public static void createConnection(String host, String port, String sid, String username, String password) {
		try { 
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + sid, username, password);
			System.out.println("Connection created successfully!!");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void closeConnection() {
		try {
			if(con != null) {
				con.close();
				System.out.println("Connection closed successfully!!");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void assertNum(int x) throws Exception {
		if(x <= 0) {
			throw new Exception("Negative number provided!!");
		}
	}
	
	private static void resetTablesSequences() {
		System.out.println("Resetting Tables");
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(DROP_TABLES);
			stmt.executeUpdate(CREATE_PERSON);
			stmt.executeUpdate(CREATE_CATEGORY);
			stmt.executeUpdate(CREATE_PRODUCT);
			stmt.executeUpdate(CREATE_SHOPPING_CART);
			stmt.executeUpdate(CREATE_PRODUCT_IN_CART);
			
			System.out.println("Tables reset done");
		} catch(Exception e) {
			System.out.println("Tables reset failed!!");
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if(stmt != null) {
					stmt.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void insertCustomers() {
		System.out.println("Inserting Customers");
		PreparedStatement ptst = null;
		try {
			int noOfRows = 0;
			int stateId=1;
			ptst = con.prepareStatement(INSERT_CUSTOMER);
			while(noOfRows < noOfCustomers) {
				ptst.setString(1, "CUST_"+noOfRows);
				stateId = rand.nextInt(56)+1;
				ptst.setInt(2, stateId);
				ptst.addBatch();
				noOfRows++;
				
				if(noOfRows % batchSize == 0) {
					ptst.executeBatch();
				}
				
			}
			ptst.executeBatch();
			System.out.println(noOfCustomers + " customers inserted successfully");
		} catch(Exception e) {
			System.out.println("Insert Customers failed!!");
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if(ptst != null) {
					ptst.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void insertCategories() {
		System.out.println("Inserting Categories");
		PreparedStatement ptst = null;
		try {
			int noOfRows = 0;
			ptst = con.prepareStatement(INSERT_CATEGORY);
			while(noOfRows < noOfCategories) {
				ptst.setString(1, "CAT_"+noOfRows);
				ptst.setString(2, "CAT_DESCRIPTION_"+noOfRows);
				ptst.addBatch();
				noOfRows++;
				
				if(noOfRows % batchSize == 0) {
					ptst.executeBatch();
				}
				
			}
			ptst.executeBatch();
			System.out.println(noOfCategories + " categories inserted successfully");
		} catch(Exception e) {
			System.out.println("Insert Categories failed!!");
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if(ptst != null) {
					ptst.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void insertProducts() {
		System.out.println("Inserting Products");
		PreparedStatement ptst = null;
		try {
			int noOfRows = 0;
			int categoryId = 0;
			int price = 0;
			productPrices = new HashMap<Integer, Integer>();
			ptst = con.prepareStatement(INSERT_PRODUCT);
			while(noOfRows < noOfProducts) {
				ptst.setString(1, "SKU_"+noOfRows);
				ptst.setString(2, "PROD_"+noOfRows);
				price = (rand.nextInt(100)+1)*10;
				ptst.setInt(3, price);
				categoryId = rand.nextInt(noOfCategories)+1;
				ptst.setInt(4, categoryId);
				
				ptst.addBatch();
				
				productPrices.put(noOfRows+1, price);
				noOfRows++;
				
				if(noOfRows % batchSize == 0) {
					ptst.executeBatch();
				}
				
			}
			ptst.executeBatch();
			System.out.println(noOfProducts + " products inserted successfully");
		} catch(Exception e) {
			System.out.println("Insert Products failed!!");
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if(ptst != null) {
					ptst.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void insertSales() {
		System.out.println("Inserting Sales");
		PreparedStatement ptst = null;
		batchSize = 10000;
		try {
			int noOfRows = 0;
			int personId = 0;
			ptst = con.prepareStatement(INSERT_SHOPPING_CART);
			while(noOfRows < noOfSales) {
				personId = rand.nextInt(noOfCustomers)+1;
				ptst.setInt(1, personId);
				ptst.setBoolean(2, true);
				ptst.setString(3, "Data_Generator purchased item");
				
				ptst.addBatch();
				noOfRows++;
				
				if(noOfRows % batchSize == 0) {
					ptst.executeBatch();
				}
				
			}
			ptst.executeBatch();
			ptst.close();
			
			noOfRows = 1;
			int totalRows = 0;
			//products_in_cart(cart_id, product_id, price, quantity)
			ptst = con.prepareStatement(INSERT_PRODUCTS_IN_CART);
			while(noOfRows <= noOfSales) {
				int noOfProductsInCart = rand.nextInt(10)+1;
				int products = 0;
				int productId = 1;
				int quantity = 1;
				int productPrice = 1;
				while(products < noOfProductsInCart) {
					ptst.setInt(1, noOfRows);
					productId = rand.nextInt(noOfProducts)+1;
					ptst.setInt(2, productId);
					productPrice = productPrices.get(productId);
					ptst.setInt(3, productPrice);
					quantity = rand.nextInt(100)+1;
					ptst.setInt(4, quantity);
					
					ptst.addBatch();
					products++;
					totalRows++;
					
					if(totalRows % batchSize == 0) {
						ptst.executeBatch();
					}
				}
				ptst.executeBatch();
				noOfRows++;
			}
			
			System.out.println(noOfSales + " sales inserted successfully");
		} catch(Exception e) {
			System.out.println("Insert Sales failed!!");
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if(ptst != null) {
					ptst.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		Scanner s = null;
		try {
			s = new Scanner(System.in);
			
			System.out.println("DB Connection details");
			System.out.println("Provide the DB Host : ");
			String host = s.nextLine();
			
			System.out.println("Provide the DB Port : ");
			String port = s.nextLine();
			
			System.out.println("Provide the DB Name : ");
			String sid = s.nextLine();
			
			System.out.println("Provide the Username : ");
			String username = s.nextLine();
			
			System.out.println("Provide the Password : ");
			String password = s.nextLine();
			
			createConnection(host, port, sid, username, password);
			System.out.println("");
			try{
				System.out.println("Provide Data Generator Inputs");
				System.out.println("Provide the number of Customers to be created : ");
				noOfCustomers = s.nextInt();
				assertNum(noOfCustomers);
				
				System.out.println("Provide the number of Categories to be created : ");
				noOfCategories = s.nextInt();
				assertNum(noOfCategories);
				
				System.out.println("Provide the number of Products to be created : ");
				noOfProducts = s.nextInt();
				assertNum(noOfProducts);
				
				System.out.println("Provide the number of Sales to be created : ");
				noOfSales = s.nextInt();
				assertNum(noOfSales);
			} catch(Exception e) {
				System.out.println("Invalid input!!");
				e.printStackTrace();
			}
			
			con.setAutoCommit(false);
			
			resetTablesSequences();
			con.commit();
			
			insertCustomers();
			insertCategories();
			con.commit();
			
			insertProducts();
			con.commit();
			
			insertSales();
			con.commit();
			
		} catch(Exception e) {
			e.printStackTrace();
			try {
				if(con != null) {
					con.rollback();
				}
			} catch(Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			closeConnection();
		}
	}
}
