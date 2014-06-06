package com.murerz.repoz.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.murerz.repoz.web.meta.AccessManager;
import com.murerz.repoz.web.meta.AccessManagerFactory;
import com.murerz.repoz.web.util.RepozUtil;
import com.murerz.repoz.web.util.ServletUtil;

public class AccessServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPut(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = ServletUtil.param(req, "path");
		String user = ServletUtil.param(req, "user");
		String pass = ServletUtil.param(req, "pass");
		String type = ServletUtil.param(req, "type");

		RepozUtil.validatePath(path);
		RepozUtil.validateUser(user);
		RepozUtil.validatePass(pass);
		RepozUtil.validateAccessType(type);

		AccessManager am = AccessManagerFactory.create();
		am.update(path, user, pass, type);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	}

}
