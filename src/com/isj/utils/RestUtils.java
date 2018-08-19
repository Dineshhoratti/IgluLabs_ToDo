package com.isj.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class RestUtils {
	String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

	public static boolean isJSONValid(String jsonString) {
		boolean valid = true;
		JSONObject obj = new JSONObject();
		try {
			obj = (JSONObject) JSONSerializer.toJSON(jsonString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			valid = false;
		}
		return valid;
	}

	// get latest synch time

	// Invalid response
	public String processError(String statusCode, String message) {
		String errorJSONString = new String();
		JSONObject errorJSON = new JSONObject();
		JSONObject errorRespJSON = new JSONObject();
		JSONObject statusJSON = new JSONObject();
		statusJSON.put("statuscode", statusCode);
		statusJSON.put("statusmessage", message);
		errorRespJSON.put("response", statusJSON);
		errorJSON.put("json", errorRespJSON);
		errorJSONString = errorJSON.toString();
		return errorJSONString;

	}

	public String processErrorWithResponseObject(String statusCode,
			String message, int key) {
		String errorJSONString = new String();
		JSONObject errorJSON = new JSONObject();
		JSONObject errorRespJSON = new JSONObject();
		JSONObject statusJSON = new JSONObject();
		statusJSON.put("statuscode", statusCode);
		statusJSON.put("statusmessage", message);
		statusJSON.put("pin", key);
		errorRespJSON.put("response", statusJSON);
		errorJSON.put("json", errorRespJSON);
		errorJSONString = errorJSON.toString();
		return errorJSONString;

	}

	public Date StringDateToDate(String StrDate) {
		Date dateToReturn = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
		try {
			dateToReturn = (Date) dateFormat.parse(StrDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateToReturn;
	}

	public String getFormattedDateStr(Date date) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = null;
		if (date != null) {
			strDate = f.format(date);
			strDate.trim();
		}
		return strDate;
	}

	public Date getLastSynchTime() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		String utcTime = f.format(new Date());
		System.out.println("String date:" + utcTime);
		Date lastSynch = StringDateToDate(utcTime);
		System.out.println("Date :" + lastSynch);
		return lastSynch;
	}

	// get latest synch time
	public String getLatestSynchTime() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		String utcTime = f.format(new Date());
		// System.out.println("String date:"+utcTime);
		Date lastSynch = StringDateToDate(utcTime);
		String lastSynchTime = getFormattedDateStr(lastSynch);
		return lastSynchTime.trim();
	}

	public boolean isEmpty(String test) {
		if (test == null || test.trim().isEmpty() == true
				|| test.equalsIgnoreCase("[]") || test == "") {
			return false;
		}
		return true;
	}

	public String processSucess(String serviceType, String functionType,
			String dataText, JSONArray dataArray) {
		JSONObject sucessJSON = new JSONObject();
		JSONObject sucessRespJSON = new JSONObject();
		JSONObject contentJSON = new JSONObject();
		contentJSON.put("servicetype", serviceType);
		contentJSON.put("functiontype", functionType);
		contentJSON.put("statuscode",
				PropertiesUtil.getProperty("statuscodesuccessvalue"));
		contentJSON.put("statusmessage",
				PropertiesUtil.getProperty("statusmessagesuccessvalue"));
		if (dataArray != null && dataArray.size() > 0) {
			contentJSON.put("size", dataArray.size());
		}
		if (dataArray != null && dataArray.size() > 0) {
			contentJSON.put(dataText, dataArray);
		}
		sucessRespJSON.put("response", contentJSON);
		sucessJSON.put("json", sucessRespJSON);
		return sucessJSON.toString();

	}

	public String processSucessWithUserDetails(String serviceType,
			String functionType, String userDetails, String dataText,
			JSONArray dataArray) {
		JSONObject sucessJSON = new JSONObject();
		JSONObject sucessRespJSON = new JSONObject();
		JSONObject contentJSON = new JSONObject();
		contentJSON.put("servicetype", serviceType);
		contentJSON.put("functiontype", functionType);
		contentJSON.put("statuscode",
				PropertiesUtil.getProperty("statuscodesuccessvalue"));
		contentJSON.put("statusmessage",
				PropertiesUtil.getProperty("statusmessagesuccessvalue"));
		contentJSON.put("userdetails", userDetails);
		if (dataArray != null && dataArray.size() > 0) {
			contentJSON.put(dataText, dataArray);
			// contentJSON.put("userdetails", userDetails);
		}
		sucessRespJSON.put("response", contentJSON);
		sucessJSON.put("json", sucessRespJSON);
		return sucessJSON.toString();

	}

	//
	public String processSucessForModules(String serviceType,
			String functionType, String dataText, Object Obj) {
		JSONObject sucessJSON = new JSONObject();
		JSONObject sucessRespJSON = new JSONObject();
		JSONObject contentJSON = new JSONObject();
		contentJSON.put("servicetype", serviceType);
		contentJSON.put("functiontype", functionType);
		contentJSON.put("statuscode",
				PropertiesUtil.getProperty("statuscodesuccessvalue"));
		contentJSON.put("statusmessage",
				PropertiesUtil.getProperty("statusmessagesuccessvalue"));
		if (Obj instanceof JSONArray) {
			contentJSON.put(dataText, Obj);
		} else if (Obj instanceof JSONObject) {
			contentJSON.put(dataText, Obj);
		}
		sucessRespJSON.put("response", contentJSON);
		sucessJSON.put("json", sucessRespJSON);
		return sucessJSON.toString();

	}

	public String processOnlySucess(String serviceType, String functionType) {
		JSONObject sucessJSON = new JSONObject();
		JSONObject sucessRespJSON = new JSONObject();
		JSONObject contentJSON = new JSONObject();
		contentJSON.put("servicetype", serviceType);
		contentJSON.put("functiontype", functionType);
		contentJSON.put("statuscode",
				PropertiesUtil.getProperty("statuscodesuccessvalue"));
		contentJSON.put("statusmessage",
				PropertiesUtil.getProperty("statusmessagesuccessvalue"));

		sucessRespJSON.put("response", contentJSON);
		sucessJSON.put("json", sucessRespJSON);
		return sucessJSON.toString();

	}

}
