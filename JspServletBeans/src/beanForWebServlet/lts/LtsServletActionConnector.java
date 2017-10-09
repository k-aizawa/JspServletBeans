package beanForWebServlet.lts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beanForWebServlet.lts.ltsElement.LtsModel;
import beanForWebServlet.lts.ltsElement.LtsTransition;
import beanForWebServlet.lts.ltsElement.annotation.LtsActionLink;
import beanForWebServlet.presentation.servletAction.ServletAction;
import beanForWebServlet.util.PackageExplorer;

public class LtsServletActionConnector {

	private static Map<String, ServletAction> connectServletActionToLtsModel(
			List<String> servletActionClassList) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		Map<String, ServletAction> ltsServletActionMap = new HashMap<String, ServletAction>();

		for (String servletActionClass : servletActionClassList) {
			Class servletAction = Class.forName(servletActionClass);

			LtsActionLink link = (LtsActionLink) servletAction
					.getAnnotation(LtsActionLink.class);
			String ltsActionName = link.ltsActionName();

			ltsServletActionMap.put(ltsActionName,
					(ServletAction) servletAction.newInstance());
		}

		return ltsServletActionMap;

	}

	public static void connect() {

	}

	public static Map<String, ServletAction> getServletActionMapFromPkg(
			String pkg) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		return connectServletActionToLtsModel(PackageExplorer
				.getClassNameListOfPackage(pkg));
	}
}
