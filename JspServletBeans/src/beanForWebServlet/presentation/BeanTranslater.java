package beanForWebServlet.presentation;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import beanForWebServlet.BeanConnector;
import beanForWebServlet.exception.GetterMethodException;
import beanForWebServlet.exception.NoSupportFieldException;
import beanForWebServlet.exception.SetterMethodException;

public class BeanTranslater {
	static final String STRING = "java.lang.String";
	static final String INT = "int";
	static final String DOUBLE = "double";
	static final String FLOAT = "float";
	static final String BIG_DECIMAL = "java.math.BigDecimal";
	static final String TIME_STAMP = "java.sql.Timestamp";
	static final String DATE = "java.sql.Date";
	static final String ENTITY = "Entity";


	/**
	 * formにセットされているString値を変換し、entityにセットします。
	 * 現在サポートしている型はString,int,double,float,BigDecimalです。
	 *
	 * @param form
	 * @param entity
	 * @throws NoSupportFieldException
	 * @throws ClassNotFoundException
	 * @throws GetterMethodException
	 * @throws SetterMethodException
	 */
	public static void translateFormToEntity(Object form, Object entity)
			throws NoSupportFieldException, ClassNotFoundException, GetterMethodException, SetterMethodException {
		BeanConnector formConnector = null, entityConnector = null;
		formConnector = BeanConnector.getConnector(form.getClass().getName());
		entityConnector = BeanConnector.getConnector(entity.getClass()
				.getName());

		List<Field> formField = formConnector.getFieldList();

		for (Field ff : formField) {
			String ffValue = (String) formConnector.getObjectByGetter(
					ff.getName(), form);

			String ffType = entityConnector.getFieldTypeName(ff.getName());
			switch (ffType) {
			case STRING:
				entityConnector
						.setObjectBySetter(ff.getName(), entity, ffValue);
				break;
			case INT:
				entityConnector.setObjectBySetter(ff.getName(), entity,
						Integer.parseInt(ffValue));
				break;
			case DOUBLE:
				entityConnector.setObjectBySetter(ff.getName(), entity,
						Double.parseDouble(ffValue));
				break;
			case FLOAT:
				entityConnector.setObjectBySetter(ff.getName(), entity,
						Float.parseFloat(ffValue));
				break;
			case BIG_DECIMAL:
				entityConnector.setObjectBySetter(ff.getName(), entity,
						new BigDecimal(ffValue));
				break;
			case TIME_STAMP:
				// TODO タイムスタンプ型に変換出来る機構を作る？
				throw new NoSupportFieldException(ff.getName()+"の型："+ffType+"はサポートされていません。");
				//break;
			case DATE:
				// TODO Date型に変換出来る機構を作る？
				throw new NoSupportFieldException(ff.getName()+"の型："+ffType+"はサポートされていません。");
				//break;
			default:
				throw new NoSupportFieldException(ff.getName()+"の型："+ffType+"はサポートされていません。");

			}
		}

	}
}
