package Services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import Bean.ActorStatistic;

public class ActorUpdate extends TimerTask {

	@Override
	public void run() {
		ActorStatistic task=(ActorStatistic) GlobalBean.getApplicationContext().getBean("ActorStatistic");
		int i=1;
		while(true) {
			System.out.println("the "+i+" times");
			if(task.Update()) break;
		}
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		String[] date=df.format(new Date()).split("-");
		System.out.println(date[0]+"/"+date[1]+"/"+date[2]);
	}

}
