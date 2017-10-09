package beanForWebServlet.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import beanForWebServlet.BeanConnector;
import beanForWebServlet.dao.annotations.Table;
import beanForWebServlet.exception.GetterMethodException;
import beanForWebServlet.exception.InvalidUseException;
import beanForWebServlet.exception.NoSupportFieldException;
import beanForWebServlet.exception.SetterMethodException;

public class BeanAdapter extends BeanConnector {

	private String tableName;

	BeanAdapter(String beanName, String tableName)
			throws ClassNotFoundException {
		super(beanName);
		if(tableName==null){
			Table table=(Table) this.getAnnotationValueOfBean(Table.class);
			if(table!=null){
				tableName=table.tableName();
			}
		}


		this.tableName = tableName;
	}

	public static BeanAdapter getAdapter(String beanName, String tableName)
			throws ClassNotFoundException {
		return new BeanAdapter(beanName, tableName);
	}

	public void setTableName(String name) {
		this.tableName = name;
	}

	public String getTableName() {
		return tableName;
	}

	public int insert(Connection con, Object bean)
			throws GetterMethodException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, SetterMethodException, InvalidUseException, NoSupportFieldException {
		return BeanDAO.insert(con, this, bean);
	}

	public int insert(Object bean) throws SQLException, NamingException,
			GetterMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, SetterMethodException, InvalidUseException, NoSupportFieldException {
		int result = 0;
		try (Connection con = DataBaseAccessor.getConnectionByDS()) {
			result = insert(con, bean);
		}
		return result;
	}

	public int update(Connection con, Object bean, String rowName)
			throws SQLException, GetterMethodException, ClassNotFoundException {
		return BeanDAO.updateByRow(con, this, bean, rowName);
	}

	public int update(Object bean, String rowName) throws SQLException,
			NamingException, GetterMethodException, ClassNotFoundException {
		int result = 0;
		try (Connection con = DataBaseAccessor.getConnectionByDS()) {
			result = update(con, bean, rowName);
		}
		return result;
	}

	public int deleteByRow(Connection con, Object bean, String rowName)
			throws SQLException, GetterMethodException, ClassNotFoundException {
		return BeanDAO.deleteByRow(con, this, bean, rowName);
	}

	public List selectValueByRow(String rowName, String rowValue)
			throws InstantiationException, IllegalAccessException,
			SQLException, SetterMethodException, NamingException {
		return BeanDAO.selectValuesByRow(DataBaseAccessor.getConnectionByDS(),
				this, rowName, rowValue);
	}

	public List selectValuesByRow(Connection con, String rowName,
			String rowValue) throws InstantiationException,
			IllegalAccessException, SQLException, SetterMethodException {
		return BeanDAO.selectValuesByRow(con, this, rowName, rowValue);
	}

	public Object selectByRow(String rowName, String rowValue)
			throws SQLException, NamingException, InstantiationException,
			IllegalAccessException, SetterMethodException {
		Object bean = null;
		try (Connection con = DataBaseAccessor.getConnectionByDS()) {
			bean = selectByRow(con, rowName, rowValue);
		}
		return bean;
	}

	public Object selectByRow(Connection con, String rowName, String rowValue)
			throws InstantiationException, IllegalAccessException,
			SQLException, SetterMethodException {
		return BeanDAO.selectByRow(con, this, rowName, rowValue);
	}


	public List<Object> executeQuery(String sql, List<String> queryList)
			throws InstantiationException, IllegalAccessException,
			SQLException, SetterMethodException, NamingException {

		return BeanDAO.executeQuery(DataBaseAccessor.getConnectionByDS(), this,
				sql, queryList);

	}

	public List<Object> executeSelectedQuery(String sql, List<String> queryList,List<String> selectedList)
			throws InstantiationException, IllegalAccessException,
			SQLException, SetterMethodException, NamingException {

		return BeanDAO.executeSelectedQuery(DataBaseAccessor.getConnectionByDS(), this,
				sql, queryList,selectedList);

	}

	public Object selectMaxValue(String rowName) throws InstantiationException, IllegalAccessException, SQLException, SetterMethodException, NamingException {
		String sql="SELECT MAX(?) AS \""+rowName+"\" FROM "+tableName;
		List<String> queryList=new ArrayList<String>();
		queryList.add(rowName);
		List result =executeSelectedQuery(sql,queryList,queryList);

		if(result!=null &&result.isEmpty()){
			return result.get(0);

		}
		return null;

	}
}
