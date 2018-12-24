package Services;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GlobalBean implements ServletContextListener {
	private static ApplicationContext applicationContext=null;
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("hello");
		GlobalBean.setApplicationContext(new ClassPathXmlApplicationContext("globalbean.xml"));
	}
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	public static void setApplicationContext(ApplicationContext applicationContext) {
		GlobalBean.applicationContext = applicationContext;
	}
}
