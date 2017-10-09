package beanForWebServlet.exception;

public class InvalidUseException extends Exception {
	public InvalidUseException(String msg){
		super(msg);
	}

	public InvalidUseException(Exception e){
		super(e);
	}
}
