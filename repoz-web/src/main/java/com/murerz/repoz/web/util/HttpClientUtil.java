package com.murerz.repoz.web.util;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {

	public static String textPost(String url, String... params) {
		try {
			Request req = Request.Post(url);

			Form form = Form.form();
			for (int i = 0; i < params.length; i += 2) {
				String name = params[i];
				String value = params[i + 1];
				form.add(name, value);
			}
			List<NameValuePair> list = form.build();
			req.bodyForm(list);

			HttpResponse resp = req.execute().returnResponse();
			HttpEntity entity = resp.getEntity();
			String responseString = EntityUtils.toString(entity);
			return responseString;
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T jsonPost(Class<T> clazz, String url, String... params) {
		String str = textPost(url, params);
		return FlexJson.instance().parse(str, clazz);
	}

}
