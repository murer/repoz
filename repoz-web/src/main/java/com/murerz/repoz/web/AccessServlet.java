package com.murerz.repoz.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.murerz.repoz.web.command.AccessUpdateCommand;

public class AccessServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		AccessUpdateCommand cmd = new AccessUpdateCommand();
		cmd.setPath(req.getParameter("path"));
		cmd.setUser(req.getParameter("user"));
		cmd.setPass(req.getParameter("pass"));
		cmd.save();
	}

}
