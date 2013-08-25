package de.newsarea.homecockpit.connector.facade.registration.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public final class ClassLoaderHelper {
	
	private static Logger log = LoggerFactory.getLogger(ClassLoaderHelper.class);

    private ClassLoaderHelper() { }

	public static Constructor<?> determineFirstConstructor(Class<?> clazz) {
		try {
			for(Constructor<?> constructor : clazz.getConstructors()) {
				return constructor;
			}
		} catch (SecurityException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

    public static Constructor<?> determineConstructorByArgumentTypes(Class<?> clazz, Class<?>[] argumentTypes) {
        try {
            for(Constructor<?> constructor : clazz.getConstructors()) {
                if(isAssignableFrom(constructor, argumentTypes)) {
                    return constructor;
                }
            }
        } catch (SecurityException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private static boolean isAssignableFrom(Constructor<?> constructor, Class<?>[] argumentTypes) {
        Class<?>[] constructorArgTypes = constructor.getParameterTypes();
        if(constructorArgTypes.length != argumentTypes.length) {
            return false;
        }
        // ~
        for(int i=0; i < argumentTypes.length; i++) {
            if(!argumentTypes[i].isAssignableFrom(constructorArgTypes[i])) {
                return false;
            }
        }
        return true;
    }

	public static List<Class<?>> determineClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile().replaceAll("%20", " ")));
		}
		ArrayList<Class<?>> classes = new ArrayList<>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes;
	}

	public static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

	public static Method determineSetterMethod(Class<?> clazz, String name) {
		for(Method method : clazz.getMethods()) {
			if(method.getName().equalsIgnoreCase("set" + name)) {
				return method;
			}
		}
		return null;
	}

}
