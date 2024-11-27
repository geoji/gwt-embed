package com.geoji.shorts;

import jakarta.servlet.ServletException;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main class for the application running in embedded tomcat
 * @author George Paret
 */
public class Main {

	/**
	 * Starts embedded tomcat and runs the app in embedded tomcat
	 * @param args the first argument args[0] should be the path to app installation directory
	 * @throws ServletException
	 * @throws LifecycleException
	 */
	public static void main(String[] args) throws LifecycleException {
		if (args.length == 0) {
			throw new IllegalArgumentException("Path to installation directory should be specified");
		}

		Tomcat tomcat = new Tomcat();

		Path installDir = Paths.get(args[0]);
		Path baseDir = installDir.resolve("base");

		tomcat.setBaseDir(baseDir.toString());
		int port = 8081;

		try {
			port = Integer.parseInt(System.getenv("PORT"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		tomcat.setPort(port);

		// Calling get connector will setup a default connector so that tomcat will listen on the specified port
		tomcat.getConnector();

		Path warDir = installDir.resolve("war");
		Context context = tomcat.addWebapp("", warDir.toString());

		// Tomcat scans all the jars at startup for configuration files. Since we do all our configuration via web.xml
		// file, this scanning is unnecessary and should be disabled to improve startup time.
		// https://tomcat.apache.org/tomcat-9.0-doc/config/jar-scanner.html
		context.getJarScanner().setJarScanFilter((jarScanType, jarName) -> false);

		tomcat.start();
		tomcat.getServer().await();
	}
}

