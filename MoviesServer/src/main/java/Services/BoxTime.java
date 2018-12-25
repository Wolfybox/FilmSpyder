package Services;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class BoxTime implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		long period = 1000*60*60*2;
		Timer timer=new Timer();
		timer.schedule(new Boxupdate(),0,period);
	}

}
