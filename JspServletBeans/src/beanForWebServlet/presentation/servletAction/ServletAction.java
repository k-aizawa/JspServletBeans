package beanForWebServlet.presentation.servletAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ServletAction {
	public final String SUCCESS="success";
	public final String FAILURE ="failure";

	public String doAction(HttpServletRequest request,HttpServletResponse response);


}
