package com.murerz.repoz.web.util;

import java.util.List;

import flexjson.JSONDeserializer;
import flexjson.JSONException;
import flexjson.JSONSerializer;

public class FlexJson {

	private static FlexJson ME = null;

	protected JSONSerializer serializer;

	private FlexJson() {

	}

	public static FlexJson instance() {
		if (ME == null) {
			synchronized (FlexJson.class) {
				if (ME == null) {
					ME = create();
				}
			}
		}
		return ME;
	}

	private static FlexJson create() {
		FlexJson ret = new FlexJson();
		ret.serializer = new JSONSerializer();
		return ret;
	}

	public <T> T parse(String json, Class<T> type) {
		JSONDeserializer<T> d = createDeserializer();
		T ret;
		try {
			ret = d.deserialize(json, type);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("JSON: " + json + " type:" + type + "\n" + e);
		} catch (JSONException e) {
			throw new RuntimeException("JSON: " + json + " type:" + type + "\n" + e);
		}

		return ret;
	}

	private <T> JSONDeserializer<T> createDeserializer() {
		JSONDeserializer<T> ret = new JSONDeserializer<T>();
		return ret;
	}

	public String format(Object obj) {
		String ret;
		try {
			ret = serializer.serialize(obj);
		} catch (JSONException e) {
			throw new RuntimeException("Object: " + obj + "\n" + e);
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> parseList(String json, Class<T> clazz) {
		JSONDeserializer<T> d = createDeserializer();
		return (List<T>) d.use("values", clazz).deserialize(json);
	}

	public Object parse(String json) {
		JSONDeserializer<Object> d = createDeserializer();
		return d.deserialize(json);
	}
}
