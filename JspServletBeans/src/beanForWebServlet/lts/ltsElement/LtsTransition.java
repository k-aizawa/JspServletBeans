package beanForWebServlet.lts.ltsElement;

public class LtsTransition {
	/**
	 * 遷移名称
	 */
	private String name;
	/**
	 * 遷移先状態
	 */
	private LtsState direction;
	/**
	 * 遷移元状態
	 */
	private LtsState source;
	/**
	 * 遷移名称を取得します。
	 * @return 遷移名称
	 */
	public String getName() {
	    return name;
	}
	/**
	 * 遷移名称を設定します。
	 * @param name 遷移名称
	 */
	public void setName(String name) {
	    this.name = name;
	}
	/**
	 * 遷移先状態を取得します。
	 * @return 遷移先状態
	 */
	public LtsState getDirection() {
	    return direction;
	}
	/**
	 * 遷移先状態を設定します。
	 * @param direction 遷移先状態
	 */
	public void setDirection(LtsState direction) {
	    this.direction = direction;
	}
	/**
	 * 遷移元状態を取得します。
	 * @return 遷移元状態
	 */
	public LtsState getSource() {
	    return source;
	}
	/**
	 * 遷移元状態を設定します。
	 * @param source 遷移元状態
	 */
	public void setSource(LtsState source) {
	    this.source = source;
	}

}
