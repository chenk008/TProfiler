package com.taobao.profile.test;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Test {

	private static final Logger LOG = LoggerFactory.getLogger(Test.class);

	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(Math.pow(Math.E, -0.2));
		
		System.out.println("1.2.17".compareTo("1.2.3"));


		tss();
	}

	public static void tss() {
		try {
			tss1();
		} catch (Exception e) {
			e.printStackTrace();
			// LOG.error("e",e);
		}
	}

	public static void tss1() {
		throw new RuntimeException("11");
	}
}
