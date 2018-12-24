package Services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import Bean.OnAirMovie;

public class OnAirMovieUpdate extends TimerTask {

	@Override
	public void run() {
		OnAirMovie task=(OnAirMovie) GlobalBean.getApplicationContext().getBean("OnAirMovie");
		int i=1;
		while(true) {
			System.out.println("the "+i+" times");
			if(task.update()) break;
		}
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		String[] date=df.format(new Date()).split("-");
		System.out.println(date[0]+"/"+date[1]+"/"+date[2]);
	}

}
