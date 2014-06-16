package com.murerz.repoz.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ErrorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Object code = req.getAttribute("javax.servlet.error.status_code");
		Object msg = req.getAttribute("javax.servlet.error.message");
		Object servlet = req.getAttribute("javax.servlet.error.servlet_name");
		Throwable exp = (Throwable) req.getAttribute("javax.servlet.error.exception");

		if (code != null && "401".equals(code.toString())) {
			resp.addHeader("WWW-Authenticate", "Basic realm=\"repoz\"");
		}

		PrintWriter out = resp.getWriter();
		out.printf("Error code: %s %s (%s)", code, msg, servlet);
		out.println();
		if (exp != null) {
			exp.printStackTrace(out);
		}
		out.println();
	}
}
