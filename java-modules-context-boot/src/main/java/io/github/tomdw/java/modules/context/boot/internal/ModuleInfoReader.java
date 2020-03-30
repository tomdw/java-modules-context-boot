package io.github.tomdw.java.modules.context.boot.internal;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Attribute;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.ClassWriter;
import org.springframework.asm.ModuleVisitor;
import org.springframework.asm.Opcodes;

/**
 * Waiting for fix in https://bugs.openjdk.java.net/browse/JDK-8241770 to be released in jdk.
 * This class forks the patch attached to the issue.
 *
 * Also logged: https://issues.apache.org/jira/browse/SUREFIRE-1765 as it is maven surefire that puts
 * test-classes and other things on the classpath and thus triggers this problem.
 */
public class ModuleInfoReader {

	private static final Map<Module, ModuleInfoReader> moduleInfoReaders = new HashMap<>();

	private final Module module;
	private Class<?> moduleInfoClazz;

	public static ModuleInfoReader of(Module module) {
		if (moduleInfoReaders.get(module) == null) {
			synchronized (moduleInfoReaders) {
				moduleInfoReaders.put(module, new ModuleInfoReader(module));
			}
		}
		return moduleInfoReaders.get(module);
	}

	ModuleInfoReader(Module module) {
		this.module = module;
	}

	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		return moduleInfoClass(module) != null && moduleInfoClass(module).isAnnotationPresent(annotationClass);
	}

	public <ANNOTATION extends Annotation> ANNOTATION  getAnnotation(Class<ANNOTATION> annotationClass) {
		return moduleInfoClass(module) != null ? moduleInfoClass(module).getAnnotation(annotationClass) : null;
	}

	private Class<?> moduleInfoClass(Module module) {
		if (moduleInfoClazz == null) {
			PrivilegedAction<Class<?>> pa = () -> loadModuleInfoClass(module);
			moduleInfoClazz = AccessController.doPrivileged(pa);
		}
		return moduleInfoClazz;
	}

	private Class<?> loadModuleInfoClass(Module module) {
		Class<?> clazz = null;
		try (InputStream in = module.getResourceAsStream("module-info.class")) {
			if (in != null)
				clazz = loadModuleInfoClass(in, module);
		} catch (Exception ignore) { }
		return clazz;
	}

	private Class<?> loadModuleInfoClass(InputStream in, Module module) throws IOException {
		final String MODULE_INFO = "module-info";

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS
				+ ClassWriter.COMPUTE_FRAMES);

		ClassVisitor cv = new ClassVisitor(Opcodes.ASM6, cw) {
			@Override
			public void visit(int version,
							  int access,
							  String name,
							  String signature,
							  String superName,
							  String[] interfaces) {
				cw.visit(version,
						Opcodes.ACC_INTERFACE
								+ Opcodes.ACC_ABSTRACT
								+ Opcodes.ACC_SYNTHETIC,
						MODULE_INFO,
						null,
						"java/lang/Object",
						null);
			}
			@Override
			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
				// keep annotations
				return super.visitAnnotation(desc, visible);
			}
			@Override
			public void visitAttribute(Attribute attr) {
				// drop non-annotation attributes
			}
			@Override
			public ModuleVisitor visitModule(String name, int flags, String version) {
				// drop Module attribute
				return null;
			}
		};

		ClassReader cr = new ClassReader(in);
		cr.accept(cv, 0);
		byte[] bytes = cw.toByteArray();

		ClassLoader cl = new ClassLoader(module.getClassLoader()) {
			@Override
			protected Class<?> findClass(String cn)throws ClassNotFoundException {
				if (cn.equals(MODULE_INFO)) {
					return super.defineClass(cn, bytes, 0, bytes.length);
				} else {
					throw new ClassNotFoundException(cn);
				}
			}
			@Override
			protected Class<?> loadClass(String cn, boolean resolve) throws ClassNotFoundException
			{
				synchronized (getClassLoadingLock(cn)) {
					Class<?> c = findLoadedClass(cn);
					if (c == null) {
						if (cn.equals(MODULE_INFO)) {
							c = findClass(cn);
						} else {
							c = super.loadClass(cn, resolve);
						}
					}
					if (resolve)
						resolveClass(c);
					return c;
				}
			}
		};

		try {
			return cl.loadClass(MODULE_INFO);
		} catch (ClassNotFoundException e) {
			throw new InternalError(e);
		}
	}
}
