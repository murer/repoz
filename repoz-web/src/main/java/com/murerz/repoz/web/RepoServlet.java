package com.murerz.repoz.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.murerz.repoz.web.fs.FileSystem;
import com.murerz.repoz.web.fs.FileSystemFactory;
import com.murerz.repoz.web.fs.MetaFile;
import com.murerz.repoz.web.fs.RepozFile;
import com.murerz.repoz.web.fs.StreamRepozFile;
import com.murerz.repoz.web.util.CTX;
import com.murerz.repoz.web.util.RepozUtil;
import com.murerz.repoz.web.util.ServletUtil;
import com.murerz.repoz.web.util.Util;

public class RepoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String path = RepozUtil.path(req);
		boolean list = ServletUtil.paramBoolean(req, "l");
		if (list) {
			ServletUtil.sendMethodNotAllowed(req, resp);
			return;
		}

		FileSystem fs = FileSystemFactory.create();

		MetaFile file = fs.head(path);
		if (file == null) {
			ServletUtil.sendNotFound(req, resp);
			return;
		}

		String contentType = file.getMediaType();
		String charset = file.getCharset();
		String length = file.getLength();
		if (contentType != null) {
			resp.setContentType(contentType);
		}
		if (charset != null) {
			resp.setCharacterEncoding(charset);
		}
		if (length != null) {
			ServletUtil.setContentLength(resp, length);
		}

		Map<String, String> params = file.getParams();
		ServletUtil.setHeaders(resp, "X-Repoz-Param-", params);

		resp.flushBuffer();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		InputStream in = null;
		try {
			String path = RepozUtil.path(req);
			boolean list = ServletUtil.paramBoolean(req, "l");
			if (list) {
				list(req, resp);
				return;
			}

			FileSystem fs = FileSystemFactory.create();

			RepozFile file = fs.read(path);
			if (file == null) {
				ServletUtil.sendNotFound(req, resp);
				return;
			}
			in = file.getIn();

			String contentType = file.getMediaType();
			String charset = file.getCharset();
			String length = file.getLength();
			if (contentType != null) {
				resp.setContentType(contentType);
			}
			if (charset != null) {
				resp.setCharacterEncoding(charset);
			}
			if (length != null) {
				ServletUtil.setContentLength(resp, length);
			}

			Map<String, String> params = file.getParams();
			ServletUtil.setHeaders(resp, "X-Repoz-Param-", params);

			OutputStream out = resp.getOutputStream();

			Util.copyAll(in, out);
		} finally {
			Util.close(in);
		}
	}

	private void list(HttpServletRequest req, HttpServletResponse resp) {
		String path = RepozUtil.path(req);
		boolean recursively = ServletUtil.paramBoolean(req, "r");
		FileSystem fs = FileSystemFactory.create();
		if (recursively) {
			Set<String> childs = fs.listRecursively(path);
			ServletUtil.writeTextLines(resp, childs);
			return;
		}
		Set<String> childs = fs.list(path);
		ServletUtil.writeTextLines(resp, childs);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPut(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String path = RepozUtil.path(req);

		FileSystem fs = FileSystemFactory.create();

		String mediaType = ServletUtil.mediaType(req);
		if (mediaType == null) {
			mediaType = RepozUtil.mediaType(path);
		}
		String charset = req.getCharacterEncoding();
		InputStream in = req.getInputStream();

		RepozFile file = (RepozFile) new StreamRepozFile().setIn(in)
				.setPath(path).setMediaType(mediaType).setCharset(charset);

		Map<String, String> params = ServletUtil.headers(req, "X-Repoz-Param-");
		file.setParams(params);
		file.setParam("username", CTX.getAsString("username"));

		fs.save(file);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String path = RepozUtil.path(req);
		FileSystem fs = FileSystemFactory.create();
		fs.delete(path);
	}

}
