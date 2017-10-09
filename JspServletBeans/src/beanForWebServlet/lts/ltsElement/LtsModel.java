package beanForWebServlet.lts.ltsElement;

import java.util.Map;

public class LtsModel {

	private LtsState initialState;

	private Map<String, LtsState> stateMap;

	/**
	 * initialStateを取得します。
	 * @return initialState
	 */
	public LtsState getInitialState() {
	    return initialState;
	}

	/**
	 * initialStateを設定します。
	 * @param initialState initialState
	 */
	public void setInitialState(LtsState initialState) {
	    this.initialState = initialState;
	}

	/**
	 * stateMapを取得します。
	 * @return stateMap
	 */
	public Map<String,LtsState> getStateMap() {
	    return stateMap;
	}

	/**
	 * stateMapを設定します。
	 * @param stateMap stateMap
	 */
	public void setStateMap(Map<String,LtsState> stateMap) {
	    this.stateMap = stateMap;
	}

}
