package Bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;

import com.mysql.cj.xdevapi.JsonArray;

import DataManager.Actor;
import DataManager.ActorList;
import DataManager.Movie;
import net.sf.json.JSONObject;

@Component(value="ActorStatistic")
public class ActorStatistic {
	private JSONObject male;
	private JSONObject female;
	{
		male=new JSONObject();
		female=new JSONObject();
	}
	public boolean Update(){
		SessionFactory sf=null;
		Session session=null;
		try {
			sf=new Configuration().configure().buildSessionFactory();
			session=sf.openSession();
			String[] Year= {"2012","2013","2014","2015","2016","2017","2018"};
			List<HashMap.Entry<String, Integer>> maleyear;
			List<HashMap.Entry<String, Integer>> femaleyear;
			for(String year:Year) {
				HashMap<String, Integer> man=new HashMap<>();
				HashMap<String, Integer> women=new HashMap<>();
				Query query=session.createQuery("from Movie m where m.year=?");
				query.setParameter(0, year);
				List<Movie> movielist=query.list();
				Set<ActorList> actorlist;
				Movie tempmovie;
				Actor actor;
				Iterator<Movie> it=movielist.iterator();
				while(it.hasNext()) {
					tempmovie=it.next();
					actorlist=tempmovie.getActorLists();
					Iterator<ActorList> ait=actorlist.iterator();
					while(ait.hasNext()) {
						actor=ait.next().getActor();
						if(actor.getSex().equals("男")) {
							if(man.containsKey(actor.getActorname())) {
								man.put(actor.getActorname(), man.get(actor.getActorname())+1);
							}
							else {
								man.put(actor.getActorname(), 1);
							}
						}
						else if(actor.getSex().equals("女")) {
							if(women.containsKey(actor.getActorname())) {
								women.put(actor.getActorname(), women.get(actor.getActorname())+1);
							}
							else {
								women.put(actor.getActorname(), 1);
							}
						}
						else continue;
					}
				}
				List<HashMap.Entry<String, Integer>> manlist = new ArrayList<HashMap.Entry<String, Integer>>(man.entrySet());
				List<HashMap.Entry<String, Integer>> womenlist = new ArrayList<HashMap.Entry<String, Integer>>(women.entrySet());
				Collections.sort(manlist, new Comparator<HashMap.Entry<String, Integer>>(){
					@Override
					public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
						if(o1.getValue()>o2.getValue()) {
							return -1;
						}
						else if(o1.getValue()==o2.getValue()) {
							return 0;
						}
						else {
							return 1;
						}
					}
				});
				Collections.sort(womenlist, new Comparator<HashMap.Entry<String, Integer>>(){
					@Override
					public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
						if(o1.getValue()>o2.getValue()) {
							return -1;
						}
						else if(o1.getValue()==o2.getValue()) {
							return 0;
						}
						else {
							return 1;
						}
					}
				});
				maleyear=manlist;
				femaleyear=womenlist;
				male.put(year, maleyear);
				female.put(year, femaleyear);
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
	public JSONObject getMale() {
		return male;
	}
	public void setMale(JSONObject male) {
		this.male = male;
	}
	public JSONObject getFemale() {
		return female;
	}
	public void setFemale(JSONObject female) {
		this.female = female;
	}

}
