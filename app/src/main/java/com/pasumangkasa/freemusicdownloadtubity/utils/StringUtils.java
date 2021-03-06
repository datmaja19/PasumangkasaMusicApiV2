package com.pasumangkasa.freemusicdownloadtubity.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * String utils
 * @author DoBao
 * Nov 28, 2012
 * @company Citigo
 * @email bao.do@citigovietnam.com
 */
public class StringUtils {
	
	public static final String TAG = StringUtils.class.getSimpleName();
	public static final String REGEX_SPECIAL_CHARACTER = "[^a-zA-Z0-9_]";
	
	public static String urlEncodeString(String data){
		if(data!=null && !data.equals("")){
			try {
				String dataEncode = URLEncoder.encode(data,"UTF-8");
				return dataEncode;
			}
			catch (UnsupportedEncodingException e) {
				DBLog.d(TAG, "---------->encodeError="+e.getMessage());
				e.printStackTrace();
			}
		}
		return data;
	}
	
	public static String urlDecodeString(String data){
		if(data!=null && !data.equals("")){
			try {
				String dataEncode = URLDecoder.decode(data, "UTF-8");
				return dataEncode;
			}
			catch (UnsupportedEncodingException e) {
				DBLog.d(TAG, "---------->decodeError="+e.getMessage());
				e.printStackTrace();
			}
		}
		return data;
	}
	
	public static String getSplitString(String mStrData, int maxLenght){
		if(mStrData!=null){
			if(mStrData.length()>maxLenght){
				return mStrData.substring(0, maxLenght)+"...";
			}
			return mStrData;
		}
		return null;
	}
	
	public static String formatHtmlBoldKeyword(String mStrOriginalData, String keyword){
		if(mStrOriginalData!=null && keyword!=null && !keyword.equals("")){
			if(mStrOriginalData.contains(keyword)){
				try {
					String mNewString = mStrOriginalData.replace(keyword, "<b>"+keyword+"</b>");
					return mNewString;
					
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return mStrOriginalData;
	}
	
	public static boolean isNumber(String data){
	    return data.matches("[+-]?\\d*(\\.\\d+)?");
	}
	
	public static boolean isEmpty(String mStr){
		if(mStr==null || mStr.equals("")){
			return true;
		}
		return false;
	}
	
	public static boolean isContainsSpecialCharacter(String mInput){
		if(mInput!=null && !mInput.equals("")){
			Pattern mPattern = Pattern.compile(REGEX_SPECIAL_CHARACTER);
			Matcher mMatcher = mPattern.matcher(mInput);
			if(mMatcher.find()){
				return true;
			}
		}
		return false;
	}
	public static float formatStringNumber(float number){
		String numberFormat = String.format(Locale.US, "%.2f", number);
		try {
			numberFormat=numberFormat.replace(",", ".");
			return Float.parseFloat(numberFormat);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return number;
	}
	public static String formatVisualNumber(long numberValue, String demiter) {
		String value = String.valueOf(numberValue);
		if (value.length() > 3) {
			try {
				int number = (int) Math.floor(value.length() / 3);
				int lenght = value.length();
				String total = "";
				for (int i = 0; i < number; i++) {
					for (int j = 0; j < 3; j++) {
						int index = lenght - 1 - (i * 3 + j);
						total = value.charAt(index) + total;
					}
					if (i != number - 1) {
						total = demiter + total;
					}
					else {
						int delta = lenght - number * 3;
						if (delta > 0) {
							total = demiter + total;
						}
					}
				}
				total = value.substring(0, lenght - number * 3) + total;
				return total;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return String.valueOf(value);
	}

}
