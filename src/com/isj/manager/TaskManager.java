package com.isj.manager;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import com.isj.utils.GlobalTags;
import com.isj.utils.PropertiesUtil;
import com.isj.utils.RestUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TaskManager {

	TaskOperations dho = new TaskOperations();
	RestUtils restUtils = new RestUtils();
	static String NEW_STR = "NEW";
	static String PROGRESS_STR = "PROGRESS";
	static String COMPLETED_STR = "COMPLETED";
	static int NEW = 0;
	static int PROGRESS = 1;
	static int COMPLETED = 2;

	public String getIgluLabsResponse(String surveyRequest,
			HttpServletRequest request) {
		String surveyResponse = null;
		String serviceType = null;
		String functionType = null;
		try {
			if (RestUtils.isJSONValid(surveyRequest) == false) {
				System.out.println("Json parsing fails");
				return restUtils.processError(
						PropertiesUtil.getProperty("XMLRequest_code"),
						PropertiesUtil.getProperty("XMLRequest_message"));
			}
			JSONObject jsonreq = new JSONObject();
			jsonreq = JSONObject.fromObject(surveyRequest);
			serviceType = jsonreq.getJSONObject(GlobalTags.JSON_TAG)
					.getJSONObject(GlobalTags.JSON_REQUEST_TAG)
					.getString(GlobalTags.JSON_SERVICE_TAG);
			functionType = jsonreq.getJSONObject(GlobalTags.JSON_TAG)
					.getJSONObject(GlobalTags.JSON_REQUEST_TAG)
					.getString(GlobalTags.JSON_FUNCTION_TAG);
			JSONObject dataObj = jsonreq.getJSONObject(GlobalTags.JSON_TAG)
					.getJSONObject(GlobalTags.JSON_REQUEST_TAG)
					.getJSONObject(GlobalTags.JSON_DATA_TAG);
			System.out.println("ST : " + serviceType + " FT : " + functionType);
			if (restUtils.isEmpty(serviceType) == false
					|| serviceType.equalsIgnoreCase(PropertiesUtil
							.getProperty("IgluLabs_ST")) == false) {
				return restUtils.processError(PropertiesUtil
						.getProperty("invalidserviceType_code"), PropertiesUtil
						.getProperty("invalidserviceType_message"));
			} else if (functionType.equalsIgnoreCase(PropertiesUtil
					.getProperty("SignUp_FT"))) {
				System.out.println("Signing Up");
				return signUpUser(dataObj);
			} else if (functionType.equalsIgnoreCase(PropertiesUtil
					.getProperty("Login_TO_FT"))) {
				return Login(dataObj, serviceType, functionType);
			} else if (functionType.equalsIgnoreCase(PropertiesUtil
					.getProperty("AddTask_FT"))) {
				return addTask(dataObj, serviceType, functionType);
			} else if (functionType.equalsIgnoreCase(PropertiesUtil
					.getProperty("ListTask_FT"))) {
				return listTask(dataObj, serviceType, functionType);
			} else if (functionType.equalsIgnoreCase(PropertiesUtil
					.getProperty("editTask_FT"))) {
				return editTask(dataObj, serviceType, functionType);
			} else if (functionType.equalsIgnoreCase(PropertiesUtil
					.getProperty("delete_task_FT"))) {
				return deleteTask(dataObj, serviceType, functionType);
			} else {
				return restUtils.processError(PropertiesUtil
						.getProperty("invalidfunctionType_code"),
						PropertiesUtil
								.getProperty("invalidfunctionType_message"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return surveyResponse;
	}

	public String signUpUser(JSONObject dataObj) {
		String res = null;
		String userName = null;
		String password = null;
		String cnfPassword = null;
		try {
			if (dataObj.containsKey(GlobalTags.USER_NAME_TAG)) {
				userName = dataObj.getString(GlobalTags.USER_NAME_TAG);
				if (restUtils.isEmpty(userName) == false) {
					return restUtils.processError(
							PropertiesUtil.getProperty("username_empty_code"),
							PropertiesUtil.getProperty("username_empty_msg"));
				}
			} else {
				return restUtils.processError(
						PropertiesUtil.getProperty("username_key_code"),
						PropertiesUtil.getProperty("username_key_msg"));
			}
			if (dataObj.containsKey(GlobalTags.PASSWORD_TAG)) {
				password = dataObj.getString(GlobalTags.PASSWORD_TAG);
				if (restUtils.isEmpty(password) == false) {
					return restUtils.processError(
							PropertiesUtil.getProperty("pass_empty_code"),
							PropertiesUtil.getProperty("pass_empty_msg"));
				}
			} else {
				return restUtils.processError(
						PropertiesUtil.getProperty("pass_nokey_code"),
						PropertiesUtil.getProperty("pass_nokey_msg"));
			}
			if (dataObj.containsKey(GlobalTags.CNF_PASSWORD_TAG)) {
				cnfPassword = dataObj.getString(GlobalTags.CNF_PASSWORD_TAG);
				if (restUtils.isEmpty(cnfPassword) == false) {
					return restUtils.processError(
							PropertiesUtil.getProperty("cnfpass_empty_code"),
							PropertiesUtil.getProperty("cnfpass_empty_msg"));
				}
			} else {
				return restUtils.processError(
						PropertiesUtil.getProperty("cnfpass_nokey_code"),
						PropertiesUtil.getProperty("cnfpass_nokey_msg"));
			}
			if (password.equalsIgnoreCase(cnfPassword) == false) {
				return restUtils.processError(
						PropertiesUtil.getProperty("pass_nomatch_code"),
						PropertiesUtil.getProperty("pass_nomatch_msg"));
			}
			res = dho.signUp(userName, password);
			if (res == null) {
				return restUtils.processError(
						PropertiesUtil.getProperty("user_added_code"),
						PropertiesUtil.getProperty("user_added_msg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public String Login(JSONObject dataObj, String serviceType,
			String functionType) {
		String res = null;
		String userName = null;
		String password = null;
		try {
			if (dataObj.containsKey(GlobalTags.USER_NAME_TAG)) {
				userName = dataObj.getString(GlobalTags.USER_NAME_TAG);
				if (restUtils.isEmpty(userName) == false) {
					return restUtils.processError(
							PropertiesUtil.getProperty("username_empty_code"),
							PropertiesUtil.getProperty("username_empty_msg"));
				}
			} else {
				return restUtils.processError(
						PropertiesUtil.getProperty("username_key_code"),
						PropertiesUtil.getProperty("username_key_msg"));
			}
			if (dataObj.containsKey(GlobalTags.PASSWORD_TAG)) {
				password = dataObj.getString(GlobalTags.PASSWORD_TAG);
				if (restUtils.isEmpty(password) == false) {
					return restUtils.processError(
							PropertiesUtil.getProperty("pass_empty_code"),
							PropertiesUtil.getProperty("pass_empty_msg"));
				}
			} else {
				return restUtils.processError(
						PropertiesUtil.getProperty("pass_nokey_code"),
						PropertiesUtil.getProperty("pass_nokey_msg"));
			}
			Connection con = dho.getConnection();
			JSONObject resObj = dho.getUserList(con, userName, password);
			if (resObj != null) {
				return restUtils.processSucessForModules(serviceType,
						functionType, "userlist", resObj);
			} else {
				return restUtils.processError(
						PropertiesUtil.getProperty("auth_failed_code"),
						PropertiesUtil.getProperty("auth_failed_msg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public String addTask(JSONObject dataObj, String serviceType,
			String functionType) {
		String res = null;
		String taskName = null;
		String date = null;
		String startTime = null;
		String endTime = null;
		int userId = -1;
		try {
			if (dataObj.containsKey(GlobalTags.USER_ID_TAG)) {
				userId = dataObj.getInt(GlobalTags.USER_ID_TAG);
				if (userId <= 0) {
					return restUtils.processError(
							PropertiesUtil.getProperty("invalid_userid_code"),
							PropertiesUtil.getProperty("invalid_userid_msg"));
				}
			} else {
				return restUtils.processError(
						PropertiesUtil.getProperty("key_userid_code"),
						PropertiesUtil.getProperty("key_userid_msg"));
			}
			if (dataObj.containsKey(GlobalTags.TASK_TAG)) {
				taskName = dataObj.getString(GlobalTags.TASK_TAG);
				if (restUtils.isEmpty(taskName) == false) {
					return restUtils.processError(
							PropertiesUtil.getProperty("task_empty_code"),
							PropertiesUtil.getProperty("task_empty_code"));
				}
			} else {
				return restUtils.processError(
						PropertiesUtil.getProperty("task_nokey_code"),
						PropertiesUtil.getProperty("task_nokey_msg"));
			}
			if (dataObj.containsKey(GlobalTags.DATE_TAG)) {
				date = dataObj.getString(GlobalTags.DATE_TAG);
				if (restUtils.isEmpty(date) == false) {
					return restUtils.processError(
							PropertiesUtil.getProperty("date_empty_code"),
							PropertiesUtil.getProperty("date_empty_msg"));
				}
			} else {
				return restUtils.processError(
						PropertiesUtil.getProperty("date_key_code"),
						PropertiesUtil.getProperty("date_key_msg"));
			}
			if (dataObj.containsKey(GlobalTags.START_TIME_TAG)) {
				startTime = dataObj.getString(GlobalTags.START_TIME_TAG);
				if (restUtils.isEmpty(startTime) == false) {
					return restUtils.processError(
							PropertiesUtil.getProperty("starttime_empty_code"),
							PropertiesUtil.getProperty("starttime_empty_msg"));
				}
			} else {
				return restUtils.processError(
						PropertiesUtil.getProperty("starttime_key_code"),
						PropertiesUtil.getProperty("starttime_key_msg"));
			}
			if (dataObj.containsKey(GlobalTags.END_TIME_TAG)) {
				endTime = dataObj.getString(GlobalTags.END_TIME_TAG);
				if (restUtils.isEmpty(endTime) == false) {
					return restUtils.processError(
							PropertiesUtil.getProperty("endtime_empty_code"),
							PropertiesUtil.getProperty("endtime_empty_msg"));
				}
			} else {
				return restUtils.processError(
						PropertiesUtil.getProperty("endtime_key_code"),
						PropertiesUtil.getProperty("endtime_key_msg"));
			}
			if ((restUtils.StringDateToDate(startTime).after(restUtils
					.StringDateToDate(endTime)))) {
				return restUtils.processError(
						PropertiesUtil.getProperty("invaliddate_code"),
						PropertiesUtil.getProperty("invaliddate_msg"));
			}
			Connection con = dho.getConnection();
			boolean validTaskOnName = dho.validateForTaskNameExist(con,
					taskName, startTime, endTime, date);
			boolean validTaskOnStartTime = dho.validateForTaskDateExist(con,
					startTime, endTime, date);
			if (validTaskOnName) {
				if (validTaskOnStartTime) {
					dho.addTask(con, taskName, startTime, endTime, date, userId);
					dho.closeConnection(con);
					return restUtils.processOnlySucess(serviceType,
							functionType);
				} else {
					return restUtils.processError(
							PropertiesUtil.getProperty("tasktime_exist_code"),
							PropertiesUtil.getProperty("tasktime_exist_msg"));
				}
			} else {
				return restUtils.processError(
						PropertiesUtil.getProperty("task_exist_code"),
						PropertiesUtil.getProperty("task_exist_msg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public String listTask(JSONObject dataObj, String serviceType,
			String functionType) {
		String res = null;
		String startTime = null;
		String endTime = null;
		int userId = -1;
		try {
			if (dataObj.containsKey(GlobalTags.USER_ID_TAG)) {
				userId = dataObj.getInt(GlobalTags.USER_ID_TAG);
				if (userId <= 0) {
					return restUtils.processError(
							PropertiesUtil.getProperty("invalid_userid_code"),
							PropertiesUtil.getProperty("invalid_userid_msg"));
				}
			} else {
				return restUtils.processError(
						PropertiesUtil.getProperty("key_userid_code"),
						PropertiesUtil.getProperty("key_userid_msg"));
			}
			String startTimeQry = "";
			if (dataObj.containsKey(GlobalTags.START_TIME_TAG)) {
				startTime = dataObj.getString(GlobalTags.START_TIME_TAG);
				if (restUtils.isEmpty(startTime)) {
					startTimeQry = " and starttime>='" + startTime + "'";
				}
			}
			String endTimeQry = "";
			if (dataObj.containsKey(GlobalTags.END_TIME_TAG)) {
				endTime = dataObj.getString(GlobalTags.END_TIME_TAG);
				if (restUtils.isEmpty(endTime)) {
					endTimeQry = " and endtime<='" + endTime + "'";
				}
			}
			String statusQry = "";
			if (dataObj.containsKey(GlobalTags.STATUS_TAG)) {
				String status = dataObj.getString(GlobalTags.STATUS_TAG);
				if (restUtils.isEmpty(status)) {
					if (status.equalsIgnoreCase(NEW_STR)) {
						statusQry = " and status=" + NEW;
					} else if (status.equalsIgnoreCase(COMPLETED_STR)) {
						statusQry = " and status=" + COMPLETED;
					} else if (status.equalsIgnoreCase(PROGRESS_STR)) {
						statusQry = " and status=" + PROGRESS;
					}
				}
			}
			String listTaskQuery = " select * from task where userid ="
					+ userId + startTimeQry + endTimeQry + statusQry;
			System.out.println("Listing Task Query Is : " + listTaskQuery);
			Connection con = dho.getConnection();
			JSONArray taskArr = dho.getTaskList(con, listTaskQuery);
			dho.closeConnection(con);
			if (taskArr != null && taskArr.size() > 0) {
				return restUtils.processSucess(serviceType, functionType,
						"tasklist", taskArr);
			} else {
				return restUtils.processError(
						PropertiesUtil.getProperty("no_task_code"),
						PropertiesUtil.getProperty("no_task_msg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public String editTask(JSONObject dataObj, String serviceType,
			String functionType) {
		String res = null;
		int taskId = -1;
		String status = null;
		int status_value = 0;
		try {
			if (dataObj.containsKey(GlobalTags.TASK_ID_TAG)) {
				taskId = dataObj.getInt(GlobalTags.TASK_ID_TAG);
				if (taskId <= 0) {
					return restUtils.processError(
							PropertiesUtil.getProperty("task_id_invalid_code"),
							PropertiesUtil.getProperty("task_id_invalid_msg"));
				}
			} else {
				return restUtils.processError(
						PropertiesUtil.getProperty("task_id_key_code"),
						PropertiesUtil.getProperty("task_id_key_msg"));
			}
			if (dataObj.containsKey(GlobalTags.STATUS_TAG)) {
				status = dataObj.getString(GlobalTags.STATUS_TAG);
				if (restUtils.isEmpty(status)) {
					if (status.equalsIgnoreCase(NEW_STR)) {
						status_value = NEW;
					} else if (status.equalsIgnoreCase(COMPLETED_STR)) {
						status_value = COMPLETED;
					} else if (status.equalsIgnoreCase(PROGRESS_STR)) {
						status_value = PROGRESS;
					}
				}
			}
			Connection con = dho.getConnection();
			String editTaskQry = "update task set status=" + status_value
					+ " where id=" + taskId;
			System.out.println("Edit Task Query : " + editTaskQry);
			dho.updateTask(con, editTaskQry);
			dho.closeConnection(con);
			return restUtils.processOnlySucess(serviceType, functionType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public String deleteTask(JSONObject dataObj, String serviceType,
			String functionType) {
		String res = null;
		int taskId = -1;
		boolean delete = true;
		try {
			if (dataObj.containsKey(GlobalTags.TASK_ID_TAG)) {
				taskId = dataObj.getInt(GlobalTags.TASK_ID_TAG);
				if (taskId <= 0) {
					return restUtils.processError(
							PropertiesUtil.getProperty("task_id_invalid_code"),
							PropertiesUtil.getProperty("task_id_invalid_msg"));
				}
			} else {
				return restUtils.processError(
						PropertiesUtil.getProperty("task_id_key_code"),
						PropertiesUtil.getProperty("task_id_key_msg"));
			}
			Connection con = dho.getConnection();
			delete = dho.canDeleteTask(con, taskId);
			if (delete == false) {
				return restUtils.processError(
						PropertiesUtil.getProperty("task_delete_code"),
						PropertiesUtil.getProperty("task_delete_msg"));
			} else {
				String deleteTask = "delete from task where id=" + taskId;
				System.out.println("deleteTask  Query : " + deleteTask);
				dho.updateTask(con, deleteTask);
				dho.closeConnection(con);
				return restUtils.processOnlySucess(serviceType, functionType);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}
