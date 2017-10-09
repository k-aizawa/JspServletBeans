package beanForWebServlet.lts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import beanForWebServlet.lts.ltsElement.LtsModel;
import beanForWebServlet.lts.ltsElement.LtsState;
import beanForWebServlet.lts.ltsElement.LtsTransition;

/**
 * LTSモデルの状態遷移を行う。 セットされたモデルの状態遷移や状態の抽出を行う。
 *
 * @author 和也
 *
 */
public class LtsDriver {
	private LtsModel ltsModel;

	private LtsState currentState;

	private List<String[]> history;

	public LtsDriver(LtsModel ltsModel) {
		this.ltsModel = ltsModel;
		currentState = ltsModel.getInitialState();
		history = new ArrayList<String[]>();
	}

	public void initialization() {
		currentState = ltsModel.getInitialState();
	}

	public String getCurrentState() {
		return currentState.getName();
	}

	public boolean setCurrentState(String state) {
		LtsState lstate = ltsModel.getStateMap().get(state);
		if (lstate == null)
			return false;
		currentState = lstate;
		return true;
	}

	public LtsState getLtsState(String stateStr) {
		return ltsModel.getStateMap().get(stateStr);
	}

	public boolean transition(String transitionStr) {
		Map<String, LtsTransition> transitionMap = currentState
				.getDirectionMap();
		LtsTransition transition = transitionMap.get(transitionStr);
		if (transition == null)
			return false;
		addHistory(currentState.getName(), transition.getName());
		currentState = transition.getDirection();
		return true;

	}

	public boolean rollback(String transitionStr) {
		LtsTransition transition = currentState.getSourceMap().get(
				transitionStr);
		if (transition == null)
			return false;

		currentState = transition.getSource();
		return true;
	}

	public List<String> getDirectionActionListOfCurrentState() {
		return getActionListOfCurrentState(true);
	}

	public List<String> getSourceActionListOfCurrentState() {
		return getActionListOfCurrentState(false);
	}

	private List<String> getActionListOfCurrentState(boolean isDirection) {
		Map<String, LtsTransition> actionMap;

		if (isDirection)
			actionMap = currentState.getDirectionMap();
		else
			actionMap = currentState.getSourceMap();

		List<String> directionList = new ArrayList<String>();
		directionList.addAll(actionMap.keySet());

		return directionList;
	}

	// For debug
	public String checkNextState(String transitionAction) {
		return currentState.getDirectionMap().get(transitionAction).getName();
	}

	public void addHistory(String state, String action) {
		String[] hist = { state, action };
		history.add(hist);
	}

	public List<String[]> showHistory() {
		return history;
	}

	public void jumpHistory(String state) {
		int pointa = getHistoryPointer(state);
		if (pointa != history.size())
			currentState = ltsModel.getStateMap().get(history.get(pointa)[0]);
	}

	public int getHistoryPointer(String state) {
		int pointa = history.size();
		System.out.print("[getHistoryPointa]"+state+","+currentState.getName());
		if (state.equals(currentState.getName())) {
			System.out.println("true");
			return pointa;
		}

		pointa--;
		while (history.get(pointa) != null) {
			System.out.println("[getHistoryPointa]"+state+","+history.get(pointa)[0]);
			if (!history.get(pointa)[0].equals(state))
				break;
			System.out.println(pointa + "," + history.get(pointa)[0] + "<->"
					+ state);
			pointa--;
		}
		return pointa;
	}

	public String[] getHistory(int pointa) {
		if(pointa>history.size()||pointa<0)return null;
		if(pointa==history.size()){
			String[] current={currentState.getName(),""};
			return current;
		}
		return history.get(pointa);
	}

}
