package com.murerz.repoz.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.murerz.repoz.web.meta.AccessManager;
import com.murerz.repoz.web.meta.AccessManagerFactory;
import com.murerz.repoz.web.meta.User;
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
		String username = ServletUtil.param(req, "user");
		String pass = ServletUtil.param(req, "pass");
		String type = ServletUtil.param(req, "type");

		User user = new User().setPath(path).setUser(username).setType(type).setPass(pass);
		user.validate();

		AccessManager am = AccessManagerFactory.create();
		am.save(user);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	}

}
