package beanForWebServlet.lts;

import java.util.List;
import java.util.Map;

public class LtsElementStr {
	private Map<List<String>,String> actionList_targetStateStr;
	private String stateStr;
	/**
	 * actionList_targetStateStrを取得します。
	 * @return actionList_targetStateStr
	 */
	public Map<List<String>,String> getActionList_targetStateStr() {
	    return actionList_targetStateStr;
	}
	/**
	 * actionList_targetStateStrを設定します。
	 * @param actionList_targetStateStr actionList_targetStateStr
	 */
	public void setActionList_targetStateStr(Map<List<String>,String> actionList_targetStateStr) {
	    this.actionList_targetStateStr = actionList_targetStateStr;
	}
	/**
	 * stateStrを取得します。
	 * @return stateStr
	 */
	public String getStateStr() {
	    return stateStr;
	}
	/**
	 * stateStrを設定します。
	 * @param stateStr stateStr
	 */
	public void setStateStr(String stateStr) {
	    this.stateStr = stateStr;
	}



}
