package Services;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TimeManager implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		long period = 1000*60*60*2;
		Timer timer=new Timer();
		timer.schedule(new ActorUpdate(),0,period);
	}

}
