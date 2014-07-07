package com.murerz.repoz.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.murerz.repoz.web.fs.FileSystem;
import com.murerz.repoz.web.fs.FileSystemFactory;
import com.murerz.repoz.web.fs.StreamRepozFile;
import com.murerz.repoz.web.meta.Config;

public class AccessLogger {

	private static final Object MUTEX = new Object();

	private static AccessLogger me = null;

	private File file;

	public static AccessLogger me() {
		if (me == null) {
			synchronized (MUTEX) {
				if (me == null) {
					AccessLogger logger = new AccessLogger();
					logger.prepare();
					me = logger;
				}
			}
		}
		return me;
	}

	private void prepare() {
		File file;
		try {
			String filename = Config.me().reqProperty("repoz.access.log.file");
			file = new File(filename);
			file = file.getAbsoluteFile();
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			if (!file.exists()) {
				throw new RuntimeException("access file ccess was not created: " + file);
			}
			if (!file.canWrite()) {
				throw new RuntimeException("we cannot write access file: " + file);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.file = file;
	}

	public void log(List<Object> list, Exception exp) {
		list = new ArrayList<Object>(list);
		String str = Util.getStack(exp);
		list.add(str);
		String json = FlexJson.instance().format(list);
		write(json, exp);
	}

	private void write(String json, Exception exp) {
		synchronized (MUTEX) {
			Writer out = null;
			try {
				out = new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");
				out.write(json + "\n");
			} catch (IOException e) {
				throw new RuntimeException("error", e);
			} finally {
				Util.close(out);
			}
			int max = Config.me().reqPropertyInt("repoz.access.log.max");
			if (file.length() >= max) {
				File gz = Util.gzip(file);
				uploadLog(gz);
			}
		}
	}

	private void uploadLog(File file) {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			StreamRepozFile repoz = new StreamRepozFile();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			String util = formatter.format(new Date());
			repoz.setCharset("UTF-8").setMediaType("text/plain").setPath("/.repoz/logs/log-" + util + ".log.gz");
			repoz.setIn(in);
			FileSystem fs = FileSystemFactory.create();
			fs.save(repoz);
			file.delete();
			file.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Util.close(in);
		}
	}

}
