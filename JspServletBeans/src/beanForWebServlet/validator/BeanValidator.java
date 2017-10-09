package beanForWebServlet.validator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

import beanForWebServlet.BeanConnector;
import beanForWebServlet.exception.GetterMethodException;

public class BeanValidator extends BeanConnector {

	protected BeanValidator(String beanName) throws ClassNotFoundException {
		super(beanName);
	}

	public static List<String> validate(Object bean)
			throws ClassNotFoundException, GetterMethodException {

		Map<String,String> errorMap = new TreeMap<String,String>();

		BeanValidator validator = new BeanValidator(bean.getClass().getName());

		List<Field> fieldList = validator.getFieldList();

		for (Field field : fieldList) {
			check(validator, bean, field.getName(), errorMap,
					ValidateOrder.FIRST);

			if (errorMap.get(field.getName())==null){
				check(validator, bean, field.getName(), errorMap,
						ValidateOrder.SECOND);
			}
			if (errorMap.get(field.getName())==null){
				check(validator, bean, field.getName(), errorMap,
						ValidateOrder.THIRD);
			}
			System.out.println("errorListSize:"+errorMap.size());
		}
		if (errorMap.isEmpty())
			return new ArrayList<String>();
			List<String> resultList=new ArrayList<String>();
			resultList.addAll(errorMap.values());
			return resultList;
	}

	private static String checkNotNull(BeanConnector connector, Object bean,
			String notNullField) throws GetterMethodException {
		String input = (String) connector.getObjectByGetter(notNullField, bean);
		if (input == null || input.isEmpty()) {
			NotNull fieldNotNull = (NotNull) connector.getAnnotationOfField(
					notNullField, NotNull.class);
			return fieldNotNull.msg();
		}

		return null;
	}

	private static String checkLength(BeanConnector connector, Object bean,
			String lengthField) throws GetterMethodException {
		String input = (String) connector.getObjectByGetter(lengthField, bean);
		Length fieldLength = (Length) connector.getAnnotationOfField(
				lengthField, Length.class);
		int minLen = fieldLength.minLength();
		int maxLen = fieldLength.maxLength();
		if (input == null || input.length() < minLen || input.length() > maxLen) {
			return fieldLength.msg();
		}

		return null;
	}

	private static String checkPattern(BeanConnector connector, Object bean,
			String patternField) throws GetterMethodException {

		String input = (String) connector.getObjectByGetter(patternField, bean);
		Pattern fieldPattern = (Pattern) connector.getAnnotationOfField(
				patternField, Pattern.class);

		java.util.regex.Pattern regexPattern = java.util.regex.Pattern
				.compile(fieldPattern.pattern());
		if (input == null)
			return fieldPattern.msg();

			Matcher matcher = regexPattern.matcher(input);

			if (!matcher.matches()) {
				return fieldPattern.msg();
			}
		return null;
	}

	private static void check(BeanValidator validator, Object bean,
			String field, Map<String,String> errorList, ValidateOrder order)
			throws GetterMethodException {
		NotNull fieldNotNull = (NotNull) validator.getAnnotationOfField(field,
				NotNull.class);
		Length fieldLength = (Length) validator.getAnnotationOfField(field,
				Length.class);
		Pattern fieldPattern = (Pattern) validator.getAnnotationOfField(field,
				Pattern.class);
		String notNullError, lengthError, patternError;

		if (fieldNotNull != null && fieldNotNull.order() == order) {
			notNullError = checkNotNull(validator, bean, field);
			if (notNullError != null) {
				errorList.put(field,notNullError);
			}
		}
		if (fieldLength != null && fieldLength.order() == order) {
			lengthError = checkLength(validator, bean, field);
			if (lengthError != null) {
				errorList.put(field,lengthError);
			}
		}
		if (fieldPattern != null && fieldPattern.order() == order) {
			patternError = checkPattern(validator, bean, field);
			if (patternError != null) {
				errorList.put(field,patternError);
			}
		}

	}

}
