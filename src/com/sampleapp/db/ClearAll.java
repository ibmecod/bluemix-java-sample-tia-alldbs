/*-------------------------------------------------------------------*/
/* Copyright IBM Corp. 2013 All Rights Reserved                      */
/*-------------------------------------------------------------------*/

package com.sampleapp.db;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ClearAll
 */
public class ClearAll extends DBAccessServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		daoFactory.clearAll();
	
		List<Influencer> records = daoFactory.getAll();
		request.setAttribute("records", records);
		request.setAttribute("totinf", records.size());
		request.getRequestDispatcher("/displayall.jsp").forward(request, response);
	}
}
