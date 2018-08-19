package com.isj.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isj.manager.TaskManager;

public class Tasks extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String flipKartReq = request.getParameter("request");
		System.out.println("IgluLabs Request Is  : " + flipKartReq);
		TaskManager textManager = new TaskManager();
		String surveyResponse = textManager.getIgluLabsResponse(flipKartReq,
				request);
		System.out.println("The response is :" + surveyResponse);
		response.setContentType("Application/json; charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.append(surveyResponse);
	}
}
