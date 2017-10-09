package beanForWebServlet.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputChecker {

	public static boolean isAlphanumericHalfSize(String input){
		return formatCheck(input,"[0-9a-zA-Z]+");
	}
	public static boolean isRangeAlphanumericHalfSize(String input,int min,int max){
		return formatCheck(input,"[0-9a-zA-Z]{"+min+","+max+"}");
	}


	public static boolean isNumber(String input,int min,int max){
		return formatCheck(input,"[0-9]{"+min+","+max+"}");
	}

	public static boolean rangeCheck(String input,int min,int max){
		return formatCheck(input,".{"+min+","+max+"}");
	}

	public static boolean formatCheck(String input,String pattern){
		Pattern p=Pattern.compile(pattern);
		Matcher m=p.matcher(input);

		return m.matches();
	}
}
