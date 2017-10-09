package beanForWebServlet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import beanForWebServlet.exception.GetterMethodException;
import beanForWebServlet.exception.SetterMethodException;

/**
 * �Z�b�g����bean���f�[�^�x�[�X�Ɛڑ����邽�߂̃R�l�N�^�[
 *
 * @author �a��
 *
 */
public class BeanConnector {

	private static final String GET = "get";
	private static final String SET = "set";
	private static final int ONE = 1;
	private static final int ZERO = 0;

	private Class<?> bean;
	private Map<String,Field> fieldMap;


	protected BeanConnector(String beanName) throws ClassNotFoundException{
		bean =Class.forName(beanName);
		Field[] fields=bean.getDeclaredFields();
		fieldMap=new TreeMap<String,Field>();
		for(Field field:fields){
			fieldMap.put(field.getName(),field);
		}
	}



	public Object getBean() throws InstantiationException, IllegalAccessException{
		return bean.newInstance();
	}

	/**
	 * ���̃R�l�N�^�[�ɃZ�b�g���ꂽbean�̃t�B�[���h�����X�g�Ŏ擾���܂��B
	 * @return
	 */
	public List<Field> getFieldList(){
		List<Field> fieldList =new ArrayList<Field>();
		fieldList.addAll(fieldMap.values());
		return fieldList;
	}
	/**
	 * ���̃R�l�N�^�[�ɃZ�b�g���ꂽbean�̎w�肳�ꂽ�t�B�[���h���擾���܂��B
	 * @param fieldName
	 * @return
	 */
	public Field getField(String fieldName){
		return fieldMap.get(fieldName);
	}
	/**
	 * ���̃R�l�N�^�[�ɃZ�b�g���ꂽbean�̎w�肳�ꂽ�t�B�[���h�̌^�����擾���܂��B
	 * @param fieldName
	 * @return
	 */
	public String getFieldTypeName(String fieldName){
		return fieldMap.get(fieldName).getType().getName();
	}

	/**
	 * bean�̖��O���w�肵�ăR�l�N�^�[���擾���܂��B
	 * @param beanName
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static BeanConnector getConnector(String beanName) throws ClassNotFoundException{
		return new BeanConnector(beanName);
	}
	/**
	 * 指定されたアノテーションの着いたフィールドのリストを取得します。
	 * @param annotation
	 * @return
	 */
	public List<String> getFieldWithAnnotaiont(Class annotation){
		List<String> annotationFields =new ArrayList<String> ();

		List<Field> fieldList=this.getFieldList();

		for(Field field :fieldList){
			if(getAnnotationOfField(field.getName(), annotation)!=null){
				annotationFields.add(field.getName());
			}
		}
		if(annotationFields.isEmpty()){
			return null;
		}else
			return annotationFields;
	}
	/**
	 * このbeanに付いているアノテーションの内、指定されたものを返します。
	 */
	public Annotation getAnnotationValueOfBean(Class annotationClass){
		Map<String,Object> annotationValuMap=new HashMap<String,Object>();
		Annotation annotation=bean.getAnnotation(annotationClass);



		return annotation;
	}

	/**
	 * 指定されたフィールドの指定されたアノテーションを取得します。
	 * @param fieldName
	 * @param Annotation
	 * @param key
	 * @return
	 */
	public Annotation getAnnotationOfField(String fieldName,Class annotationClass){

		Field field =this.getField(fieldName);
		return field.getAnnotation(annotationClass);

	}



	/**
	 * 指定されたフィールドの指定されたアノテーションを取得します。
	 * @param fieldName
	 * @param Annotation
	 * @param key
	 * @return
	 */
	public Annotation[] getAnnotationsOfField(String fieldName){

		Field field =this.getField(fieldName);
		return field.getAnnotations();

	}

	/**
	 * ���̃R�l�N�^�[�ɃZ�b�g���ꂽbean�̎w�肵���t�B�[���h��getter���\�b�h���Ăяo���܂�
	 *
	 * @param fieldName
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	protected Method getGetterMethod(String fieldName)
			throws NoSuchMethodException, SecurityException {
		String getter = GET + fieldName.substring(ZERO, ONE).toUpperCase()
				+ fieldName.substring(ONE);
		return bean.getDeclaredMethod(getter);

	}

	/**
	 * ���̃R�l�N�^�[�ɃZ�b�g���ꂽbean�̎w�肵���t�B�[���h��setter���\�b�h���Ăяo���܂�
	 *
	 * @param field
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	protected Method getSetterMethod(String fieldName) throws NoSuchMethodException,
			SecurityException, NoSuchFieldException {
		String setter = SET + fieldName.substring(ZERO, ONE).toUpperCase()
				+ fieldName.substring(ONE);
		return bean.getDeclaredMethod(setter,getField(fieldName).getType());
	}

	/**
	 * �w�肳�ꂽ�I�u�W�F�N�g�̎w�肳�ꂽ�t�B�[���h��getter���\�b�h��p���ăI�u�W�F�N�g���擾���܂��B
	 *
	 * @param fieldName
	 * @param beanObject
	 * @return
	 * @throws GetterMethodException
	 */
	public Object getObjectByGetter(String fieldName, Object beanObject)
			throws GetterMethodException {
		Object obj = null;
		try {
			obj = getGetterMethod(fieldName).invoke(beanObject);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
			throw new GetterMethodException(e);
		}
		return obj;
	}

	/**
	 * �w�肳�ꂽ�I�u�W�F�N�g�̎w�肳�ꂽ�t�B�[���h��setter���\�b�h��p���ăI�u�W�F�N�g���Z�b�g���܂��B
	 *
	 * @param fieldName
	 * @param beanObject
	 * @param setObject
	 * @throws SetterMethodException
	 */
	public void setObjectBySetter(String fieldName, Object beanObject,
			Object setObject) throws SetterMethodException {
		try {
			getSetterMethod(fieldName).invoke(beanObject, setObject);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException | NoSuchFieldException e) {
			e.printStackTrace();
			throw new SetterMethodException(e);

		}
	}

}