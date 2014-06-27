package com.murerz.repoz.web;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.murerz.repoz.web.util.ServletUtil;
import com.murerz.repoz.web.util.Util;

public class VersionServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Properties props = Util.properties(getClass().getClassLoader().getResource("repoz.build.properties"));
		if (props == null) {
			ServletUtil.writeText(resp, "#unknown");
			return;
		}
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		props.store(resp.getWriter(), "repoz");
	}

}
