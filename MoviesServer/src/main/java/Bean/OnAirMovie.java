package Bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;

import DataManager.Actor;
import DataManager.ActorList;
import DataManager.Manager;
import DataManager.Movie;
import DataManager.MovieClass;
import DataManager.TempMovie;

@Component(value="OnAirMovie")
public class OnAirMovie {
	private List<Movie> movies;
	{
		movies=new ArrayList<>();
	}
	public boolean update() {
		SessionFactory sf=new Configuration().configure().buildSessionFactory();
		Session session=sf.openSession();
		Manager manager=new Manager();
		List<Movie> tempMovieList=new ArrayList<>();
		try {
			Query query=session.createQuery("from TempMovie");
			List<TempMovie> movielist=query.list();
			TempMovie t;
			Iterator<TempMovie> it=movielist.iterator();
			while(it.hasNext()) {
				t=it.next();
				Movie movie=pack(t);
				query=session.createQuery("from Movie m where m.moviename=? and m.year=? and m.month=? and m.day=?");
				query.setParameter(0,movie.getMoviename());
				query.setParameter(1, movie.getYear());
				query.setParameter(2, movie.getMonth());
				query.setParameter(3, movie.getDay());
				List<Movie> mtemp=query.list();
				if(mtemp.size()>0) {
					Movie mhave=mtemp.get(0);
					movie.setId(mhave.getId());
					if(!manager.alter(movie)) {
						continue;
					}
					tempMovieList.add(movie);
				}else {
					if(!manager.insert(movie)) {
						continue;
					}
					String[] clas=movie.getClass_().split(",");
					MovieClass cla;
					for(String temp:clas) {
						cla=new MovieClass();
						cla.setClass_(temp);
						cla.setMovie(movie);
						manager.insert(cla);
					}
					String[] actors=movie.getActor().split(",");
					ActorList temp;
					Actor ac=null;
					for(String actor:actors) {
						temp=new ActorList();
						temp.setMovie(movie);
						query=session.createQuery("from Actor where maoyanid=?");
						query.setParameter(0, actor);
						ac=(Actor) query.uniqueResult();
						if(ac==null) {
							continue;
						}
						temp.setActor(ac);
						manager.insert(temp);
					}
					tempMovieList.add(movie);
				}
			}
			this.movies=tempMovieList;
			return true;
		}catch(Exception e) {
			System.out.println(e);
			return false;
		}finally {
			manager.closefactory();
			session.close();
			sf.close();
		}
	}
	public Movie pack(TempMovie t) {
		Movie m=new Movie();
		m.setActor(t.getActor());
		m.setBoxOffice(t.getBoxOffice());
		m.setClass_(t.getClass_());
		m.setDay(t.getDay());
		m.setMonth(t.getMonth());
		m.setYear(t.getYear());
		m.setDirector(t.getDirector());
		m.setMoviename(t.getMoviename());
		m.setThumburl(t.getThumburl());
		m.setIntroduce(t.getIntroduce());
		m.setRate(t.getRate());
		return m;
	}
	public List<Movie> getMovies() {
		return movies;
	}
	public void setMovies(List<Movie> movies) {
		this.movies = movies;
	}
}
