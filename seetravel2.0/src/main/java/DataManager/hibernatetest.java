package DataManager;




import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;





public class hibernatetest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SessionFactory s=(SessionFactory) new Configuration().configure().buildSessionFactory();
		Session ss=s.openSession();
		List<Comment> stu = (List<Comment>)ss.createQuery("from Comment where item=?").setParameter(0, 1).list();
		for(Comment c:stu) {
			System.out.println(c.getId());
		}
		/*
		SessionFactory s=(SessionFactory) new Configuration().configure().buildSessionFactory();
		Session ss=s.openSession();
		Transaction t=ss.beginTransaction();
		try {
			User test=(User)ss.get(User.class, 2);
			test.setSex("M");
			
			t.commit();
		}catch(Exception e) {
			System.out.println(e);
		}
		
		
		Manager m=new Manager();
		
		User user=new User();
		user.setId(3);
		user.setUsername("nias");
		user.setPhone("123456789");
		user.setPassword("abc123");
		m.alter(user);
		List<Comment> list=m.query("Comment");
		for(Comment u:list) {
			if(u.getItem()==1) {
			System.out.println(u.getContent()+"   "+u.getItem());}
		}*/
	}

}
