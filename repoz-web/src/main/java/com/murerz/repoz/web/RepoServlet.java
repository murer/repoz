package com.murerz.repoz.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.murerz.repoz.web.fs.FileSystem;
import com.murerz.repoz.web.fs.FileSystemFactory;
import com.murerz.repoz.web.fs.RepozFile;
import com.murerz.repoz.web.fs.StreamRepozFile;
import com.murerz.repoz.web.util.RepozUtil;
import com.murerz.repoz.web.util.ServletUtil;
import com.murerz.repoz.web.util.Util;

public class RepoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		InputStream in = null;
		try {
			String path = RepozUtil.path(req);
			FileSystem fs = FileSystemFactory.create();

			RepozFile file = fs.read(path);
			if (file == null) {
				ServletUtil.sendNotFound(req, resp);
				return;
			}

			String contentType = file.getContentType();
			String charset = file.getCharset();
			if (contentType != null) {
				resp.setContentType(contentType);
			}
			if (charset != null) {
				resp.setCharacterEncoding(charset);
			}

			in = file.getIn();
			OutputStream out = resp.getOutputStream();

			Util.copyAll(in, out);
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
		String path = RepozUtil.path(req);

		FileSystem fs = FileSystemFactory.create();

		String contentType = req.getContentType();
		String charset = req.getCharacterEncoding();
		InputStream in = req.getInputStream();

		RepozFile file = new StreamRepozFile().setIn(in).setPath(path).setContentType(contentType).setCharset(charset);

		fs.save(file);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = RepozUtil.path(req);
		FileSystem fs = FileSystemFactory.create();
		fs.delete(path);
	}

}
