package com.murerz.repoz.web;

import java.io.IOException;
import java.math.BigInteger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.murerz.repoz.web.util.ServletUtil;
import com.murerz.repoz.web.util.Util;

public class LimitFilter implements Filter {

	private BigInteger maxLength = new BigInteger("262144000");

	public void init(FilterConfig filterConfig) throws ServletException {
		String len = Util.str(System.getProperty("repoz.post.maxLength"));
		if (len != null) {
			maxLength = new BigInteger(len);
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		filter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	private void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
		try {
			BigInteger len = ServletUtil.headerBigInteger(request, "Content-Length");
			if (len != null && len.compareTo(maxLength) > 0) {
				ServletUtil.sendRequestEntityTooLarge(response, maxLength.toString(), len.toString());
				return;
			}
			chain.doFilter(request, response);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
	}

	public void destroy() {

	}

}
