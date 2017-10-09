package beanForWebServlet.presentation;

import java.lang.reflect.Field;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import beanForWebServlet.validator.BeanValidator;
import beanForWebServlet.BeanConnector;
import beanForWebServlet.exception.GetterMethodException;
import beanForWebServlet.exception.SetterMethodException;


public class FormSetter extends BeanConnector{

	protected FormSetter(String beanName) throws ClassNotFoundException {
		super(beanName);
	}

	public static void setFormFromRequest(HttpServletRequest request,Object targetForm) throws ClassNotFoundException, SetterMethodException{
		FormSetter setter =new FormSetter(targetForm.getClass().getName());
		List<Field> fieldList=setter.getFieldList();
		for(Field field:fieldList){
			setter.setObjectBySetter(field.getName(), targetForm, request.getParameter(field.getName()));
		}
	}

	public static List<String> setFormWithValidation(HttpServletRequest request,Object targetForm) throws ClassNotFoundException, SetterMethodException, GetterMethodException{
		setFormFromRequest(request,targetForm);
		List<String> errorList=BeanValidator.validate(targetForm);
		if(errorList!=null&&!errorList.isEmpty()){
			if(!targetForm.getClass().getName().contains("."))
				return null;
			String[] formName=targetForm.getClass().getName().split("\\.");

			request.setAttribute(formName[formName.length-1], targetForm);
		}

		return errorList;

	}
}
