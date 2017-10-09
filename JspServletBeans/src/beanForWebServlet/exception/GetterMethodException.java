package beanForWebServlet.exception;

public class GetterMethodException extends Exception {

	public GetterMethodException(String message) {
		super(message);
	}
	public GetterMethodException(Exception e){
		super(e);
	}
	
}
