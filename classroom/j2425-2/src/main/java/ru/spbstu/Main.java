package ru.spbstu;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import ru.spbstu.hsai.imgen.components.config.WebConfig;
import ru.spbstu.hsai.imgen.components.user.api.http.UserController;
import ru.spbstu.hsai.imgen.components.user.dao.UserDao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) {
//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        Tomcat tomcat = new Tomcat();

        final Connector connector = new Connector();
        connector.setPort(8081);
        connector.setScheme("http");
        connector.setSecure(false);
        tomcat.setConnector(connector);

        File baseDir = null;
        try {
            baseDir = Files.createTempDirectory("embedded-tomcat").toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Context context = tomcat.addWebapp("", baseDir.getAbsolutePath());


        // Create a Spring application context
        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(WebConfig.class);

        // Create a DispatcherServlet and register it with Tomcat
        DispatcherServlet dispatcherServlet = new DispatcherServlet(appContext);

        Tomcat.addServlet(context, "dispatcherServlet", dispatcherServlet).setLoadOnStartup(1);
        context.addServletMappingDecoded("/*", "dispatcherServlet");

        try {
            tomcat.start();
            UserDao controller = appContext.getBean(UserDao.class);
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
        tomcat.getServer().await();
    }
}