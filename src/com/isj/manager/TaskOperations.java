package com.isj.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.isj.utils.PropertiesUtil;
import com.isj.utils.RestUtils;
import com.mysql.jdbc.Driver;

public class TaskOperations {
	RestUtils restUtils = new RestUtils();
	String creteDatabasequery = " create database if not exists iglulabs ";
	String creteUserTablequery = " create table if not exists user(id int primary key,username varchar(255),password varchar(255))";
	String creteTaskTablequery = "create table if not exists task(id int primary key,taskname varchar(255),startdate datetime,endtime datetime,status int,userid int ,foreign key(userid) references user(id))";
	String updateQry = "ALTER TABLE user MODIFY COLUMN id INT NOT NULL AUTO_INCREMENt";
	String alterTAbleQry = "alter table task add column description varchar(255);";
	String signUpQry = "insert into user(username,password) values('%s','%s')";
	String selectUsrQry = "select * from user where username='%s'";
	String addTaskQry = "insert into task (taskname,date,starttime,endtime,status,userid) values('%s','%s','%s','%s',%d,%d)";

	//GET CONNECTION
	public Connection getConnection() throws SQLException {
		Driver driverRef = new Driver();
		Connection con = null;
		try {
			System.out.println("Connection Established");
			String url1 = "jdbc:mysql://localhost:3306/iglulabs";
			String user = "root";
			String password = "password";
			DriverManager.registerDriver(driverRef);
			con = DriverManager.getConnection(url1, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;

	}

	public boolean closeConnection(Connection con) {
		boolean conRes = false;
		try {
			if (con != null) {
				con.close();
				conRes = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
					System.out.println("Connection Closed");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return conRes;
	}

	public String signUp(String userName, String password) throws SQLException {
		String res = null;
		Statement stmt = null;
		Connection con = null;
		try {
			boolean isUsrExist = validateUser(userName, con);
			if (isUsrExist) {
				return restUtils.processError(
						PropertiesUtil.getProperty("username_exist_code"),
						PropertiesUtil.getProperty("username_exist_msg"));
			}
			signUpQry = String.format(signUpQry, userName, password);
			System.out.println("SignUp Qry Is : " + signUpQry);
			con = getConnection();
			stmt = con.createStatement();
			stmt.executeUpdate(signUpQry);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public boolean validateUser(String userName, Connection con)
			throws SQLException {
		Statement stmt = null;
		boolean isUsrExist = false;
		ResultSet rs = null;
		try {
			String query = "select * from user where username='" + userName
					+ "'";
			con = getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isUsrExist;
	}

	public void updateTask(Connection con, String query) throws SQLException {
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public JSONObject getUserList(Connection con, String userName,
			String password) {
		JSONObject resObj = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String query = " select * from user where username='" + userName
					+ "'" + " and password='" + password + "'";
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				resObj = new JSONObject();
				resObj.put("id", rs.getInt("id"));
				resObj.put("username", rs.getString("username"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resObj;
	}

	public JSONObject getTaskForUser(Connection con, String userName,
			String password) {
		JSONObject resObj = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String query = " select * from user where username='" + userName
					+ "'" + " and password='" + password + "'";
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				resObj = new JSONObject();
				resObj.put("id", rs.getInt("id"));
				resObj.put("username", rs.getString("username"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resObj;
	}

	public boolean validateForTaskNameExist(Connection con, String taskName,
			String startTime, String endTime, String date) {
		Statement stmt = null;
		ResultSet rs = null;
		boolean validTask = true;
		try {
			String query = " select * from task where taskname='" + taskName
					+ "'";
			System.out.println("Validate Task Query : " + query);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return validTask = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return validTask;
	}

	public boolean validateForTaskDateExist(Connection con, String startTime,
			String endTime, String date) {
		Statement stmt = null;
		ResultSet rs = null;
		boolean validTask = true;
		try {
			String query = " select * from task where starttime='" + startTime
					+ "'";
			System.out.println("Validate Task Query : " + query);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return validTask = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return validTask;
	}

	public boolean addTask(Connection con, String taskName, String startTime,
			String endTime, String date, int userId) {
		Statement stmt = null;
		boolean validTask = true;
		try {
			String query = String.format(addTaskQry, taskName, date, startTime,
					endTime, 0, userId);
			System.out.println("Add Task Query : " + query);
			stmt = con.createStatement();
			stmt.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return validTask;
	}

	public JSONArray getTaskList(Connection con, String query) {
		Statement stmt = null;
		ResultSet rs = null;
		JSONArray taskArr = new JSONArray();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				JSONObject taskObj = new JSONObject();
				taskObj.put("id", rs.getInt("id"));
				taskObj.put("taskname", rs.getString("taskname"));
				taskObj.put("date", rs.getString("date"));
				taskObj.put("starttime", rs.getString("starttime"));
				taskObj.put("endtime", rs.getString("endtime"));
				String status = getStatus(rs.getInt("status"));
				taskObj.put("status", status);
				taskArr.add(taskObj);
			}
			if (taskArr != null && taskArr.size() > 0) {
				return taskArr;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getStatus(int statusInt) {
		if (statusInt == TaskManager.NEW) {
			return TaskManager.NEW_STR;
		} else if (statusInt == TaskManager.COMPLETED) {
			return TaskManager.COMPLETED_STR;
		} else if (statusInt == TaskManager.PROGRESS) {
			return TaskManager.PROGRESS_STR;
		}
		return null;
	}

	public boolean canDeleteTask(Connection con, int id) {
		Statement stmt = null;
		ResultSet rs = null;
		boolean canDeleteTask = true;
		try {
			String query = " select * from task where id=" + id
					+ " and status=2";
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return canDeleteTask;
	}

}