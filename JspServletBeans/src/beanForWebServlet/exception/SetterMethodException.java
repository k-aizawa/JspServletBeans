package beanForWebServlet.exception;

public class SetterMethodException extends Exception {

	public SetterMethodException(Exception e){
		super(e);
	}
	public SetterMethodException(String msg){
		super(msg);
	}
}
