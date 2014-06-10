package com.murerz.repoz.web;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.murerz.repoz.web.fs.FileSystem;
import com.murerz.repoz.web.fs.FileSystemFactory;
import com.murerz.repoz.web.fs.RepozFile;
import com.murerz.repoz.web.meta.AccessManager;
import com.murerz.repoz.web.meta.AccessManagerFactory;
import com.murerz.repoz.web.meta.User;
import com.murerz.repoz.web.util.RepozUtil;
import com.murerz.repoz.web.util.ServletUtil;
import com.murerz.repoz.web.util.Util;

public class AccessServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String repo = ServletUtil.param(req, "path");
		RepozUtil.validatePath(repo);
		RepozUtil.checkFalsePattern(repo, ".+/$");
		RepozUtil.checkPattern(repo, "^/[^/]+$");

		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");

		FileSystem fs = FileSystemFactory.create();
		RepozFile file = fs.read(repo + RepozUtil.ACCESS);
		if (file == null) {
			return;
		}

		InputStream in = file.getIn();
		try {
			Util.copyAll(in, resp.getOutputStream());
		} finally {
			Util.close(in);
		}
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

		RepozUtil.checkPattern(path, "^/[^/]+$");

		AccessManager am = AccessManagerFactory.create();
		am.save(user);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String repo = ServletUtil.param(req, "path");
		String username = ServletUtil.param(req, "user");

		RepozUtil.validatePath(repo);
		RepozUtil.checkFalsePattern(repo, ".+/$");
		RepozUtil.checkPattern(repo, "^/[^/]+$");
		RepozUtil.validateUser(username);

		AccessManager am = AccessManagerFactory.create();
		am.delete(repo, username);
	}

}
