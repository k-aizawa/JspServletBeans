package beanForWebServlet.presentation;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beanForWebServlet.presentation.servletAction.ServletAction;

public class Controller {
	private static final String SERVELT_ACTION = "ServletAction";
	private static final String DIRECTION_JSP = "DirectionToJsp";
	private static final String DIRECTION_COMPO = "directionOfJspCompo";
	private static final String SCREEN = "screen";
	private static final String COMPO = "compo";
	private static final String INIT = "init";

	public static String controlAction(HttpServletRequest request,
			HttpServletResponse response) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {

		// プロパティファイルの読み込み
		ResourceBundle sa = ResourceBundle.getBundle(SERVELT_ACTION);
		ResourceBundle dirJ = ResourceBundle.getBundle(DIRECTION_JSP);
		ResourceBundle rbDC = ResourceBundle.getBundle(DIRECTION_COMPO);

		// スクリーン名と部品名の取得
		String screen = request.getParameter(SCREEN);
		String compo = request.getParameter(COMPO);
		String triger;
		if (screen == null || screen.isEmpty() || compo == null
				|| compo.isEmpty()) {
			triger = INIT;
		} else {
			triger = screen + "." + compo;
		}

		// スクリーン名と部品名に対応する処理の呼び出し
		String servletName = sa.getString(triger);
		ServletAction action = (ServletAction) Class.forName(servletName)
				.newInstance();
		String result = servletName + "." + action.doAction(request, response);
		if (result.contains("logic")) {
			while (result.contains("logic")) {
				//ロジックアクション
				servletName = sa.getString(result);
				action = (ServletAction) Class.forName(servletName)
						.newInstance();
				result = servletName + "." + action.doAction(request, response);

			}
			//遷移アクション
			servletName = sa.getString(result);
			action = (ServletAction) Class.forName(servletName)
					.newInstance();
			result = servletName + "." + action.doAction(request, response);

		}
		// 処理結果に基づく次画面への遷移準備
		String jspName = dirJ.getString(result);
		String[] jspCompo = rbDC.getString(jspName).split(",");
		for (String compoName : jspCompo) {
			request.setAttribute(compoName, rbDC.getString(compoName));
		}
		return jspName;
	}
}
