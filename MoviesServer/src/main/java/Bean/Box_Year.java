package Bean;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;

import DataManager.Movie;
import net.sf.json.JSONObject;

@Component(value="Box_Year")
public class Box_Year {
	private HashMap<Integer, JSONObject> Box_data;
	{
		Box_data=new HashMap<>();
	}
	public boolean update() {
		SessionFactory sf=new Configuration().configure().buildSessionFactory();
		Session session=sf.openSession();
		try {
			int startyear=2012;
			int endyear=2018;
			JSONObject json=null;
			List<Movie> movielist=null;
			Query query=null;
			for(int i=startyear;i<=endyear;i++) {
				json=new JSONObject();
				for(int j=1;j<=12;j++) {
					query=session.createQuery("from Movie m where cast(m.year as integer)=? and cast(m.month as integer)=?");
					query.setParameter(0, i);
					query.setParameter(1, j);
					movielist=query.list();
					double sum=0;
					if(movielist.size()<1) {
						json.put(j,0);
					}
					Iterator<Movie> it=movielist.iterator();
					while(it.hasNext()) {
						sum+=Double.parseDouble(it.next().getBoxOffice());
					}
					json.put(j, sum);
				}
				Box_data.put(i, json);
			}
			return true;
		}catch(Exception e) {
			System.out.println(e);
			return false;
		}finally {
			session.close();
			sf.close();
		}
	}
	public HashMap<Integer, JSONObject> getBox_data() {
		return Box_data;
	}
	public void setBox_data(HashMap<Integer, JSONObject> box_data) {
		Box_data = box_data;
	}
}
