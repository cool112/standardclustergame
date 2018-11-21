package com.garow.proto.utils;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 类工具
 * @author      gjs
 * @version 	1.0
 */
public class ClassUtils
{


	private static final String CLASS_EXT = ".class";
	private static final String JAR_FILE_EXT = ".jar";
	private static final String ERROR_MESSAGE = "packageName can't be null";
	private static final String DOT = ".";
	private static final String ZIP_SLASH = "/";
	private static final String EMPTY = "";
	private static final ClassFilter NULL_CLASS_FILTER = null;
	
	/**
	 * 默认文件过滤器，过滤掉不需要的文件 
	 */
	private static FileFilter fileFilter = new FileFilter()
	{
		@Override
		public boolean accept(File pathname)
		{
			return isClass(pathname.getName()) || isDirectory(pathname) || isJarFile(pathname.getName());
		}
	};

	/**
	 * 如果packageName为空，就抛出空指针异常。</br>
	 * (本工具类有一个bug，如果扫描文件时需要一个包路径为截取字符串的条件，现在还没有修复,所以加上该条件)
	 * 
	 * @param packageName
	 */
	private static void ckeckNullPackageName(String packageName)
	{
		if (packageName == null || packageName.trim().length() == 0)
			throw new IllegalArgumentException(ERROR_MESSAGE);
	}

	/**
	 * 改变 com -> com. 避免在比较的时候把比如 completeTestSuite.class类扫描进去，如果没有"." </br>
	 * 那class里面com开头的class类也会被扫描进去,其实名称后面或前面需要一个 ".",来添加包的特征
	 * 
	 * @param packageName
	 * @return
	 */
	private static String getWellFormedPackageName(String packageName)
	{
		return packageName.lastIndexOf(DOT) != packageName.length() - 1 ? packageName + DOT : packageName;
	}

	/**
	 * 扫面包路径下满足class过滤器条件的所有class文件，
	 * 如果包路径为 com.abs + A.class 但是输入 abs 会产生classNotFoundException
	 * 因为className 应该为 com.abs.A 现在却成为
	 * abs.A,此工具类对该异常进行忽略处理,有可能是一个不完善的地方，以后需要进行修改
	 * 
	 * @param packageName
	 *            包路径 com | com. | com.abs | com.abs.
	 * @param classFilter class过滤器，过滤掉不需要的class
	 * @return
	 */
	public static <T> List<Class<T>> scanPackage(String packageName, ClassFilter classFilter)
	{
		return scanPackage(packageName, classFilter, false);
	}
	/**
	 * 扫描包路径下所有class文件,是否需要包含jre
	 * @param packageName
	 * @param classFilter
	 * @param needJre
	 * @return
	 */
	public static <T> List<Class<T>> scanPackage(String packageName,ClassFilter classFilter,boolean needJre)
	{
		ckeckNullPackageName(packageName);
		
		
		final List<Class<T>> classes = new ArrayList<Class<T>>();
		for (String classPath : needJre?getClassPathArrayIncludeJre():getClassPathArray())
		{
			fillClasses(new File(classPath), getWellFormedPackageName(packageName), classFilter, classes);
		}
		return classes;
	}

	/**
	 * 扫描包路径下所有class文件(除jre)
	 * 
	 * @param packageName 包路径 com | com. | com.abs | com.abs.
	 * @return
	 */
	public static <T> List<Class<T>> scanPackage(String packageName)
	{
		return scanPackage(packageName, NULL_CLASS_FILTER);
	}
	
	

	/**
	 * 填充满足条件的class 填充到 classes
	 * 
	 * @param file 类路径下的文件
	 * @param packageName 需要扫面的包名
	 * @param classFilter class过滤器
	 * @param classes List 集合
	 */
	private static <T> void fillClasses(File file, String packageName, ClassFilter classFilter,  List<Class<T>> classes)
	{
		if (isDirectory(file))
		{
			processDirectory(file, packageName, classFilter, classes);
		} else if (isClass(file.getName()))
		{
			processClassFile(file, packageName, classFilter, classes);
		} else if (isJarFile(file.getName()))
		{
			processJarFile(file, packageName, classFilter, classes);
		}
	}

	/**
	 * 处理如果为目录的情况,需要递归调用 fillClasses方法
	 * 
	 * @param directory
	 * @param packageName
	 * @param classFilter
	 * @param classes
	 */
	private static <T> void processDirectory(File directory, String packageName, ClassFilter classFilter,
			List<Class<T>> classes)
	{
		for (File file : directory.listFiles(fileFilter))
		{
			fillClasses(file, packageName, classFilter, classes);
		}
	}

	/**
	 * 处理为class文件的情况,填充满足条件的class 到 classes
	 * 
	 * @param file
	 * @param packageName
	 * @param classFilter
	 * @param classes
	 */
	private static <T> void processClassFile(File file, String packageName, ClassFilter classFilter, List<Class<T>> classes)
	{
		final String filePathWithDot = file.getAbsolutePath().replace(File.separator, DOT);
		int subIndex = -1;
		if ((subIndex = filePathWithDot.indexOf(packageName)) != -1)
		{
			final String className = filePathWithDot.substring(subIndex).replace(CLASS_EXT, EMPTY);
			fillClass(className, packageName, classes, classFilter);
		}
	}

	/**
	 * 处理为jar文件的情况，填充满足条件的class 到 classes
	 * 
	 * @param file
	 * @param packageName
	 * @param classFilter
	 * @param classes
	 */
	private static <T> void processJarFile(File file, String packageName, ClassFilter classFilter, List<Class<T>> classes)
	{
		try
		{
			for (ZipEntry entry : Collections.list(new ZipFile(file).entries()))
			{
				if (isClass(entry.getName()))
				{
					final String className = entry.getName().replace(ZIP_SLASH, DOT).replace(CLASS_EXT, EMPTY);
					fillClass(className, packageName, classes, classFilter);
				}
			}
		} catch (Throwable ex)
		{
			// ignore this ex
		}
	}

	/**
	 * 填充class 到 classes
	 * 
	 * @param className
	 * @param packageName
	 * @param classes
	 * @param classFilter
	 */
	@SuppressWarnings("unchecked")
	private static <T> void fillClass(String className, String packageName, List<Class<T>> classes, ClassFilter classFilter)
	{
		if (checkClassName(className, packageName))
		{
			try
			{
				final Class<T> clazz = (Class<T>) Class.forName(className, Boolean.FALSE, ClassUtils.class.getClassLoader());
				if (checkClassFilter(classFilter, clazz))
				{
					classes.add(clazz);
				}
			} catch (Throwable ex)
			{
				// ignore this ex
			}
		}
	}
	/**
	 * 不包括 jre
	 */
	private static String[] getClassPathArray()
	{
		
		return System.getProperty("java.class.path").split(System.getProperty("path.separator"));
		
	}
	
	/**
	 * 包括jre
	 * @return
	 */
	private static String[] getClassPathArrayIncludeJre()
	{
		return System.getProperty("java.class.path").
				  concat(System.getProperty("path.separator")).
				  concat(System.getProperty("java.home")).
				  split(System.getProperty("path.separator"));
	}

	private static boolean checkClassName(String className, String packageName)
	{
		return className.indexOf(packageName) == 0;
	}

	@SuppressWarnings("rawtypes")
	private static boolean checkClassFilter(ClassFilter classFilter, Class clazz)
	{
		return classFilter == NULL_CLASS_FILTER || classFilter.accept(clazz);
	}

	private static boolean isClass(String fileName)
	{
		return fileName.endsWith(CLASS_EXT);
	}

	private static boolean isDirectory(File file)
	{
		return file.isDirectory();
	}

	private static boolean isJarFile(String fileName)
	{
		return fileName.endsWith(JAR_FILE_EXT);
	}

	
	/**
	 * 检查A类和B类是否为同一个类:<br>
	 * <ul>
	 * <li>如果A类和B类是同一个类，则返回true。</li>
	 * <li>如果A类是B类的子类，则返回true。</li>
	 * <li>如果A类是B的实现类，则返回true。</li>
	 * </ul>
	 * @param a class
	 * @param b class
	 * @return 两个类是同一类或则子父类关系，则返回true。
	 */
	public static boolean isSubclass(Class<?> a, Class<?> b)
	{
		if(a == b)
		{
			return true;
		}
		if(a == null || b == null)
		{
			return false;
		}
		for(Class<?> x = a; x != null; x = x.getSuperclass())
		{
			if(x == b)
			{
				return true;
			}
			if(b.isInterface())
			{
				Class<?>[] interfaces = x.getInterfaces();
				for(Class<?> anInterface : interfaces)
				{
					if(isSubclass(anInterface, b))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 检查这个类是否在指定的包中
	 * @param clazz 需要检查的类
	 * @param packageName 包名
	 * @return 
	 * 	true 在这个包中
	 * 	false 不在
	 */
	public static boolean isPackageMember(Class<?> clazz, String packageName)
	{
		return isPackageMember(clazz.getName(), packageName);
	}

	/**
	 * 检查指定的类名是否在指定的包中
	 * @param className	类名
	 * @param packageName 包名
	 * @return 
	 * 	true 类在这个包中
	 * 	false 不在
	 */
	public static boolean isPackageMember(String className, String packageName)
	{
		if(!className.contains("."))
		{
			return packageName == null || packageName.isEmpty();
		}
		else
		{
			String classPackage = className.substring(0, className.lastIndexOf('.'));
			return packageName.equals(classPackage);
		}
	}
	
	/**
	 * 判断是否抽象类
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isAbstract(Class clazz)
	{
		int modifiers = clazz.getModifiers();
		return Modifier.isAbstract(modifiers);
	}
	/**
	 * 类过滤器
	 * @author 		seg
	 */
	public static interface ClassFilter
	{
		@SuppressWarnings("rawtypes")
		boolean accept(Class clazz);
	}
}
