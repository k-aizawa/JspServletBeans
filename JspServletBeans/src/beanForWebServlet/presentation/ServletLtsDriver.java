package beanForWebServlet.presentation;

import java.util.List;

import beanForWebServlet.lts.LtsDriver;
import beanForWebServlet.lts.ltsElement.LtsModel;
import beanForWebServlet.presentation.servletAction.ServletAction;
import beanForWebServlet.presentation.servletAction.annotation.ScreenTransitionTarget;

public class ServletLtsDriver extends LtsDriver {

	private String lastScreenTransitionAction;
	private String lastScreenTransitionState;
	private ServletLtsModel servletLtsModel;
	
	public ServletLtsDriver(LtsModel ltsModel) {
		super(ltsModel);
	}

	public ServletLtsDriver(ServletLtsModel servletLtsModel) {
		this((LtsModel) servletLtsModel);
		this.servletLtsModel = servletLtsModel;
	}

	/**
	 * lastScreenTransitionActionを取得します。
	 *
	 * @return lastScreenTransitionAction
	 */
	public String getLastScreenTransitionAction() {
		return lastScreenTransitionAction;
	}

	/**
	 * lastScreenTransitionActionを設定します。
	 *
	 * @param lastScreenTransitionAction
	 *            lastScreenTransitionAction
	 */
	public void setLastScreenTransitionAction(String lastScreenTransitionAction) {
		this.lastScreenTransitionAction = lastScreenTransitionAction;

	}

	/**
	 * lastScreenTransitionStateを取得します。
	 *
	 * @return lastScreenTransitionState
	 */
	public String getLastScreenTransitionState() {
		return lastScreenTransitionState;
	}

	/**
	 * lastScreenTransitionStateを設定します。
	 *
	 * @param lastScreenTransitionState
	 *            lastScreenTransitionState
	 */
	public void setLastScreenTransitionState(String lastScreenTransitionState) {
		this.lastScreenTransitionState = lastScreenTransitionState;
	}

	public ServletAction screenBack(String currentState) {
		int pointa = getHistoryPointer(currentState);
		if (pointa < 2)
			return null;
		pointa -= 2;
		String[] actionAndState = getHistory(pointa);
		System.out.println("[Screen back]"+pointa+":"+actionAndState[0] + "->" + actionAndState[1]);
		ServletAction servletAction = servletLtsModel
				.getServletAction(actionAndState[1]);
		ScreenTransitionTarget scTranTarget = servletAction.getClass()
				.getAnnotation(ScreenTransitionTarget.class);
		while (scTranTarget == null && pointa >= 0) {
			pointa--;
			if (pointa >= 0) {
				actionAndState = getHistory(pointa);
				System.out
						.println("[Screen back]"+pointa+":"+actionAndState[0] + "->" + actionAndState[1]);

				servletAction = servletLtsModel
						.getServletAction(actionAndState[1]);
				scTranTarget = servletAction.getClass().getAnnotation(
						ScreenTransitionTarget.class);
			}
		}
		if (scTranTarget != null) {
			jumpHistory(actionAndState[0]);
			return servletAction;
		}
		return null;
	}

}
