package Services;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class OnAirUpdate implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		long period = 1000*60*60*10;
		Timer timer=new Timer();
		timer.schedule(new OnAirMovieUpdate(),0,period);
	}

}
