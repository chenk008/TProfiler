/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;

import org.objectweb.asm.ClassReader;

import com.taobao.profile.exception.ExceptionAsm;
import com.taobao.profile.instrument.ProfTransformer;

/**
 * TProfiler入口
 * 
 * @author luqi
 * @since 2010-6-23
 */
public class Main {

	/**
	 * @param args
	 * @param inst
	 */
	public static void premain(String args, Instrumentation inst) {
		Manager.instance().initialization();
		System.out.println(Arrays.toString(inst.getAllLoadedClasses()));
		inst.addTransformer(new ProfTransformer());
		try {
			inst.redefineClasses(new ClassDefinition(Throwable.class, 
					ExceptionAsm.transformExceptionClass(new ClassReader("java.lang.Throwable")).toByteArray()));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnmodifiableClassException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Manager.instance().startupThread();
	}
}
