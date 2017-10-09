package beanForWebServlet.lts.fsp;

import java.util.*;

import beanForWebServlet.lts.LtsElementStr;
import beanForWebServlet.lts.ltsElement.LtsModel;
import beanForWebServlet.lts.ltsElement.LtsState;
import beanForWebServlet.lts.ltsElement.LtsTransition;

/**
 *
 * @author 和也
 *
 *         BNF気泡 <FSP>::=<EXP>"."|<EXP>","<FSP> <EXP>::=<STATE>"=("<TERM>")"
 *         <TERM>::=<ACTION>"->"<STATE>|<ACTION>"->"<STATE>"|"<TERM>
 *
 *
 *
 *         <ACTION>::=<ACTION_TERM>|<ACTION_TERM>"."<ACTION>
 *         <ACTION_TERM>::=<STRING_SET>|<RANGES>|<STRING_SET><RANGES>|<STRING><RANGES>|<STRING>
 *         <STATE>::=<STRING>
 *         <RANGES>::="["<NUMBER>".."<NUMBER>"]"
 *         <STRING_SET>::="{"<STRING_CSV>"}
 *         <STRING_CSV>::=<ACTION>","<STRING_CSV>|<ACTION>
 *         <STRING>::="{a-zA-Z0-9}."
 */

// 各モデルのテキストファイルを読み込むためのファイル
public class FspTranslator {

	public static LtsModel translateFspToLts(List<String> fspList) {

		return null;

	}

	public static void processFsp(List<String> fspList) {
		Iterator<String> fspIterator = fspList.iterator();
		LtsModel ltsModel = new LtsModel();

		while (fspIterator.hasNext()) {
			String fspStr = fspIterator.next();
			int tokenAt = 0;
			char[] fspCharArray = fspStr.toCharArray();
		}

	}

	public static List<LtsElementStr> getLtsElementStrFromResourceBundle(ResourceBundle rb){
		Iterator<String> keyIterator=rb.keySet().iterator();
		List<LtsElementStr> lesList=new ArrayList<LtsElementStr>();
		while(keyIterator.hasNext()){
			String sourceState=keyIterator.next();
			String termState="";
			lesList.add(processTermInBracket(new FspStringParser(rb.getString(sourceState)), sourceState));
		}

		return lesList;

	}

	/**
	 *
	 * <EXP>::=<STATE>"=("<TERM>")"
	 *
	 * @param expCharArray
	 * @return
	 */
	public static LtsElementStr processExp(FspStringParser fspObject) {
		String sourceStateStr = "";
		while (fspObject.currentChar() != '=') {
			sourceStateStr += fspObject.nextChar();
		}
		fspObject.nextChar();
		LtsElementStr ltsElmStr = processTermInBracket(fspObject,
				sourceStateStr);

		return ltsElmStr;
	}

	private static LtsElementStr processTermInBracket(
			FspStringParser fspObject, String sourceStateStr) {
		String termStr = "";
		while (fspObject.currentChar() != '(') {
			fspObject.nextChar();
		}
		fspObject.nextChar();
		while (fspObject.currentChar() != ')') {
			termStr += fspObject.nextChar();
		}
		fspObject.nextChar();
		LtsElementStr ltsElmStr = new LtsElementStr();
		ltsElmStr.setActionList_targetStateStr(processTerm(new FspStringParser(
				termStr)));
		ltsElmStr.setStateStr(sourceStateStr);
		return ltsElmStr;
	}

	private static Map<List<String>, String> processTerm(
			FspStringParser fspObject) {
		String actionStr = "";
		Map<List<String>, String> action_targetMap = new HashMap<List<String>, String>();
		while (!fspObject.isOver()
				&& (fspObject.currentChar() != '-' || fspObject.offSetChar(1) != '>')) {
			actionStr += fspObject.nextChar();
		}
		fspObject.nextChar();
		fspObject.nextChar();

		String termOrState = "";
		while (!fspObject.isOver() && fspObject.currentChar() != '|') {
			termOrState += fspObject.nextChar();
		}
		if (fspObject != null && !fspObject.isOver()
				&& fspObject.currentChar() == '|') {
			fspObject.nextChar();
			action_targetMap.put(processAction(new FspStringParser(actionStr)),
					termOrState.trim());
			action_targetMap.putAll(processTerm(fspObject));
		} else {
			List<String> actionList = new ArrayList<String>();
			actionList.addAll(processAction(new FspStringParser(actionStr)));
			action_targetMap.put(actionList, termOrState.trim());
		}
		return action_targetMap;
	}

	/**
	 * <ACTION>::=<ACTION_TERM>|<ACTION_TERM>"."<ACTION>
	 * <ACTION_TERM>::=<STRING_SET
	 * >|<RANGES>|<STRING_SET><RANGES>|<STRING><RANGES>|<STRING>
	 * <STATE>::=<STRING> <RANGES>::="["<NUMBER>".."<NUMBER>"]"
	 * <STRING_SET>::="{"<STRING_CSV>"}
	 * <STRING_CSV>::=<STRING>|<STRING>","<STRING_CSV>|<ACTION>
	 * <STRING>::="{a-zA-Z0-9}."
	 *
	 * @param actionStr
	 * @return
	 */
	private static List<String> processAction(FspStringParser fspParser) {
		String action_term = "";
		int inBracket = 0;
		while ((!fspParser.isOver() && (inBracket != 0 || fspParser
				.currentChar() != '.'))) {
			if (fspParser.currentChar() == '{'
					|| fspParser.currentChar() == '[') {
				inBracket++;
			} else if (fspParser.currentChar() == '}'
					|| fspParser.currentChar() == ']') {
				inBracket--;
			}
			action_term += fspParser.nextChar();
		}
		// System.out.println(action_term+","+fspParser.currentChar());
		List<List<String>> listOfActionElementList = new ArrayList<List<String>>();
		if (!action_term.isEmpty()) {
			listOfActionElementList = processAction_term(new FspStringParser(
					action_term));
		}
		if (!fspParser.isOver() && fspParser.currentChar() == '.') {
			fspParser.nextChar();
			List<String> list = processAction(fspParser);
			for (int i = 0; i < list.size(); i++) {
				list.set(i, "." + list.get(i));
			}
			listOfActionElementList.add(list);
		}
		List<String> actionList = makeActionListFromElementList(listOfActionElementList);

		return actionList;
	}

	private static List<String> makeActionListFromElementList(
			List<List<String>> listOfActionList) {
		List<String> actionList = new ArrayList<String>();
		for (int i = listOfActionList.size() - 1; i >= 0; i--) {
			List<String> actionElementList = listOfActionList.get(i);
			if (actionList.isEmpty()) {
				actionList = actionElementList;
			} else {

				List<String> actionTempList = new ArrayList<String>();
				for (int j = 0; j < actionElementList.size(); j++) {
					for (int k = 0; k < actionList.size(); k++) {
						actionTempList.add(actionElementList.get(j)
								+ actionList.get(k));
					}
				}
				actionList = actionTempList;
			}
		}
		return actionList;
	}

	/**
	 * <ACTION_TERM>::=<STRING_SET>|<STRING_SET><RANGES>|<STRING><RANGES>|<
	 * RANGES>|<STRING> <RANGES>::="["<NUMBER>".."<NUMBER>"]"
	 * <STRING_SET>::="{"<STRING_CSV>"}
	 * <STRING_CSV>::=<STRING>|<STRING>","<STRING_CSV>|<ACTION>
	 */
	private static List<List<String>> processAction_term(
			FspStringParser fspParser) {
		List<List<String>> actionList = new ArrayList<List<String>>();
		// RANGEの場合、そのリストを追加
		if (fspParser.currentChar() == '{') {
			actionList.add(processString_set(new FspStringParser(
					getAction_setStr(fspParser))));
			if (!fspParser.isOver()&&fspParser.currentChar() == '[') {
				actionList.add(processRanges(fspParser));
			}
		} else if (fspParser.currentChar() == '[') {
			actionList.add(processRanges(fspParser));
		} else {
			String actionStr = "";
			while (fspParser.currentChar() != '.'
					&& fspParser.currentChar() != '[') {
				actionStr += fspParser.nextChar();
				if (fspParser.isLast()) {
					actionStr += fspParser.currentChar();
					break;

				}
			}

			List<String> singleAct = new ArrayList<String>();
			singleAct.add(actionStr.trim());
			actionList.add(singleAct);

			if (!fspParser.isLast() && fspParser.currentChar() == '[') {
				actionList.add(processRanges(fspParser));
			}
		}
		return actionList;
	}

	private static String getAction_setStr(FspStringParser fspParser) {
		int action_setCount = 1;
		String actionSetStr = "";
		actionSetStr += fspParser.nextChar();
		while (action_setCount > 0) {
			if (fspParser.currentChar() == '{') {
				action_setCount++;
				actionSetStr += fspParser.nextChar();
			} else if (fspParser.currentChar() == '}') {
				action_setCount--;
				actionSetStr += fspParser.nextChar();
			} else {
				actionSetStr += fspParser.nextChar();
			}
		}
		return actionSetStr;
	}

	/**
	 * <STRING_SET>::="{"<STRING_CSV>"}|<STRING>
	 *
	 * @param actionTokenAt
	 */
	private static List<String> processString_set(FspStringParser fspPerser) {
		List<String> actionList = new ArrayList<String>();
		if (fspPerser.currentChar() == '{') {
			fspPerser.nextChar();
			actionList.addAll(processString_csv(fspPerser));

		}
		return actionList;
	}

	/**
	 *
	 *
	 * <ACTION>::=<ACTION_TERM>|<ACTION_TERM>"."<ACTION>
	 * <ACTION_TERM>::=<STRING_SET>|<RANGES>|<STRING_SET><RANGES>
	 * <STRING_SET>::="{"<STRING_CSV>"}|<STRING>
	 * <STRING_CSV>::=<ACTION>|<ACTION>","<STRING_CSV>|
	 *
	 * @param actionCharArray
	 * @param actionTokenAt
	 */
	private static List<String> processString_csv(FspStringParser fspParser) {
		List<String> actionList = new ArrayList<String>();

		String actionStr = "";
		boolean isAdded = false;
		int inBracket=0;
		//ここめっちゃ直す。
		while (!fspParser.isOver() &&(fspParser.currentChar() != '}')||inBracket!=0) {
			if(fspParser.currentChar()=='{')
				inBracket++;
			else if(fspParser.currentChar()=='}')
				inBracket--;

			if (inBracket==0&&fspParser.currentChar() == ',') {
				actionList.addAll(processAction(new FspStringParser(actionStr)));
				fspParser.nextChar();
				actionList.addAll(processString_csv(fspParser));
				isAdded = true;
			} else {
				actionStr += fspParser.nextChar();
			}
		}
		fspParser.nextChar();
		if (!isAdded) {
			actionList.addAll(processAction(new FspStringParser(actionStr)));
		}

		return actionList;
	}

	private static List<String> processRanges(FspStringParser fspParser) {
		char left = '[';
		char right = ']';
		String beginNum = "";
		String endNum = "";
		List<String> rangeList = new ArrayList<String>();
		if (fspParser.currentChar() == '[') {
			fspParser.nextChar();
			while (!fspParser.isOver() && fspParser.currentChar() != '.') {
				beginNum += fspParser.nextChar();
			}
			fspParser.nextChar();
			fspParser.nextChar();
			while (fspParser.currentChar() != ']') {
				endNum += fspParser.nextChar();
			}
			fspParser.nextChar();

			int begin = Integer.parseInt(beginNum);
			int end = Integer.parseInt(endNum);

			for (int i = begin; i <= end; i++) {
				rangeList.add("" + left + i + right);
			}
		}
		return rangeList;
	}

	private static void regisgerAction(String sourceStateStr,
			List<String> actionStrList, String targetStateStr, LtsModel model) {

		LtsState source = new LtsState(), target = new LtsState();
		source.setName(sourceStateStr);
		target.setName(targetStateStr);

		LtsTransition action = new LtsTransition();
		action.setName(actionStrList.get(0));

	}

}