package DataManager;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import net.sf.json.JSONObject;



public class Manager {
	private SessionFactory sf;
	private Session session;
	private Transaction tx;
	{
		sf=new Configuration().configure().buildSessionFactory();
	}
	//查询某一个表内的全部信息
	public List query(String table){
		session=sf.openSession();
		String hql="from "+table;
		Query q=session.createQuery(hql);
		List list=(List) q.list();
		session.close();
		return list;
	}
	//插入数据
	public <T> boolean insert(T t){
		session=sf.openSession();
		tx=session.beginTransaction();
		try{
			session.save(t);
			tx.commit();
			session.close();
			return true;
		}catch (Exception e) {
			System.out.println(e.toString());
			return false;
		}finally {
			session.close();
		}
	}
	//删除数据
	public <T> boolean delect(T t){
		session=sf.openSession();
		tx=session.beginTransaction();
		try{
			session.delete(t);
			tx.commit();
			session.close();
			return true;
		}catch (Exception e) {
			System.out.println(e.toString());
			return false;
		}finally {
			session.close();
		}
	}
	//更改某一个数据的全部信息
	public <T> boolean alter(T t) {
		session=sf.openSession();
		tx=session.beginTransaction();
		try {
			session.update(t);
			session.flush();
			tx.commit();
			return true;
		}catch(Exception e) {
			System.out.println(e.toString());
			return false;
		}finally {
			session.close();
		}
	}
	//返回某一个表的数据总量
	public int count(String table){
		session=sf.openSession();
		String hql="select count(*) from "+table;
		Query query=session.createQuery(hql);
		int count=((Number)query.uniqueResult()).intValue();
		session.close();
		return count;
	}
	//根据id获取某一个数据，需传入需查询的数据的对象class
	public <T> T get(Class t,int id) {
		session=sf.openSession();
		tx=session.beginTransaction();
		T response=(T)session.get(t, id);
		return response;
	}
}
