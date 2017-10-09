package beanForWebServlet.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DataBaseAccessor {

	private static DataBaseAccessor dataBaseAccessor;

	private static String dataSourceName;
	private static String driverManagerName;
	private static String dmUser;
	private static String dmPassword;
	public static void setDataSource(String dataSource){
		dataSourceName=dataSource;
	}
	public static void setDriverManager(String driverManager,String user,String password){
		driverManagerName=driverManager;
		dmUser=user;
		dmPassword=password;
	}
	
	public static void init() {
		dataBaseAccessor = new DataBaseAccessor();
	}

	public static Connection getConnectionByDS()
			throws NamingException, SQLException {
		InitialContext context = new InitialContext();
		DataSource ds = (DataSource) context.lookup(dataSourceName);
		return ds.getConnection();
	}
	
	public static Connection getConnectionByDM() throws SQLException{
		return DriverManager.getConnection(driverManagerName,dmUser,dmPassword);
	}


}
