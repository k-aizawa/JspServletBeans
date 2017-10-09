package beanForWebServlet.lts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import beanForWebServlet.lts.ltsElement.LtsModel;
import beanForWebServlet.lts.ltsElement.LtsState;
import beanForWebServlet.lts.ltsElement.LtsTransition;


public class LtsFactory {

	private LtsModel ltsModel;
	private LtsDriver ltsDriver;

	private LtsFactory(){
		this.ltsModel=new LtsModel();
		this.ltsModel.setStateMap(new HashMap<String,LtsState>());
		this.ltsDriver=new LtsDriver(ltsModel);
	}

	public static LtsDriver generateLtsFromFsp(List<LtsElementStr> lesList){
		return generateLtsFactoryFromFsp(lesList,null).getLtsDriver();
	}

	public static LtsModel generateLtsModelFromFsp(List<LtsElementStr> lesList){
		return generateLtsModelFromFsp(lesList,null);
	}

	public static LtsModel generateLtsModelFromFsp(List<LtsElementStr> lesList,String initialState){
		return generateLtsFactoryFromFsp(lesList,initialState).getLtsModel();

	}

	public static LtsFactory generateLtsFactoryFromFsp(List<LtsElementStr> lesList,String initialState){
		LtsFactory ltsFactory=new LtsFactory();

		for(LtsElementStr les:lesList){
			ltsFactory.registerTransition(les);
		}
		if(initialState==null||initialState.isEmpty())
			ltsFactory.setInitialStateDefault();
		else
			ltsFactory.setInitialState(initialState);
		return ltsFactory;
	}

	private boolean setInitialStateDefault(){
		return setInitialState("Q0");
	}

	private boolean setInitialState(String initial){
		LtsState state=ltsModel.getStateMap().get(initial);
		if(state==null)return false;

		ltsModel.setInitialState(state);
		ltsDriver.initialization();
		return true;
	}

	private LtsDriver getLtsDriver() {
		return ltsDriver;
	}

	private LtsModel getLtsModel(){
		return ltsModel;
	}

	private void registerState(String stateStr){
		LtsState state=new LtsState();

		state.setName(stateStr);
		state.setDirectionMap(new HashMap<String,LtsTransition>());
		state.setSourceMap(new HashMap<String,LtsTransition>());

		ltsModel.getStateMap().put(stateStr, state);

	}

	private void registerTransition(LtsElementStr les){
		String sourceState=les.getStateStr();
		Map<List<String>,String> actionMap=les.getActionList_targetStateStr();
		Iterator<List<String>> actionIt=actionMap.keySet().iterator();
		while(actionIt.hasNext()){
			List<String> actionList=actionIt.next();
			String targetState=actionMap.get(actionList);
			for(String action:actionList){
				registerTransition(action,sourceState,targetState);
			}
		}
	}

	private void registerTransition(String transitionStr,String sourceStateStr,String targetStateStr){
		LtsState source=ltsDriver.getLtsState(sourceStateStr);
		if(source==null){
			registerState(sourceStateStr);
			source=ltsDriver.getLtsState(sourceStateStr);
		}
		LtsState target=ltsDriver.getLtsState(targetStateStr);
		if(target==null){
			registerState(targetStateStr);
			target=ltsDriver.getLtsState(targetStateStr);
		}
		LtsTransition transition=new LtsTransition();
		//Transitionの生成・登録
		transition.setName(transitionStr);
		transition.setDirection(target);
		transition.setSource(source);

		//TargetのSourceにTransitionを登録
		target.getSourceMap().put(transitionStr, transition);

		//SourceのDirectionにTransitionを登録
		source.getDirectionMap().put(transitionStr, transition);


	}
}
