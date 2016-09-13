package com.taobao.profile.exception;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionAsm {

	public static final Logger LOG = LoggerFactory.getLogger(ExceptionAsm.class);

	public static void main(String[] args) throws IOException {
		ClassReader cr = new ClassReader("java.lang.Throwable");
		ClassWriter cw = new ClassWriter(0);

		ClassPrinter cp = new ClassPrinter(cw);
		cr.accept(cp, 0);


	}
	
	public static ClassWriter transformExceptionClass(ClassReader cr) throws IOException{

		ClassWriter cw = new ClassWriter(0);


		cr.accept(new ClassAdapter(cw) {
			@Override
			public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
				cv.visit(version, access, name, signature, superName, interfaces);
			}

			@Override
			public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
				if ("printStackTrace".equals(name) && "()V".equals(desc)) {
					// 这里只是简单的比较了方法名字，其实还需要比较方法参数，参数信息在desc中
					return cv.visitMethod(access, name + "$1", desc, signature, exceptions);
				}
				return cv.visitMethod(access, name, desc, signature, exceptions);
			}

		}, 0);

		// value先为null，能传值的情况只有Integer, Float, Long, Double , String
//		cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "OUT_LOG", "Lorg/slf4j/Logger;", null, null).visitEnd();

		// 我们接着需要增加一个printStackTrace方法
		 MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC,
		 "printStackTrace", "()V", null, null);
		 // 开始增加代码
		 mv.visitCode();
		 // 接下来，我们需要把新的execute方法的内容，增加到这个方法中
		 mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
		 "Ljava/io/PrintStream;");
		 mv.visitLdcInsn("Before execute");
		 mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
		 "println", "(Ljava/lang/String;)V");
		
		 // load this指针
		 mv.visitVarInsn(Opcodes.ALOAD, 0);
		 mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Throwable",
		 "printStackTrace$1", "()V");
		
		 mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
		 "Ljava/io/PrintStream;");
		 mv.visitLdcInsn("End execute");
		 mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
		 "println", "(Ljava/lang/String;)V");
		 // mv.visitInsn(Opcodes.RETURN);
		 mv.visitMaxs(0, 0); //
//		 这个地方，最大的操作数栈和最大的本地变量的空间，是自动计算的，是因为构造ClassWriter的时候使用了ClassWriter.COMPUTE_MAXS
		 mv.visitEnd();

		 return cw;
	}
	
	
	public static void write(ClassWriter cw) throws Exception{
		File file = new File("java/lang/Throwable.class");
		if (file.exists()) {
			file.delete();
		}
		// file.deleteOnExit();
		boolean b = file.createNewFile();
		System.out.println(b);
		RandomAccessFile aFile = new RandomAccessFile(file, "rw");
		FileChannel inChannel = aFile.getChannel();
		inChannel.write(ByteBuffer.wrap(cw.toByteArray()));
		inChannel.force(true);
		inChannel.close();
		aFile.close();
	}

	public static class ClassPrinter extends ClassAdapter {

		public ClassPrinter(ClassVisitor arg0) {
			super(arg0);
		}

		// public void visit(int version, int access, String name, String
		// signature, String superName, String[] interfaces) {
		// System.out.println(name + " extends " + superName + " {");
		// }

		// public void visitSource(String source, String debug) {
		// }
		//
		// public void visitOuterClass(String owner, String name, String desc) {
		// }
		//
		// public void visitAttribute(Attribute attr) {
		// }
		//
		// public void visitInnerClass(String name, String outerName, String
		// innerName, int access) {
		// }

		public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
			System.out.println("    " + desc + " " + name);
			return super.visitField(access, name, desc, signature, value);
		}

		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			if (name.equals("printStackTrace") && desc.equals("()V")) {

			}
			System.out.println(" " + name + desc);
			return super.visitMethod(access, name, desc, signature, exceptions);
		}

		// public void visitEnd() {
		// System.out.println("}");
		// }
	}

}
