package beanForWebServlet.presentation;

import java.io.IOException;
import java.text.Annotation;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beanForWebServlet.dao.DataBaseAccessor;
import beanForWebServlet.lts.LtsDriver;
import beanForWebServlet.lts.LtsElementStr;
import beanForWebServlet.lts.LtsFactory;
import beanForWebServlet.lts.LtsServletActionConnector;
import beanForWebServlet.lts.fsp.FspTranslator;
import beanForWebServlet.lts.ltsElement.LtsModel;
import beanForWebServlet.presentation.servletAction.ServletAction;
import beanForWebServlet.presentation.servletAction.annotation.ScreenTransitionTarget;

/**
 * Servlet implementation class LtsController
 */
@WebServlet("/LtsController")
public class LtsController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String ACTION = "action";
	private static final String INIT = "init";
	private static final String FSP = "FSP";
	public static final String DB_ACCESS_KEY = "java:comp/env/jdbc/TestDB";

	private static ServletLtsModel ltsModel;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LtsController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// プロパティファイルからLtsモデルを取得し、web.xmlからServletActionのパッケージを取得する。
		ResourceBundle rb = ResourceBundle.getBundle(FSP);
		ServletContext sc = config.getServletContext();
		String pkgName = sc.getInitParameter("pkgName");

		// LTSモデルを作成する。
		List<LtsElementStr> lesList = FspTranslator
				.getLtsElementStrFromResourceBundle(rb);
		ltsModel = new ServletLtsModel(
				LtsFactory.generateLtsModelFromFsp(lesList));

		// LTSモデルのアクションとServletActionのマッピングを作成する。
		try {
			ltsModel.setActionMap(pkgName);
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		// DetaBaseとの接続を行う。
		String dataSource = sc.getInitParameter("dataSource");
		DataBaseAccessor.setDataSource(dataSource);

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		String action;
		ServletAction servletAction;
		ScreenTransitionTarget screenTransition;

		// sessionからdriverを取得する。取得できない場合は新規に作成する。
		ServletLtsDriver driver = (ServletLtsDriver) session
				.getAttribute("driver");
		if (driver == null) {
			System.out.println("new driver");
			driver = new ServletLtsDriver(ltsModel);
			// 初期アクションを取得
			action = driver.getDirectionActionListOfCurrentState().get(0);
		} else {
			// フォームからアクションを取得
			action = request.getParameter(ACTION);
			// TODO アクションが存在しない場合はエラーとし、直前の遷移を再現する
			if (action == null) {
				action = driver.getLastScreenTransitionAction();
			}
		}

		servletAction = ltsModel.getServletAction(action);

		screenTransition = servletAction.getClass().getAnnotation(
				ScreenTransitionTarget.class);
		while (screenTransition == null) {
			System.out.print(driver.getCurrentState() + "->" + action + ":");

			if (driver.transition(action)) {
				System.out.println("true");
				String result = servletAction.doAction(request, response);
				// TODO アクションの結果と次のアクションへの遷移関係の整理。
				driver.transition(action + "." + result);
				action = driver.getDirectionActionListOfCurrentState().get(0);
				servletAction = ltsModel.getServletAction(action);
				screenTransition = servletAction.getClass().getAnnotation(
						ScreenTransitionTarget.class);

			} else {
				System.out.println("false");
				driver.setCurrentState(driver.getLastScreenTransitionState());
				action = driver.getLastScreenTransitionAction();
				servletAction = ltsModel.getServletAction(action);
				screenTransition = servletAction.getClass().getAnnotation(
						ScreenTransitionTarget.class);

			}
		}

		servletAction.doAction(request, response);
		driver.setLastScreenTransitionState(driver.getCurrentState());
		driver.transition(action);
		driver.setLastScreenTransitionAction(action);
		session.setAttribute("driver", driver);
		request.getRequestDispatcher(screenTransition.target()).forward(
				request, response);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
