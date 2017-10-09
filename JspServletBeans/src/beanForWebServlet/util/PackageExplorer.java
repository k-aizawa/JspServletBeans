package beanForWebServlet.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PackageExplorer {


	public static List<String> getClassNameListOfPackage(String packageName){
		final String resourceName = packageName.replace('.', '/');

		ClassLoader loader=Thread.currentThread().getContextClassLoader();

		List<String> classList=new ArrayList<String>();
		URL resourceURL=loader.getResource(resourceName);

		File pkgFolder = new File(resourceURL.getFile());

		File[] pkgSubFolder=pkgFolder.listFiles();


		if(pkgFolder.isDirectory())
		for(File file:pkgSubFolder){
			if(file.isDirectory()){
				classList.addAll(getClassNameListOfPackage(packageName+"."+file.getName()));
			}else if(file.isFile()){
				classList.add(packageName+"."+file.getName().substring(0, file.getName().lastIndexOf('.')));
			}
		}


		return classList;
	}
}
