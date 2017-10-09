package beanForWebServlet.dao;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import beanForWebServlet.BeanConnector;
import beanForWebServlet.dao.annotations.AutoIncrement;
import beanForWebServlet.dao.annotations.Join;
import beanForWebServlet.dao.annotations.OrderBy;
import beanForWebServlet.dao.annotations.PrimaryKey;
import beanForWebServlet.dao.annotations.Sysdate;
import beanForWebServlet.exception.GetterMethodException;
import beanForWebServlet.exception.InvalidUseException;
import beanForWebServlet.exception.NoSupportFieldException;
import beanForWebServlet.exception.SetterMethodException;

/**
 *
 * JavaBeans（以下、bean）を中心としたデータベースアクセスを行うためのクラスです。
 * beanとを以下のルールに従って作成する事でDBへのアクセスをサポートしレコード（以下、情報）の 取得、追加、更新、削除機能を提供します。
 *
 * <pre>
 * {@code  前提：
 * DBのテーブル名はある一般名詞の複数形、もしくは一般名詞をアンダースコアで繋げ、末尾の単語を複数形としたもの。
 * <例> RECORDS,RECORD_DETAILS,QUIZZES
 *
 * beanのルール
 *  1.クラスの命名：テーブル名の末尾の単語を単数形とし、末尾に"Entity"を付与する。
 *   <例>RecordEntity,Record_detailEntity,QuizEntity
 *   ※テーブル名とクラス名はsetAdapterを用いればこのルールに限らずクラスとテーブルを紐付けることが出来ます。
 *
 *  2.属性の命名：テーブルの各列に一致させます。また、属性の数は列の数と一致していなければなりません。
 *  3.getter,setterの命名：一般的なbeanの命名規則に従います。
 *
 * }
 * </pre>
 *
 * @author 和也
 *
 */
public class BeanDAO {
	static final String STRING = "java.lang.String";
	static final String INT = "int";
	static final String DOUBLE = "double";
	static final String FLOAT = "float";
	static final String BIG_DECIMAL = "java.math.BigDecimal";
	static final String TIME_STAMP = "java.sql.Timestamp";
	static final String DATE = "java.sql.Date";
	static final String ENTITY = "Entity";

	private static Map<String, BeanAdapter> adapterMap;

	/**
	 * 内部メソッド。指定されたbeanのDBアダプターを生成します。 bean名から推測された複数形のテーブルと紐づける。
	 *
	 * @param beanName
	 *            bean名（フルパス）を指定します。
	 * @return 生成したアダプターを返します。
	 * @throws ClassNotFoundException
	 */
	private static BeanAdapter generateAdapter(String beanName)
			throws ClassNotFoundException {
		if (!beanName.contains(".")) {
			return null;
		}
		String tableName = getTableName(beanName);
		return new BeanAdapter(beanName, tableName);

	}

	private static String getTableName(String beanName) {
		String[] beanNameSplit = beanName.split("\\.");
		// System.out.println(beanName + ":" + beanNameSplit.length);
		String className = beanNameSplit[beanNameSplit.length - 1];

		String tableName = className;
		if (tableName.contains(ENTITY)) {

			tableName = tableName.split(ENTITY)[0];
			// 複数系のルールに従ってワードを付け替える。
			PluralFormMap pfMap = PluralFormMap.getInstance();
			String specialTableName = pfMap.getPlural(tableName.toLowerCase());
			// System.out.println(tableName+","+specialTableName);
			if (specialTableName == null) {
				switch (tableName.charAt(tableName.length() - 1)) {

				case 'h':
					switch (tableName.length() - 2) {
					case 'c':
					case 's':
						tableName += "es";
						break;
					default:
						tableName += "s";
					}
					break;
				case 's':
				case 'x':
					tableName += "es";
					break;
				case 'y':
					switch (tableName.length() - 2) {
					case 'a':
					case 'i':
					case 'u':
					case 'e':
					case 'o':
						tableName += "s";
						break;
					default:
						tableName = tableName.substring(0,
								tableName.length() - 1) + "ies";
					}
					break;
				default:
					tableName += "s";

				}
			} else {
				tableName = specialTableName;
			}

		} else {
			tableName = null;
		}
		return tableName;
	}

	/**
	 * beanとテーブルの独自紐付けを行ったアダプターを作成する。 作成したアダプターはstatic領域にkeyNameに紐づけて保存される。
	 * 保存されたアダプターはgetAdapterによって取得できる。
	 *
	 * @param keyName
	 *            getAdapter時のkeyとなります。保存されたアダプターはこのkeyNameで取得可能です。
	 * @param beanAdapterName
	 *            テーブルと接続するbeanの名前をフルパスで指定します。
	 * @param tableName
	 *            beanと接続するテーブル名を指定します。
	 * @throws ClassNotFoundException
	 */
	public static void setAdapter(Object bean, String tableName)
			throws ClassNotFoundException {
		BeanAdapter beanAdapter = BeanAdapter.getAdapter(bean.getClass()
				.getName(), tableName);
		if (adapterMap == null) {
			adapterMap = new HashMap<String, BeanAdapter>();
		}
		adapterMap.put(bean.getClass().getName(), beanAdapter);
	}

	/**
	 * setAdapterでセットしたアダプターを取得します。
	 *
	 * @param keyName
	 *            setAdapterで指定したkeyNameを用いて保存したアダプターを取得します。
	 * @return 保存されたアダプター
	 */
	public static BeanAdapter getAdapter(String keyName) {
		if (adapterMap == null) {
			return null;
		}

		return adapterMap.get(keyName);
	}

	/**
	 * 指定したbeanをそのbeanに紐づくテーブルに追加します。 setAdapterでテーブルとの紐づけを行っていた場合、その紐づけが優先されます。
	 *
	 * @param con
	 *            DBへのコネクション
	 * @param bean
	 *            テーブルに追加する情報が入ったbean
	 * @return
	 * @throws ClassNotFoundException
	 * @throws GetterMethodException
	 * @throws SQLException
	 * @throws NoSupportFieldException
	 * @throws InvalidUseException
	 * @throws SetterMethodException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static int insertBean(Connection con, Object bean)
			throws ClassNotFoundException, GetterMethodException, SQLException,
			InstantiationException, IllegalAccessException,
			SetterMethodException, InvalidUseException, NoSupportFieldException {
		BeanAdapter adapter = getAdapter(bean.getClass().getName());

		if (adapter == null)
			return insert(con, generateAdapter(bean.getClass().getName()), bean);
		else
			return insert(con, adapter, bean);
	}

	/**
	 * 指定したbeanに紐づくテーブルをそのbean情報で更新します。
	 * setAdapterでテーブルとの紐づけを行っていた場合、その紐づけが優先されます。 更新対象となる列名をrowNameで指定でき、
	 * rowNameと一致するbean内の属性に設定された値と一致する行が更新されます。
	 *
	 * @param con
	 *            DBへのコネクション
	 * @param bean
	 *            テーブルを更新するための情報が入ったbean
	 * @param rowName
	 *            更新対象の行を特定するためにbean内で使用する属性名（列名）
	 *
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws GetterMethodException
	 */
	public static int updateBean(Connection con, Object bean, String rowName)
			throws ClassNotFoundException, SQLException, GetterMethodException {
		BeanAdapter adapter = getAdapter(bean.getClass().getName());

		if (adapter == null)
			return updateByRow(con, generateAdapter(bean.getClass().getName()),
					bean, rowName);
		else
			return updateByRow(con, adapter, bean, rowName);
	}

	/**
	 *
	 * @param con
	 *            DBへのコネクション
	 * @param bean
	 *            テーブルを更新するための情報が入ったbean
	 * @param rowList
	 *            更新対象の行を特定するためにbean内で使用する属性名（列名）のリスト。
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws GetterMethodException
	 */
	public static int updateBeanByRows(Connection con, Object bean,
			List<String> rowList) throws ClassNotFoundException, SQLException,
			GetterMethodException {
		BeanAdapter adapter = getAdapter(bean.getClass().getName());

		if (adapter == null)
			return updateByRows(con,
					generateAdapter(bean.getClass().getName()), bean, rowList);
		else
			return updateByRows(con, adapter, bean, rowList);
	}

	/**
	 * 指定したbeanに紐づくテーブルを削除します。 setAdapterでテーブルとの紐づけを行っていた場合、その紐づけが優先されます。
	 * 削除対象となる列名をrowNameで指定でき、 rowNameと一致するbean内の属性に設定された値と一致する行が削除されます。
	 *
	 * @param con
	 *            DBへのコネクション
	 * @param bean
	 *            テーブルを削除するための情報が入ったbean
	 * @param rowName
	 *            削除対象の行を特定するためにbean内で使用する属性名（列名）
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws GetterMethodException
	 */
	public static int deleteBean(Connection con, Object bean, String rowName)
			throws ClassNotFoundException, SQLException, GetterMethodException {
		BeanAdapter adapter = getAdapter(bean.getClass().getName());

		if (adapter == null)
			return deleteByRow(con, generateAdapter(bean.getClass().getName()),
					bean, rowName);
		else
			return deleteByRow(con, adapter, bean, rowName);
	}

	/**
	 * 指定したbeanに紐づくテーブルから情報を取得します。 setAdapterでテーブルとの紐づけを行っていた場合、その紐づけが優先されます。
	 * 取得対象となる条件値をrowNameで指定でき、 rowNameと一致するbean内の属性に設定された値と一致する行の全てが取得されます。
	 *
	 * @param con
	 *            DBへのコネクション
	 * @param bean
	 *            テーブルから情報を取得するためのbean
	 * @param rowName
	 *            取得対象の行を特定するためにbean内で使用する属性名（列名）
	 * @return 条件に合致するテーブル内の情報（beanのリスト形式）
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 * @throws SetterMethodException
	 * @throws GetterMethodException
	 * @throws ClassNotFoundException
	 */

	@SuppressWarnings("rawtypes")
	public static List selectBeanListByRow(Connection con, Object bean,
			String rowName) throws InstantiationException,
			IllegalAccessException, SQLException, SetterMethodException,
			GetterMethodException, ClassNotFoundException {

		BeanAdapter adapter = getAdapter(bean.getClass().getName());
		if (adapter == null)
			adapter = generateAdapter(bean.getClass().getName());
		return selectValuesByRow(
				con,
				adapter,
				rowName,
				enString(adapter.getObjectByGetter(rowName, bean),
						adapter.getField(rowName)));
	}

	/**
	 * 指定したbeanに紐づくテーブルに登録されている全ての情報を取得します。
	 * setAdapterでテーブルとの紐づけを行っていた場合、その紐づけが優先されます。
	 *
	 * @param con
	 *            DBへのコネクション
	 * @param bean
	 *            テーブルから情報を取得するためのbean
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 * @throws SetterMethodException
	 * @throws GetterMethodException
	 * @throws ClassNotFoundException
	 */
	public static List selectBeanList(Connection con, Object bean)
			throws InstantiationException, IllegalAccessException,
			SQLException, SetterMethodException, GetterMethodException,
			ClassNotFoundException {

		BeanAdapter adapter = getAdapter(bean.getClass().getName());
		if (adapter == null)
			adapter = generateAdapter(bean.getClass().getName());
		return selectAllValue(con, adapter);
	}

	/**
	 * 指定したbeanに紐づくテーブルから情報を取得します。 setAdapterでテーブルとの紐づけを行っていた場合、その紐づけが優先されます。
	 * 取得対象となる条件値をrowNameで指定でき、
	 * rowNameと一致するbean内の属性に設定された値と一致する行の先頭から1番目が取得されます。
	 *
	 * @param con
	 *            DBへのコネクション
	 * @param bean
	 *            テーブルから情報を取得するためのbean
	 * @param rowName
	 *            取得対象の行を特定するためにbean内で使用する属性名（列名）
	 * @return 条件に合致するテーブル内の情報（bean形式）
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws SetterMethodException
	 * @throws GetterMethodException
	 */
	@SuppressWarnings("rawtypes")
	public static Object selectBeanByRow(Connection con, Object bean,
			String rowName) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException,
			SetterMethodException, GetterMethodException {

		List list = selectBeanListByRow(con, bean, rowName);
		if (list != null && !list.isEmpty())
			return list.get(0);
		return null;

	}

	/**
	 * 指定したbeanに紐づくテーブルの特定列の最大値を取得します。
	 * setAdapterでテーブルとの紐づけを行っていた場合、その紐づけが優先されます。
	 * 取得対象となる列をrowNameで指定し、その最大値を取得します。
	 *
	 * @param con
	 *            DBへのコネクション
	 * @param bean
	 *            テーブルから情報を取得するためのbean
	 * @param rowName
	 *            最大値取得対象を特定するためにbean内で使用する属性名（列名）
	 * @return 条件に合致するテーブル内の情報の最大値
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 * @throws SetterMethodException
	 * @throws NamingException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object selectMaxValue(Connection con, Object bean,
			String rowName) throws InstantiationException,
			IllegalAccessException, SQLException, SetterMethodException,
			NamingException, ClassNotFoundException {
		BeanAdapter adapter = generateAdapter(bean.getClass().getName());
		String sql = "SELECT MAX(" + rowName + ") AS " + rowName + " FROM "
				+ adapter.getTableName();
		List<String> queryList = new ArrayList<String>();
		queryList.add(rowName);
		// System.out.println("rowName:" + rowName);
		List result = executeSelectedQuery(con, adapter, sql, new ArrayList(),
				queryList);

		if (result != null && !result.isEmpty()) {
			return result.get(0);

		}
		return null;

	}

	/**
	 * 指定したbean内でAutoIncrementアノテーションが付与されている属性を探し、その値を1つ進めます。
	 * 値を進める時に基準となる値は現在DB内で該当する列に保存されている最大値です。
	 * AutoIncrement対象が複合主キーの一部の場合、他の主キー部についてはbeanを絞り込み条件として利用します。
	 *
	 * @param con
	 * @param bean
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InvalidUseException
	 * @throws GetterMethodException
	 * @throws NoSupportFieldException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 * @throws SetterMethodException
	 */
	public static Object autoIncrement(Connection con, Object bean)
			throws ClassNotFoundException, InvalidUseException,
			GetterMethodException, NoSupportFieldException,
			InstantiationException, IllegalAccessException, SQLException,
			SetterMethodException {

		BeanAdapter adapter = getAdapter(bean.getClass().getName());
		if (adapter == null)
			adapter = generateAdapter(bean.getClass().getName());

		List<Field> allField = adapter.getFieldList();
		Field incrementField = null;
		List<Field> primaryFields = new ArrayList<Field>();
		for (Field field : allField) {
			PrimaryKey pk = (PrimaryKey) adapter.getAnnotationOfField(
					field.getName(), PrimaryKey.class);
			AutoIncrement increment = (AutoIncrement) adapter
					.getAnnotationOfField(field.getName(), AutoIncrement.class);

			if (increment != null) {
				if (incrementField != null)
					throw new InvalidUseException(
							"AutoIncrementは1つのentityで1つまでです。");
				incrementField = field;
				if (pk == null) {
					primaryFields = null;
					break;
				}
			} else if (pk != null) {
				primaryFields.add(field);
			}
		}
		List<String> queryList = new ArrayList<String>();
		List<String> selectedRowList = new ArrayList<String>();

		String sql = "SELECT MAX(\"" + incrementField.getName() + "\") AS \""
				+ incrementField.getName() + "\" FROM "
				+ adapter.getTableName();
		selectedRowList.add(incrementField.getName());

		if (primaryFields != null && !primaryFields.isEmpty()) {
			sql += " WHERE ";
			for (int i = 0; i < primaryFields.size(); i++) {
				Field pkField = primaryFields.get(i);
				sql += "\"" + pkField.getName() + "\"=" + "?";
				queryList.add(enString(
						adapter.getObjectByGetter(pkField.getName(), bean),
						pkField));

				if (i < primaryFields.size() - 1) {
					sql += " AND ";
				}

			}
		}

		List<Object> beanList = BeanDAO.executeSelectedQuery(con, bean, sql,
				queryList, selectedRowList);
		if (beanList == null || beanList.isEmpty()) {
			return getInitVal(0, incrementField);
		} else {
			return getNextVal(adapter.getObjectByGetter(
					incrementField.getName(), beanList.get(0)), incrementField);
		}

	}

	private static Object getInitVal(int i, Field field)
			throws NoSupportFieldException {
		switch (field.getType().getName()) {
		case INT:
		case DOUBLE:
		case FLOAT:
			return 1;

		case BIG_DECIMAL:
			return new BigDecimal(1);

		default:
			throw new NoSupportFieldException("指定された属性の型はサポートされません。");
		}
	}

	private static Object getNextVal(Object obj, Field field)
			throws NoSupportFieldException {
		switch (field.getType().getName()) {
		case INT:
		case DOUBLE:
		case FLOAT:
			return (int) obj + 1;

		case BIG_DECIMAL:
			return ((BigDecimal) obj).add(new BigDecimal(1));

		default:
			throw new NoSupportFieldException("指定された属性の型はサポートされません。");
		}
	}

	/**
	 *
	 * @param fieldValue
	 * @return
	 */
	private static String enString(Object value, Field field) {

		String ffType = field.getType().getName();
		switch (ffType) {
		case STRING:
			return (String) value;
		case INT:
			return String.valueOf((int) value);
		case DOUBLE:
			return String.valueOf((double) value);
		case FLOAT:
			return String.valueOf((float) value);
		case BIG_DECIMAL:
			return ((BigDecimal) value).toString();
		default:
			return String.valueOf(value);
		}
	}

	/**
	 * DBにアクセスし、beanに対応するテーブルの情報を取得します。
	 * sql文内のプレースメントホルダーはqueryList内のStringで置換されます。
	 *
	 * @param con
	 *            DBにアクセスするためのコネクションです。
	 * @param bean
	 *            アクセスしたいテーブルに紐づくbeanです。
	 *            テーブル名がある単語の単数形かつbean名が"テーブル名の単数形+Entity" である場合に、自動で紐づけが行われます。
	 * @param sql
	 *            SQL文です。プレースメントホルダー"?"を利用できます。
	 *            プレースメントホルダーを使用した場合、置換する文字列はqueryListに指定します。
	 * @param queryList
	 *            SQL文ないでプレースメントホルダーを使用する場合にこのリスト内の文字列で置換されます。
	 *            SQL文内の先頭とListの先頭が一致し、以下順番に置換されていきます。
	 *
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws SetterMethodException
	 */
	@SuppressWarnings("rawtypes")
	public static List executeQuery(Connection con, Object bean, String sql,
			List<String> queryList) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException,
			SetterMethodException {
		return executeQuery(con, generateAdapter(bean.getClass().getName()),
				sql, queryList);
	}

	/**
	 *
	 * @param con
	 * @param bean
	 * @param sql
	 * @param queryList
	 * @param selectedRow
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws SetterMethodException
	 */
	@SuppressWarnings("rawtypes")
	public static List executeSelectedQuery(Connection con, Object bean,
			String sql, List<String> queryList, List<String> selectedRow)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, SetterMethodException {
		return executeSelectedQuery(con, generateAdapter(bean.getClass()
				.getName()), sql, queryList, selectedRow);

	}

	/**
	 *
	 * @param con
	 * @param adapter
	 * @param obj
	 * @return
	 * @throws GetterMethodException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws NoSupportFieldException
	 * @throws InvalidUseException
	 * @throws SetterMethodException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	static int insert(Connection con, BeanAdapter adapter, Object obj)
			throws GetterMethodException, SQLException, ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			SetterMethodException, InvalidUseException, NoSupportFieldException {
		String sql = "INSERT INTO " + adapter.getTableName() + " ";
		String row = "(";
		String value = "(";
		List<Field> beanFieldList = adapter.getFieldList();
		List<String> beanFieldNameList = new ArrayList<String>();
		for (int i = 0; i < beanFieldList.size(); i++) {
			Field beanField = beanFieldList.get(i);
			row += "\"" + beanField.getName() + "\"";
			Sysdate sysdate = (Sysdate) adapter.getAnnotationOfField(
					beanField.getName(), Sysdate.class);
			if (sysdate != null)
				value += "SYSDATE";
			else {
				value += "?";
				beanFieldNameList.add(beanField.getName());

				AutoIncrement ai = (AutoIncrement) adapter
						.getAnnotationOfField(beanField.getName(),
								AutoIncrement.class);
				if (ai != null) {
					adapter.setObjectBySetter(beanField.getName(), obj,
							autoIncrement(con, obj));
				}
			}
			if (i < beanFieldList.size() - 1) {
				row += ",";
				value += ",";
			}
		}
		row += ")";
		value += ")";

		sql += row + " VALUES " + value;

		return executeUpdate(con, sql, beanFieldNameList, obj);
	}

	static int updateByRow(Connection con, BeanAdapter adapter, Object bean,
			String rowName) throws SQLException, GetterMethodException,
			ClassNotFoundException {
		List<String> rowNameList = new ArrayList<String>();
		rowNameList.add(rowName);
		return updateByRows(con, adapter, bean, rowNameList);
	}

	static int updateByRows(Connection con, BeanAdapter adapter, Object bean,
			List<String> rowNameList) throws SQLException,
			GetterMethodException, ClassNotFoundException {
		String sql = "UPDATE " + adapter.getTableName() + " SET ";
		List<Field> fieldList = adapter.getFieldList();
		List<String> fieldNameList = new ArrayList<String>();
		for (int i = 0; i < fieldList.size(); i++) {
			sql += "\"" + fieldList.get(i).getName() + "\" = ? ";
			if (i < fieldList.size() - 1)
				sql += " , ";
			fieldNameList.add(fieldList.get(i).getName());
		}
		if (rowNameList != null && !rowNameList.isEmpty()) {
			sql += " WHERE ";
			for (int i = 0; i < rowNameList.size(); i++) {
				sql += "\"" + rowNameList.get(i) + "\" = ? ";
				if (i < rowNameList.size() - 1)
					sql += " AND ";
				fieldNameList.add(rowNameList.get(i));
			}
		}
		// System.out.println(fieldNameList.size());
		return executeUpdate(con, sql, fieldNameList, bean);
	}

	static int deleteByRow(Connection con, BeanAdapter adapter, Object bean,
			String rowName) throws SQLException, GetterMethodException,
			ClassNotFoundException {
		List<String> beanFieldNameList = new ArrayList<String>();
		beanFieldNameList.add(rowName);

		return deleteByRows(con, adapter, bean, beanFieldNameList);
	}

	static int deleteByRows(Connection con, BeanAdapter adapter, Object bean,
			List<String> rowNameList) throws SQLException,
			GetterMethodException, ClassNotFoundException {
		String sql = "DELETE FROM " + adapter.getTableName() + " WHERE ";
		for (int i = 0; i < rowNameList.size(); i++) {
			sql += "\"" + rowNameList.get(i) + "\" = ?";
			if (i < rowNameList.size() - 1)
				sql += " AND ";
		}

		return executeUpdate(con, sql, rowNameList, bean);
	}

	static Object selectByRow(Connection con, BeanAdapter adapter,
			String rowName, String object) throws InstantiationException,
			IllegalAccessException, SQLException, SetterMethodException {
		List<Object> valueList = selectValuesByRow(con, adapter, rowName,
				object);
		if (valueList == null || valueList.isEmpty())
			return null;
		return valueList.get(0);
	}

	static List<Object> selectValuesByRow(Connection con, BeanAdapter adapter,
			String rowName, String object) throws SQLException,
			InstantiationException, IllegalAccessException,
			SetterMethodException {
		List<String> wherePhrase = new ArrayList<String>();

		wherePhrase.add("\"" + rowName + "\"= ?");
		List<String> queryList = new ArrayList<String>();
		queryList.add(object);
		return selectBeanList(con, adapter, wherePhrase, queryList);
	}

	static List<Object> selectBeanList(Connection con, BeanAdapter adapter,
			List<String> wherePhrase, List<String> queryList)
			throws SQLException, InstantiationException,
			IllegalAccessException, SetterMethodException {
		String sql = makeSelectWord(adapter, wherePhrase);

		return executeQuery(con, adapter, sql, queryList);
	}

	// TODO 作りかけ
	private static String makeSelectWord(BeanAdapter adapter,
			List<String> wherePhrase) {
		String sql = "SELECT ";
		String rows = "";
		Map<String, String> otherNameMap = new HashMap<String, String>();
		Map<String, String[]> onPhraseMap = new HashMap<String, String[]>();
		List<Field> fieldList = adapter.getFieldList();
		for (int i = 0; i < fieldList.size(); i++) {
			Join join = fieldList.get(i).getAnnotation(Join.class);
			if (join != null) {
				otherNameMap.put(join.otherTableName(), join.joinTable());
				onPhraseMap.put(join.otherTableName(), join.onPhrase());
				sql += join.otherTableName() + ".\"" + join.joinRow()
						+ "\" AS \"";
			} else {
				sql += adapter.getTableName() + ".\""
						+ fieldList.get(i).getName() + "\" AS \"";
			}
			sql += fieldList.get(i).getName() + "\" ";
			if (i < fieldList.size() - 1) {
				sql += ", ";
			}

		}
		sql += "FROM " + adapter.getTableName() + " ";
		if (!otherNameMap.isEmpty()) {
			Iterator<String> it = otherNameMap.keySet().iterator();
			while (it.hasNext()) {
				String otherName = it.next();
				sql += "JOIN " + otherNameMap.get(otherName) + " " + otherName
						+ " ";

				String[] onPhrases = onPhraseMap.get(otherName);
				sql += "ON ";
				for (int i = 0; i < onPhrases.length; i++) {
					sql += onPhrases[i] + " ";
					if (i < onPhrases.length - 1)
						sql += "AND ";
				}
			}
		}
		if (wherePhrase != null && !wherePhrase.isEmpty()) {
			sql += "WHERE ";
			for (int i = 0; i < wherePhrase.size(); i++) {
				sql += wherePhrase.get(i) + " ";
				if (i < wherePhrase.size() - 1) {
					sql += "AND ";
				}
			}

		}
		List<String> orderByList = adapter
				.getFieldWithAnnotaiont(OrderBy.class);
		if (orderByList != null && !orderByList.isEmpty()) {
			sql += "ORDER BY ";
			for (int i = 0; i < orderByList.size(); i++) {
				String orderBy = orderByList.get(i);
				sql += "\""+orderBy + "\" ";
				OrderBy odAnn = (OrderBy) adapter.getAnnotationOfField(orderBy,
						OrderBy.class);
				switch (odAnn.orderType()) {
				case ASC:
					sql+="ASC ";
					break;
				case DESC:
					sql+="DESC ";
				}
				if(i<orderByList.size()-1){
					sql+=", ";
				}
			}

		}

		return sql;

	}

	static List<Object> selectAllValue(Connection con, BeanAdapter adapter)
			throws SQLException, InstantiationException,
			IllegalAccessException, SetterMethodException {
		String sql = "SELECT * FROM " + adapter.getTableName();

		return executeQuery(con, adapter, sql, new ArrayList<String>());

	}

	/**
	 * SQL文を実行し、Adapterにセットされたbeanのリストを返す
	 *
	 * @param con
	 * @param adapter
	 * @param sql
	 * @param queryList
	 * @return
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SetterMethodException
	 */
	static List<Object> executeQuery(Connection con, BeanAdapter adapter,
			String sql, List<String> queryList) throws SQLException,
			InstantiationException, IllegalAccessException,
			SetterMethodException {
		System.out.println(sql);
		List<Object> beanList = null;
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			for (int i = 0; i < queryList.size(); i++) {
				ps.setString(i + 1, queryList.get(i));
			}
			beanList = getObjFromResultSet(ps.executeQuery(), adapter);
		}
		return beanList;
	}

	/**
	 * SQL文を実行し、Adapterにセットされたbeanのリストを返す。 ただし、引数のリストで指定したフィールドのみを実行。
	 *
	 * @param con
	 * @param adapter
	 * @param sql
	 * @param queryList
	 * @param selectedFieldList
	 * @param bean
	 * @return
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SetterMethodException
	 */
	static List<Object> executeSelectedQuery(Connection con,
			BeanAdapter adapter, String sql, List<String> queryList,
			List<String> selectedFieldList) throws SQLException,
			InstantiationException, IllegalAccessException,
			SetterMethodException {
		System.out.println(sql);
		List<Object> beanList = null;
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			for (int i = 0; i < queryList.size(); i++) {
				ps.setString(i + 1, queryList.get(i));
			}
			beanList = getSelectedObjFromResultSet(ps.executeQuery(), adapter,
					selectedFieldList);
		}
		return beanList;

	}

	public static int executeUpdate(Connection con, String sql,
			List<String> queryList, Object bean) throws SQLException,
			GetterMethodException, ClassNotFoundException {
		int count = 0;
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			BeanAdapter adapter = BeanDAO.generateAdapter(bean.getClass()
					.getName());

			for (int i = 0; i < queryList.size(); i++) {
				String queryName = queryList.get(i);
				// System.out.println(queryName);
				switch (adapter.getFieldTypeName(queryName)) {
				case STRING:
					ps.setString(i + 1,
							(String) adapter.getObjectByGetter(queryName, bean));
					break;
				case INT:
					ps.setInt(i + 1,
							(int) adapter.getObjectByGetter(queryName, bean));
					break;
				case DOUBLE:
					ps.setDouble(i + 1,
							(double) adapter.getObjectByGetter(queryName, bean));
					break;
				case FLOAT:
					ps.setFloat(i + 1,
							(float) adapter.getObjectByGetter(queryName, bean));
					break;
				case BIG_DECIMAL:
					ps.setBigDecimal(i + 1, (BigDecimal) adapter
							.getObjectByGetter(queryName, bean));
					break;
				case TIME_STAMP:
					ps.setTimestamp(i + 1, (Timestamp) adapter
							.getObjectByGetter(queryName, bean));
					break;
				case DATE:
					ps.setDate(i + 1,
							(Date) adapter.getObjectByGetter(queryName, bean));
					break;
				default:
					ps.setObject(i + 1,
							adapter.getObjectByGetter(queryName, bean));
				}
			}
			System.out.println(sql);
			count = ps.executeUpdate();
		}
		return count;
	}

	private static List<Object> getObjFromResultSet(ResultSet rs,
			BeanAdapter adapter) throws SQLException, InstantiationException,
			IllegalAccessException, SetterMethodException {
		List<Object> beanList = new ArrayList<Object>();
		List<Field> beanFieldList = adapter.getFieldList();
		while (rs.next()) {
			Object beanObj = adapter.getBean();
			for (Field field : beanFieldList) {
				getValueOfField(rs, adapter, beanObj, field);
			}
			beanList.add(beanObj);
		}

		return beanList;
	}

	/**
	 * TODO 引数に選択されたフィールドに対してのみResultSetからの値取り出しを行う。
	 *
	 * @param rs
	 * @param adapter
	 * @param selectedFieldList
	 * @return
	 * @throws SQLException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SetterMethodException
	 */
	private static List<Object> getSelectedObjFromResultSet(ResultSet rs,
			BeanAdapter adapter, List<String> selectedFieldList)
			throws SQLException, InstantiationException,
			IllegalAccessException, SetterMethodException {
		List<Object> beanList = new ArrayList<Object>();
		while (rs.next()) {
			Object beanObj = adapter.getBean();
			for (String fieldString : selectedFieldList) {
				Field selectedField = adapter.getField(fieldString);
				getValueOfField(rs, adapter, beanObj, selectedField);
			}
			beanList.add(beanObj);
		}

		return beanList;

	}

	private static void getValueOfField(ResultSet rs, BeanAdapter adapter,
			Object beanObj, Field field) throws SetterMethodException,
			SQLException {
		switch (field.getType().getName()) {
		case STRING:
			adapter.setObjectBySetter(field.getName(), beanObj,
					rs.getString(field.getName()));
			break;
		case INT:
			adapter.setObjectBySetter(field.getName(), beanObj,
					rs.getInt(field.getName()));
			break;
		case DOUBLE:
			adapter.setObjectBySetter(field.getName(), beanObj,
					rs.getDouble(field.getName()));
			break;
		case FLOAT:
			adapter.setObjectBySetter(field.getName(), beanObj,
					rs.getFloat(field.getName()));
			break;
		case BIG_DECIMAL:
			// System.out.print(field.getName() + ":");
			// System.out.println(rs.getObject(field.getName()));
			adapter.setObjectBySetter(field.getName(), beanObj,
					rs.getBigDecimal(field.getName()));
			break;
		case TIME_STAMP:
			adapter.setObjectBySetter(field.getName(), beanObj,
					rs.getTimestamp(field.getName()));
			break;
		case DATE:
			adapter.setObjectBySetter(field.getName(), beanObj,
					rs.getDate(field.getName()));
			break;
		}
	}

}
