package beanForWebServlet.lts.ltsElement;

import java.util.Map;

public class LtsState {
	/**
	 * 状態名称
	 */
	private String name;
	/**
	 * 状態から出ている遷移のマップ
	 */
	private Map<String,LtsTransition> directionMap;
	/**
	 * 状態に入ってくる遷移のマップ
	 */
	private Map<String,LtsTransition> sourceMap;
	/**
	 * 状態名称を取得します。
	 * @return 状態名称
	 */
	public String getName() {
	    return name;
	}
	/**
	 * 状態名称を設定します。
	 * @param name 状態名称
	 */
	public void setName(String name) {
	    this.name = name;
	}
	/**
	 * 状態から出ている遷移のマップを取得します。
	 * @return 状態から出ている遷移のマップ
	 */
	public Map<String,LtsTransition> getDirectionMap() {
	    return directionMap;
	}
	/**
	 * 状態から出ている遷移のマップを設定します。
	 * @param directionMap 状態から出ている遷移のマップ
	 */
	public void setDirectionMap(Map<String,LtsTransition> directionMap) {
	    this.directionMap = directionMap;
	}
	/**
	 * 状態に入ってくる遷移のマップを取得します。
	 * @return 状態に入ってくる遷移のマップ
	 */
	public Map<String,LtsTransition> getSourceMap() {
	    return sourceMap;
	}
	/**
	 * 状態に入ってくる遷移のマップを設定します。
	 * @param sourceMap 状態に入ってくる遷移のマップ
	 */
	public void setSourceMap(Map<String,LtsTransition> sourceMap) {
	    this.sourceMap = sourceMap;
	}
}
