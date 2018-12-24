package Services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aliyuncs.exceptions.ClientException;

import AssistClass.Sms;
import Bean.ActorStatistic;
import Bean.OnAirMovie;
import DataManager.Actor;
import DataManager.ActorCollect;
import DataManager.ActorList;
import DataManager.Manager;
import DataManager.Movie;
import DataManager.MovieClass;
import DataManager.User;
import SpringModel.actordetail;
import SpringModel.actorsimple;
import SpringModel.actorsub;
import SpringModel.idclass;
import SpringModel.moviedetail;
import SpringModel.moviesimple;
import SpringModel.pagesearch;
import SpringModel.phone;
import SpringModel.search;
import SpringModel.statistics;
import SpringModel.trend;
import SpringModel.usersimple;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Controller

public class ServicesController {
	private SessionFactory sf=new Configuration().configure().buildSessionFactory();
	private Transaction tx;
	@RequestMapping(value="sms.do",method=RequestMethod.POST)
	@ResponseBody
	//手机验证码发送
	/*
	 * 输入参数model{
	 * phone:电话号码(String)}
	 * 返回参数map{
	 * num:四位验证码(String))
	 * 若失败则num="-1"
	 */
	public HashMap<String,String> Sms(@RequestBody phone model) {
		//需接收的json对象要包括phone这个属性
		HashMap<String,String> map=new HashMap<>();
		Random random=new Random();
		//随机生成四位数的验证码
		String num=""+(random.nextInt(999)+1000);
		String phone=model.getPhone();
		try {
			//发送短信
			Sms.sendSms(phone, num);
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("num", "-1");
			return map;
		}
		//发送成功后返回的json对象，里面只有num一个属性，但num为-1时发送验证码失败
		map.put("num", num);
		return map;
	}
	//用户注册
	/*
	 * 输入参数User{
	 * username:用户姓名(String)
	 * phone:用户电话(String)}
	 * 成功返回User注册信息
	 * 若用户已注册则返回用户的信息
	 * 失败返回null
	 */
	@RequestMapping(value="register.do",method=RequestMethod.POST)
	@ResponseBody
	public usersimple Register(@RequestBody User user) {
		Session session;
		session=sf.openSession();
		String phone=user.getPhone();
		String username=user.getUsername();
		Query query=session.createQuery("from User u where u.username=?");
		query.setParameter(0, username);
		List<User> ifhave=query.list();
		if(ifhave.size()>0) {
			return packUser(ifhave.get(0));
		}
		Manager manager=new Manager();
		if(manager.insert(user)) {
			manager.closefactory();
			return packUser(user);
		}
		manager.closefactory();
		return null;
	}
	//用户登录
	/*
	 * 输入参数model{
	 * phone:用户电话(String)}
	 * 返回参数User{
	 * id:用户id（int）
	 * username：用户名（String）
	 * phone：用户手机(String)}
	 * 若出错则返回null
	 */
	@RequestMapping(value="login.do",method=RequestMethod.POST)
	@ResponseBody
	public User Login(@RequestBody phone model) {
		Manager manager=new Manager();
		String phone=model.getPhone();
		Session session;
		session=sf.openSession();
		try {
			User user=(User) session.createQuery("from User where phone=?").setParameter(0, phone).list().get(0);
			return user;
		}catch(Exception e) {
			System.out.println(e);
		}finally {
			session.close();
			manager.closefactory();
		}
		return null;
	}
	//用户收藏
	/*
	 * 输入参数collect{
	 * userid:用户id(int)
	 * movieid:电影id(int)}
	 * 成功返回collect信息
	 * 失败返回null
	 */
	@RequestMapping(value="collect.do",method=RequestMethod.POST)
	@ResponseBody
	public DataManager.Collect Collect(@RequestBody DataManager.Collect collect) {
		Manager manager=new Manager();
		Session session;
		session=sf.openSession();
		try {
			Query query=session.createQuery("from Collect c where c.userid=? and c.movieid=?");
			query.setParameter(0, collect.getUserid());
			query.setParameter(1, collect.getMovieid());
			if(query.list().size()>0) return null;
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}finally {
			session.close();
		}
		if(manager.insert(collect)) {
			manager.closefactory();
			return collect;
		}
		manager.closefactory();
		return null;
	}
	@RequestMapping(value="returncollect.do",method=RequestMethod.POST)
	@ResponseBody
	public List<moviesimple> ReturnCollect(@RequestBody DataManager.Collect collect){
		int userid=collect.getUserid();
		Session session=null;
		try {
			session=sf.openSession();
			List<moviesimple> respone=new ArrayList<>();
			Query query=session.createQuery("from Collect c where c.userid=?");
			query.setParameter(0, userid);
			List<DataManager.Collect> collectlist=query.list();
			if(collectlist.size()<1) {
				return null;
			}
			Iterator<DataManager.Collect> it=collectlist.iterator();
			while(it.hasNext()) {
				int movieid=it.next().getMovieid();
				query=session.createQuery("from Movie m where m.id=?");
				query.setParameter(0, movieid);
				Movie t=(Movie) query.uniqueResult();
				if(t==null) {
					continue;
				}
				respone.add(pack(t));
			}
			return respone;
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}finally {
			session.close();
		}
	}
	//删除收藏电影
	/*
	 * 传人参数{
	 * userid:int 用户id
	 * movieid:int 电影id
	 * }
	 * 若成功则返回{"ifsuccess","true"}
	 * 失败则返回{"ifsuccess",false}
	 * 若表中没有这条记录返回{"ifsuccess","no this collect"}
	 */
	@RequestMapping(value="deletecollect.do",method=RequestMethod.POST)
	@ResponseBody
	public HashMap<String, String> DeleteCollect(@RequestBody DataManager.Collect collect) {
		int userid=collect.getUserid();
		int movieid=collect.getMovieid();
		Session session=null;
		HashMap<String, String> respone=new HashMap<>();
		Manager manager=null;
		try {
			session=sf.openSession();
			Query query=session.createQuery("from Collect c where c.userid=? and c.movieid=?");
			query.setParameter(0, userid);
			query.setParameter(1, movieid);
			List<DataManager.Collect> list=query.list();
			if(list.size()<1) {
				respone.put("ifsuccess","no this collect");
				return respone;
			}
			DataManager.Collect hascollect=list.get(0);
			manager=new Manager();
			if(manager.delect(hascollect)) {
				respone.put("ifsuccess", "true");
				return respone;
			}
			respone.put("ifsuccess","false");
			return respone;
		}catch(Exception e) {
			System.out.println(e);
			respone.put("ifsuccess", "false");
			return respone;
		}finally {
			manager.closefactory();
			session.close();
		}
	}
	//演员收藏
	/*
	 * 传人参数{
	 * actorname:演员名字
	 * userid:用户id
	 * }
	 * 若失败则返回null
	 */
	@RequestMapping(value="sub.do",method=RequestMethod.POST)
	@ResponseBody
	public HashMap<String, Object> Sub(@RequestBody actorsub s){
		String actorname=s.getActorname();
		int id=s.getUserid();
		Session session=null;
		Manager manager=null;
		try {
			session=sf.openSession();
			manager=new Manager();
			HashMap<String , Object> map=new HashMap<>();
			Query query=session.createQuery("from Actor a where a.actorname=?");
			query.setParameter(0, actorname);
			List<Actor> tt=query.list();
			if(tt.size()<1) {
				return null;
			}
			Actor actor=tt.get(0);
			query=session.createQuery("from User u where u.id=?");
			query.setParameter(0, id);
			List<User> users=query.list();
			if(users.size()<1) {
				return null;
			}
			User user=users.get(0);
			List<ActorCollect> list=manager.query("ActorCollect");
			Iterator<ActorCollect> it=list.iterator();
			while(it.hasNext()) {
				ActorCollect t=it.next();
				if(t.getActor().getActorname().equals(actor.getActorname())&&t.getUser().getUsername().equals(user.getUsername())) {
					map.put("userid", t.getUser().getId());
					map.put("actorname",t.getActor().getActorname());
					map.put("id",t.getId());
					return map;
				}
			}
			ActorCollect actorcollect=new ActorCollect(actor, user);
			if(!manager.insert(actorcollect)) {
				return null;
			}
			map.put("userid", user.getId());
			map.put("actorname", actor.getActorname());
			map.put("id", actorcollect.getId());
			return map;
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}finally {
			manager.closefactory();
			session.close();
		}
	}
	//返回关注演员
	@RequestMapping(value="returnsub.do",method=RequestMethod.POST)
	@ResponseBody
	public List<actorsimple> ReturnSub(@RequestBody actorsub s){
		int userid=s.getUserid();
		Session session=null;
		try {
			session=sf.openSession();
			Query query=session.createQuery("from User u where u.id=?");
			query.setParameter(0, userid);
			User user=(User) query.uniqueResult();
			if(user==null) {
				System.out.println("no this user");
				return null;
			}
			query=session.createQuery("from ActorCollect a where a.user=?");
			query.setParameter(0, user);
			List<ActorCollect> list=query.list();
			List<actorsimple> respone=new ArrayList<>();
			Iterator<ActorCollect> it=list.iterator();
			Actor t;
			while(it.hasNext()) {
				t=it.next().getActor();
				if(t==null) {
					continue;
				}
				respone.add(packActorsimple(t));
			}
			return respone;
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}finally {
			session.close();
		}
	}
	//演员取关
	/*
	 * 传人参数{
	 * userid:int
	 * actorname:String
	 * }
	 * 若成功则返回{"ifsuccess","true"}
	 * 失败则返回{"ifsuccess","false"}
	 * 若记录中不存在{"ifsuccess","no this attention"}
	 */
	@RequestMapping(value="deletesub.do",method=RequestMethod.POST)
	@ResponseBody
	public HashMap<String, String> DeleteSub(@RequestBody actorsub s){
		String actorname=s.getActorname();
		int userid=s.getUserid();
		Session session=null;
		HashMap<String, String> map=new HashMap<>();
		Manager manager=null;
		try {
			session=sf.openSession();
			manager=new Manager();
			Query query=session.createQuery("from User u where u.id=?");
			query.setParameter(0, userid);
			User user=(User) query.uniqueResult();
			query=session.createQuery("from ActorCollect a where a.user=?");
			query.setParameter(0, user);
			List<ActorCollect> list=query.list();
			if(list.size()<1) {
				map.put("ifsuccess", "no this attention");
				return map;
			}
			Iterator<ActorCollect> it=list.iterator();
			ActorCollect a;
			while(it.hasNext()) {
				a=it.next();
				if(a.getActor().getActorname().equals(actorname)) {
					if(manager.delect(a)) {
						map.put("ifsuccess","true");
						return map;
					}
					map.put("ifsuccess", "no");
				}
			}
			map.put("ifsuccess", "no this attention");
			return map;
		}catch(Exception e) {
			System.out.println(e);
			map.put("ifsuccess", "false");
			return map;
		}finally {
			manager.closefactory();
			session.close();
		}
	}
	//电影上传
	/*
	 * 输入参数movie{
	 * private Integer id;
	 *private String moviename;
	 *private String director;
	 *private String boxOffice;
	 *private String class_;
	 *private String year;
	 *private String month;
	 *private String day;
	 *private String introduce;
	 *private String thumburl;
	 *private String actor;
	 *private Integer rate;
	 * }
	 * 若上传失败则返回null
	 */
	@RequestMapping(value="movieupload.do",method=RequestMethod.POST)
	@ResponseBody
	public Movie Movieupload(@RequestBody Movie movie) {
		Manager manager=new Manager();
		Session session;
		session=sf.openSession();
		Query query=session.createQuery("from Movie where moviename=?");
		query.setParameter(0, movie.getMoviename());
		List<Movie> list=query.list();
		MovieClass cla;
		if(list.size()>0) {
			Movie oldmovie=list.get(0);
			if(movie.getBoxOffice()!=null) {
				oldmovie.setBoxOffice(movie.getBoxOffice());
			}
			if(movie.getDay()!=null) {
				oldmovie.setDay(movie.getDay());
			}
			if(movie.getDirector()!=null) {
				oldmovie.setDirector(movie.getDirector());
			}
			if(movie.getIntroduce()!=null) {
				oldmovie.setIntroduce(movie.getIntroduce());
			}
			if(movie.getMonth()!=null) {
				oldmovie.setMonth(movie.getMonth());
			}
			if(movie.getMoviename()!=null) {
				oldmovie.setMoviename(movie.getMoviename());
			}
			if(movie.getYear()!=null) {
				oldmovie.setYear(movie.getYear());
			}
			if(movie.getThumburl()!=null) {
				oldmovie.setThumburl(movie.getThumburl());
			}
			if(movie.getActor()!=null&&oldmovie.getActor()==null) {
				oldmovie.setActor(movie.getActor());
				String[] actors=movie.getActor().split(",");
				ActorList temp;
				Actor ac=null;
				for(String actor:actors) {
					temp=new ActorList();
					temp.setMovie(oldmovie);
					query=session.createQuery("from Actor where actorname=?");
					query.setParameter(0, actor);
					ac=(Actor) query.uniqueResult();
					if(ac==null) {
						continue;
					}
					temp.setActor(ac);
					manager.insert(temp);
				}
			}
			if(oldmovie.getClass_()==null&&movie.getClass_()!=null) {
				oldmovie.setClass_(movie.getClass_());
				String[] clas=movie.getClass_().split(",");
				for(String temp:clas) {
					cla=new MovieClass();
					cla.setClass_(temp);
					cla.setMovie(oldmovie);
					manager.insert(cla);
				}
			}
			manager.alter(oldmovie);
			return oldmovie;
		}
		else {
			if(manager.insert(movie)) {
				String[] clas=movie.getClass_().split(",");
				String[] actors=movie.getActor().split(",");
				ActorList actorlist;
				Actor ac=null;
				for(String actor:actors) {
					actorlist=new ActorList();
					actorlist.setMovie(movie);
					query=session.createQuery("from Actor where actorname=?");
					query.setParameter(0, actor);
					ac=(Actor) query.uniqueResult();
					if(ac==null) {
						continue;
					}
					actorlist.setActor(ac);
					manager.insert(actorlist);
				}
				for(String temp:clas) {
					cla=new MovieClass();
					cla.setClass_(temp);
					cla.setMovie(movie);
					manager.insert(cla);
				}
				return movie;
			}
			return null;
		}
	}
	//根据电影名字模糊查询电影
	/*
	 * 输入参数search{
	 * String search:需要查询的电影名字，可简写
	 * }
	 * 若失败则返回null
	 * 成功返回一个电影List<Movie>
	 */
	@RequestMapping(value="searchbyname.do",method=RequestMethod.POST)
	@ResponseBody
	public List<moviesimple> SearchbyName(@RequestBody search search){
		String searchname=search.getSearch();
		Session session;
		String temp="%";
		for(int i=0;i<searchname.length();i++) {
			temp+=searchname.charAt(i)+"%";
		}
		searchname=temp;
		session=sf.openSession();
		Query query=session.createQuery("from Movie m where m.moviename like:name");
		query.setString("name", searchname);
		List<Movie> movielist=query.list();
		List<moviesimple> respone=new ArrayList<>();
		if(movielist.size()<=0) {
			session.close();
			return null;
		}
		else {
			moviesimple moviesimple;
			Iterator<Movie> i=movielist.iterator();
			Movie t;
			while(i.hasNext()) {
				t=i.next();
				moviesimple=pack(t);
				respone.add(moviesimple);
			}
			session.close();
			return respone;
		}
	}
	//根据类别返回电影
	/*
	 * 输入参数search{
	 * String search:电影类别
	 * }
	 * 若失败则返回null
	 * 成功返回一个电影List<moviesimple>
	 */
	@RequestMapping(value="searchbyclass.do",method=RequestMethod.POST)
	@ResponseBody
	public List<moviesimple> SearchbyClass(@RequestBody search search){
		String class_=search.getSearch();
		Session session=null;
		int firststart=(search.getPage()-1)*20+1;
		try {
			session=sf.openSession();
			Query query=session.createQuery("from MovieClass m where m.class_=?");
			query.setFirstResult(firststart);
			query.setMaxResults(20);
			query.setParameter(0, class_);
			List<MovieClass> list=query.list();
			List<moviesimple> respone=new ArrayList<>();
			Movie movie;
			for(MovieClass temp:list) {
				movie=temp.getMovie();
				respone.add(pack(movie));
			}
			return respone;
		}catch(Exception e) {
			return null;
		}finally {
			session.close();
		}
	}
	//根据演员名字返回演员详情
	/*
	 * 传人参数{
	 * search:String 演员名字
	 * }
	 * 若失败则返回null
	 */
	@RequestMapping(value="searchactor.do",method=RequestMethod.POST)
	@ResponseBody
	public actordetail SearchActor(@RequestBody search search) {
		String actorname=search.getSearch();
		Session session=null;
		try {
			session=sf.openSession();
			Query query=session.createQuery("from Actor a where a.actorname=?");
			query.setParameter(0, actorname);
			List tt=query.list();
			if(tt.size()<1) {
				return null;
			}
			Actor actor=(Actor)tt.get(0);
			actordetail actordetail=new actordetail();
			actordetail.setActorname(actor.getActorname());
			String birth=actor.getYear()+"/"+actor.getMonth()+"/"+actor.getDay();
			actordetail.setBirth(birth);
			Set<ActorList> set=actor.getActorLists();
			actordetail.setCount(set.size());
			List<moviesimple> list=new ArrayList<>();
			Iterator<ActorList> it=set.iterator();
			ActorList temp=null;
			while(it.hasNext()) {
				temp=it.next();
				list.add(pack(temp.getMovie()));
			}
			JSONArray arry=JSONArray.fromObject(list);
			actordetail.setPlay(arry);
			String imageurl=actor.getImageurl();
			if(!imageurl.startsWith("http")) {
				actordetail.setImageurl("https://filmspyder.cn/Image/timg.png");
			}else {
				actordetail.setImageurl(actor.getImageurl());
			}
			return actordetail;
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}finally {
			session.close();
		}
	}
//	@RequestMapping(value="movietest.do",method=RequestMethod.POST)
//	@ResponseBody
//	public int Movietest(){
//		beantest b=(beantest) GlobalBean.getApplicationContext().getBean("beantest");
//		b.add();
//		System.out.println(b.get());
//		return b.get();
//	}
	//按照电影评分分页返回数据
	/*
	 * 输入参数page{
	 * int page:需要请求的页数
	 * }
	 * 成功则返回List<moviesimple>
	 * 失败返回null
	 */
	@RequestMapping(value="moviebyrate.do",method=RequestMethod.POST)
	@ResponseBody
	public List<moviesimple> MoviebyRate(@RequestBody pagesearch page){
		Session session=null;
		session=sf.openSession();
		int firststart=(page.getPage()-1)*50+1;
		Query query=session.createQuery("from Movie m order by m.rate desc");
		query.setFirstResult(firststart);
		query.setMaxResults(50);
		List<Movie> movielist=query.list();
		List<moviesimple> respone=new ArrayList<>();
		if(movielist.size()<=0) {
			session.close();
			return null;
		}
		else {
			moviesimple moviesimple;
			Iterator<Movie> i=movielist.iterator();
			Movie t;
			while(i.hasNext()) {
				t=i.next();
				moviesimple=pack(t);
				respone.add(moviesimple);
			}
			session.close();
			return respone;
		}
	}
	//根据电影id返回电影详细信息
	/*
	 * 传人参数 idclass{
	 * id:int 电影id
	 * }
	 * 若失败则返回null
	 * 否则返回电影详细信息
	 */
	@RequestMapping(value="moviebyid.do",method=RequestMethod.POST)
	@ResponseBody
	public moviedetail MovieById(@RequestBody SpringModel.idclass idclass) {
		int id=idclass.getId();
		Session session=null;
		Manager manager = null;
		try {
			manager=new Manager();
			Movie movie=manager.get(Movie.class, id);
			if(movie==null) {
				System.out.println("no this id");
				return null;
			}
			return packMovieDetail(movie);
		}catch(Exception e) {
			return null;
		}finally {
			manager.closefactory();
		}
	}
	//根据给定的年月，统计各类别电影出现的比例
	/*
	 * 输入参数{
	 * String year
	 * String month
	 * }
	 * 若失败则返回null
	 * 成功则返回一个hashmap
	 */
	@RequestMapping(value="statisticsbymonth.do",method=RequestMethod.POST)
	@ResponseBody
	public HashMap<String,Double> StatisticsByMonth(@RequestBody statistics request){
		String year=request.getYear();
		String month=request.getMonth();
		Session session=null;
		try {
			session=sf.openSession();
			Query query=session.createQuery("from Movie m where m.year=? and m.month=?");
			query.setParameter(0, year);
			query.setParameter(1, month);
			List<Movie> list=query.list();
			if(list.size()<=0) {
				System.out.println("no movie in this time");
				return null;
			}
			Iterator<Movie> it=list.iterator();
			HashMap<String,Double> respone=new HashMap<>();
			Movie t;
			while(it.hasNext()) {
				t=it.next();
				double boxoffice=Double.parseDouble(t.getBoxOffice());
				String[] class_=t.getClass_().split(",");
				if(class_.length<=0) {
					return null;
				}
				for(String cla:class_) {
					if(respone.containsKey(cla)) {
						double temp=respone.get(cla)+boxoffice;
						respone.replace(cla, temp);
					}
					else {
						double temp=boxoffice;
						respone.put(cla,temp);
					}
				}
			}
			Set<String> keys=respone.keySet();
			Iterator<String> key=keys.iterator();
			while(key.hasNext()) {
				String tkey=key.next();
				double temp=respone.get(tkey);
				respone.replace(tkey, temp);
			}
			return respone;
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}finally {
			session.close();
		}
	}
	//根据年和季度，统计各类别电影的比例
	/*
	 * 输入参数{
	 * String year
	 * String quarter:季度，可选["第一季度",“第二季度","第三季度","第四季度"]
	 * }
	 * 若失败则返回null
	 * 成功则返回一个hashmap
	 */
	@RequestMapping(value="statisticsbyquarter.do",method=RequestMethod.POST)
	@ResponseBody
	public HashMap<String,Double> StatisticsByQuarter(@RequestBody statistics request){
		String year=request.getYear();
		String quarter=request.getQuarter();
		String[] month;
		Session session=null;
		if(quarter.equals("第一季度")) {
			month=new String[] {"01","02","03"};
		}
		else if(quarter.equals("第二季度")) {
			month=new String[] {"04","05","06"};
		}
		else if(quarter.equals("第三季度")) {
			month=new String[] {"07","08","09"};
		}
		else {
			month=new String[] {"10","11","12"};
		}
		try {
			session=sf.openSession();
			Query query=session.createQuery("from Movie m where m.year=? and (m.month=? or m.month=? or m.month=?)");
			query.setParameter(0,year);
			query.setParameter(1, month[0]);
			query.setParameter(2, month[1]);
			query.setParameter(3, month[2]);
			List<Movie> list=query.list();
			if(list.size()<=0) {
				System.out.println("no movie in this time");
				return null;
			}
			Iterator<Movie> it=list.iterator();
			HashMap<String,Double> respone=new HashMap<>();
			Movie t;
			while(it.hasNext()) {
				t=it.next();
				double boxoffice=Double.parseDouble(t.getBoxOffice());
				String[] class_=t.getClass_().split(",");
				if(class_.length<=0) {
					return null;
				}
				for(String cla:class_) {
					if(respone.containsKey(cla)) {
						double temp=respone.get(cla)+boxoffice;
						respone.replace(cla, temp);
					}
					else {
						double temp=boxoffice;
						respone.put(cla,temp);
					}
				}
			}
			Set<String> keys=respone.keySet();
			Iterator<String> key=keys.iterator();
			while(key.hasNext()) {
				String tkey=key.next();
				double temp=respone.get(tkey);
				respone.replace(tkey, temp);
			}
			return respone;
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}finally {
			session.close();
		}
	}
	//返回topN的电影票房
	/*
	 * 传人参数{
	 * year:String
	 * topnumber:返回的条数 int
	 * }
	 * 若失败则返回null
	 */
	@RequestMapping(value="statisticstop.do",method=RequestMethod.POST)
	@ResponseBody
	public HashMap<String, Double> StatisticsTop(@RequestBody statistics request){
		Session session=null;
		HashMap<String, Double> respone=new HashMap<>();
		String year=request.getYear();
		int topnum=request.getTopnumber();
		try {
			session=sf.openSession();
			Query query=session.createQuery("from Movie m where m.year=? order by cast(m.boxOffice as integer) desc");
			query.setParameter(0, year);
			query.setMaxResults(topnum);
			List<Movie> list=query.list();
			if(list.isEmpty()) {
				return null;
			}
			Movie temp;
			Iterator<Movie> it=list.iterator();
			while(it.hasNext()) {
				temp=it.next();
				respone.put(temp.getMoviename(), Double.parseDouble(temp.getBoxOffice()));
			}
			return respone;
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}finally {
			session.close();
		}
	}
	@RequestMapping(value="top10.do",method=RequestMethod.POST)
	@ResponseBody
	public List<moviesimple> Top10(){
		Session session=null;
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		String year=df.format(new Date()).split("-")[0];
		String month=df.format(new Date()).split("-")[1];
		List<moviesimple> respone=new ArrayList<>();
		try {
			session=sf.openSession();
			Query query=session.createQuery("from Movie m where m.year=? and m.month=? order by cast(m.boxOffice as integer) desc");
			query.setParameter(0, year);
			query.setParameter(1, month);
			query.setMaxResults(10);
			List<Movie> movies=query.list();
			Iterator<Movie> it=movies.iterator();
			while(it.hasNext()) {
				respone.add(pack(it.next()));
			}
			return respone;
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}finally {
			session.close();
		}
	}
	@RequestMapping(value="cold10.do",method=RequestMethod.POST)
	@ResponseBody
	public List<moviesimple> Cold10(){
		Session session=null;
		List<moviesimple> respone=new ArrayList<>();
		try {
			session=sf.openSession();
			Query query=session.createQuery("from Movie m where m.rate>=8 order by cast(m.boxOffice as integer)");
			query.setMaxResults(10);
			List<Movie> movies=query.list();
			Iterator<Movie> it=movies.iterator();
			while(it.hasNext()) {
				respone.add(pack(it.next()));
			}
			return respone;
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}finally {
			session.close();
		}
	}
	@RequestMapping(value="toprate10.do",method=RequestMethod.POST)
	@ResponseBody
	public List<moviesimple> TopRate10(){
		Session session=null;
		List<moviesimple> respone=new ArrayList<>();
		try {
			session=sf.openSession();
			Query query=session.createQuery("from Movie m order by m.rate desc");
			query.setMaxResults(10);
			List<Movie> movies=query.list();
			Iterator<Movie> it=movies.iterator();
			while(it.hasNext()) {
				respone.add(pack(it.next()));
			}
			return respone;
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}finally {
			session.close();
		}
	}
	@RequestMapping(value="science10.do",method=RequestMethod.POST)
	@ResponseBody
	public List<moviesimple> Science10(){
		Session session=null;
		List<moviesimple> respone=new ArrayList<>();
		try {
			session=sf.openSession();
			Query query=session.createQuery("from MovieClass m where m.class_=?");
			query.setParameter(0, "科幻");
			List<MovieClass> class_=query.list();
			Iterator<MovieClass> it=class_.iterator();
			List<Movie> movie=new ArrayList<>();
			while(it.hasNext()) {
				movie.add(it.next().getMovie());
			}
			Collections.sort(movie, new Comparator<Movie>() {

				@Override
				public int compare(Movie o1, Movie o2) {
					if(o1.getRate()>o2.getRate()) {
						return -1;
					}
					else if(o1.getRate()==o2.getRate()) {
						return 0;
					}
					else {
						return 1;
					}
				}
			});
			for(int i=0;i<10;i++) {
				respone.add(pack(movie.get(i)));
			}
			return respone;
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}finally {
			session.close();
		}
	}
	@RequestMapping(value="statisticsactor.do",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject StatisticsActor(@RequestBody statistics request){
		JSONObject respone=new JSONObject();
		int topnum=request.getTopnumber();
		Session session=null;
		try {
			session=sf.openSession();
			HashMap<String, Integer> man=new HashMap<>();
			Query query=session.createQuery("from Actor a where a.sex=? order by a.count desc");
			query.setParameter(0, "男");
			query.setMaxResults(topnum);
			List<Actor> list=query.list();
			Iterator<Actor> it=list.iterator();
			Actor actor;
			while(it.hasNext()) {
				actor=it.next();
				man.put(actor.getActorname(), actor.getCount());
			}
			respone.put("male", man);
			HashMap<String, Integer> women=new HashMap<>();
			query=session.createQuery("from Actor a where a.sex=? order by a.count desc");
			query.setParameter(0, "女");
			query.setMaxResults(topnum);
			list=query.list();
			it=list.iterator();
			while(it.hasNext()) {
				actor=it.next();
				women.put(actor.getActorname(), actor.getCount());
			}
			respone.put("female", women);
			return respone;
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}finally {
			session.close();
		}
	}
	@RequestMapping(value="statisticsyear.do",method=RequestMethod.POST)
	@ResponseBody
	public JSONArray StatisticsYear(@RequestBody trend request){
		Session session=null;
		try {
			int startyear=Integer.parseInt(request.getStartyear());
			int endyear=Integer.parseInt(request.getEndyear());
			JSONArray arry=new JSONArray();
			JSONObject json=null;
			session=sf.openSession();
			Query query=null;
			List<Movie> movielist=null;
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
				arry.add(json);
			}
			return arry;
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}finally {
			session.close();
		}
	}
	@RequestMapping(value="statisticsactorbyyear.do",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject StaticsActorByYear(@RequestBody statistics request){
		Session session=null;
		String year=request.getYear();
		int topnum=request.getTopnumber();
		JSONObject respone=new JSONObject();
		try {
			session=sf.openSession();
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
						if(man.containsKey(actor.getActorname())) {
							women.put(actor.getActorname(), man.get(actor.getActorname())+1);
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
			HashMap<String, Integer> manrespone=new HashMap<>();
			HashMap<String, Integer> womenrespone=new HashMap<>();
			for(int i=0;i<topnum;i++) {
				if(i<manrespone.size()) {
					manrespone.put(manlist.get(i).getKey(),manlist.get(i).getValue());
				}
				if(i<womenrespone.size()) {
					womenrespone.put(womenlist.get(i).getKey(), womenlist.get(i).getValue());
				}
			}
			respone.put("male", manrespone);
			respone.put("female", womenrespone);
			return respone;
 		}catch(Exception e) {
			System.out.println(e);
			return null;
		}finally {
			session.close();
		}
	}
	@RequestMapping(value="actoryear.do",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject ActorYear(@RequestBody statistics request) {
		ActorStatistic actorstatistic=(ActorStatistic) GlobalBean.getApplicationContext().getBean("ActorStatistic");
		String year=request.getYear();
		int topnum=request.getTopnumber();
		JSONObject respone=new JSONObject();
		try {
			JSONObject male=actorstatistic.getMale();
			JSONObject female=actorstatistic.getFemale();
			JSONObject maletemp=new JSONObject();
			JSONObject femaletemp=new JSONObject();
			JSONArray man= male.getJSONArray(year);
			JSONArray women=female.getJSONArray(year);
			for(int i=0;i<topnum;i++) {
				if(i<man.size()) {
					maletemp.put(i,man.get(i));
				}
				if(i<women.size()) {
					femaletemp.put(i,women.get(i));
				}
			}
			respone.put("male", maletemp);
			respone.put("female", femaletemp);
			return respone;
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}
	}
	//正在热映电影，返回当前月的所有电影
	@RequestMapping(value="onair.do",method=RequestMethod.POST)
	@ResponseBody
	public List<moviesimple> OnAir() {
//		Session session=null;
//		try {
//			session=sf.openSession();
//			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
//			String year=df.format(new Date()).split("-")[0];
//			String month=df.format(new Date()).split("-")[1];
//			Query query=session.createQuery("from Movie m where m.year=? and m.month=? ");
//			query.setParameter(0, year);
//			query.setParameter(1, month);
//			List<Movie> list=query.list();
//			Iterator<Movie> it=list.iterator();
//			List<moviesimple> respone=new ArrayList<>();
//			while(it.hasNext()) {
//				respone.add(pack(it.next()));
//			}
//			return respone;
//		}catch(Exception e) {
//			System.out.println(e);
//			return null;
//		}finally {
//			session.close();
//		}
		OnAirMovie movie=(OnAirMovie) GlobalBean.getApplicationContext().getBean("OnAirMovie");
		Iterator<Movie> it=movie.getMovies().iterator();
		List<moviesimple> respone=new ArrayList<>();
		while(it.hasNext()) {
			respone.add(pack(it.next()));
		}
		return respone;
	}
	//推荐
	@RequestMapping(value="recommend.do",method=RequestMethod.POST)
	@ResponseBody
	public List<moviesimple> Recommend(@RequestBody idclass idclass){
		Session session=null;
		int id=idclass.getId();
		try {
			session=sf.openSession();
			Movie movie;
			List<moviesimple> respone=new ArrayList<>();
			Random rand = new Random();
			int[] j=new int[3];
			for(int i=0;i<3;i++) {
				j[i]=rand.nextInt(12)+1;
			}
			for(int i=0;i<3;i++) {
				Query query=session.createQuery("from Movie m where cast(m.month as integer)=? order by m.rate desc");
				query.setParameter(0, j[i]);
				movie=(Movie) query.list().get(0);
				respone.add(pack(movie));
			}
			return respone;
		}catch(Exception e) {
			System.out.println(e);
			return null;
		}finally {
			session.close();
		}
	}
	//将电影信息包装为简洁版电影信息
	public moviesimple pack(Movie t) {
		moviesimple moviesimple=new moviesimple();
		moviesimple.setClass_(t.getClass_());
		moviesimple.setDirector(t.getDirector());
		moviesimple.setId(t.getId());
		moviesimple.setMoviename(t.getMoviename());
		moviesimple.setRate(t.getRate());
		moviesimple.setThumburl(t.getThumburl());
		moviesimple.setTime(t.getYear()+"/"+t.getMonth()+"/"+t.getDay());
		return moviesimple;
	}
	//将电影信息封装成详细版电影信息
	public moviedetail packMovieDetail(Movie t) {
		moviedetail moviedetail=new moviedetail();
		moviedetail.setId(t.getId());
		moviedetail.setBoxOffice(t.getBoxOffice());
		moviedetail.setClass_(t.getClass_());
		moviedetail.setMoviename(t.getMoviename());
		moviedetail.setTime(t.getYear()+"/"+t.getMonth()+"/"+t.getDay());
		moviedetail.setDirector(t.getDirector());
		moviedetail.setIntroduce(t.getIntroduce());
		moviedetail.setRate(t.getRate());
		moviedetail.setThumburl(t.getThumburl());
		JSONArray actor=new JSONArray();
		JSONObject json;
		Actor atemp;
		Set<ActorList> actorlist=t.getActorLists();
		Iterator<ActorList> it=actorlist.iterator();
		while(it.hasNext()) {
			atemp=it.next().getActor();
			if(atemp==null) continue;
			json=new JSONObject();
			if(atemp.getActorname()==null) {
				continue;
			}
			json.put("actorname", atemp.getActorname());
			if(atemp.getImageurl()!=null) {
				json.put("imageurl", atemp.getImageurl());
			}
			if(!atemp.getImageurl().startsWith("http")){
				json.put("imageurl", "https://filmspyder.cn/Image/timg.png");
			}
			actor.add(json);
		}
		moviedetail.setActor(actor);
		return moviedetail;
	}
	//将用户信息封装
	public usersimple packUser(User u) {
		SpringModel.usersimple usersimple=new usersimple();
		usersimple.setUsername(u.getUsername());
		usersimple.setId(u.getId());
		return usersimple;
	}
	public actorsimple packActorsimple(Actor actor) {
		actorsimple actrosimples=new actorsimple();
		actrosimples.setActorname(actor.getActorname());
		actrosimples.setImageurl(actor.getImageurl());
		return actrosimples;
	}
}
