package DataManager;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class demo {
	public static void main(String[] args) {
		SessionFactory sf=new Configuration().configure().buildSessionFactory();
		Session session=sf.openSession();
		Manager manager=new Manager();
		Query tquery=session.createQuery("from Movie m where m.id>2034");
		
		List<Movie> movielist=tquery.list();
		ActorList actorlist=null;
		Iterator<Movie> it=movielist.iterator();
		Movie tempmovie=null;
		Query query;
		Actor actor=null;
		while(it.hasNext()) {
			tempmovie=it.next();
			String[] actors=tempmovie.getActor().split(",");
			System.out.println(tempmovie.getMoviename()+"\t"+tempmovie.getActor());
			for(String actornum:actors) {
				query=session.createQuery("from Actor a where a.maoyanid=?");
				query.setParameter(0, actornum);
				List tt=query.list();
				if(tt.size()<1) {
					continue;
				}
				actor=(Actor)tt.get(0);
				if(actor==null) {
					continue;
				}
				actorlist=new ActorList(actor, tempmovie);
				if(!manager.insert(actorlist)) {
					continue;
				}
			}
		}
		session.close();
		sf.close();
		manager.closefactory();
	}
}
