package beanForWebServlet.exception;

public class NoSupportFieldException extends Exception {

	public NoSupportFieldException(String msg){
		super(msg);
	}

	public NoSupportFieldException(Exception e){
		super(e);
	}
}
