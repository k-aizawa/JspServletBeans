package beanForWebServlet.lts.fsp;

public class FspStringParser {

	private int tokenAt;
	private char[] fspCharArray;

	public FspStringParser(String fsp){
		fspCharArray=fsp.toCharArray();
		tokenAt=0;
	}

	/**
	 * tokenAtを取得します。
	 * @return tokenAt
	 */
	public int getTokenAt() {
	    return tokenAt;
	}

	/**
	 * tokenAtを設定します。
	 * @param tokenAt tokenAt
	 */
	public void setTokenAt(int tokenAt) {
	    this.tokenAt = tokenAt;
	}

	/**
	 * fspCharArrayを取得します。
	 * @return fspCharArray
	 */
	public char[] getFspCharArray() {
	    return fspCharArray;
	}

	/**
	 * fspCharArrayを設定します。
	 * @param fspCharArray fspCharArray
	 */
	public void setFspCharArray(char[] fspCharArray) {
	    this.fspCharArray = fspCharArray;
	}

	public Character nextChar(){
		if(tokenAt<fspCharArray.length)
			return fspCharArray[tokenAt++];
		return null;
	}

	public Character currentChar(){

		if(tokenAt<fspCharArray.length)
			return fspCharArray[tokenAt];
		return null;
	}

	public int currentToken(){
		return tokenAt;
	}

	public Character offSetChar(int i) {
		if(tokenAt+i<fspCharArray.length)
			return fspCharArray[tokenAt+i];
		return null;
	}

	public boolean isLast(){
		return tokenAt>=fspCharArray.length-1;
	}
	public boolean isOver(){
		return tokenAt>fspCharArray.length-1;
	}
}
