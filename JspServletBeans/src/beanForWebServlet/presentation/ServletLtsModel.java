package beanForWebServlet.presentation;

import java.util.Map;

import beanForWebServlet.lts.LtsServletActionConnector;
import beanForWebServlet.lts.ltsElement.LtsModel;
import beanForWebServlet.presentation.servletAction.ServletAction;

public class ServletLtsModel extends LtsModel {
	private Map<String, ServletAction> actionMap;

	public ServletLtsModel(LtsModel ltsModel) {
		super();
		this.setStateMap(ltsModel.getStateMap());
		this.setInitialState(ltsModel.getInitialState());
	}

	public void setActionMap(String pkgName) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		this.actionMap = LtsServletActionConnector
				.getServletActionMapFromPkg(pkgName);
	}

	public ServletAction getServletAction(String action) {
		return actionMap.get(action);
	}

}
