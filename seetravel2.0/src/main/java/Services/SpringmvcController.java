package Services;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import DataManager.Comment;
import DataManager.Global;
import DataManager.Image;
import DataManager.Manager;
import DataManager.User;
import DataManager.Video;
import Springmodel.recivemodel;
import Springmodel.typemodel;
import sun.misc.BASE64Decoder;

@Controller
public class SpringmvcController {
	private SessionFactory sf=new Configuration().configure().buildSessionFactory();
	private Session session;
	private Transaction tx;
	@RequestMapping(value="Comment.do",method=RequestMethod.POST)
	@ResponseBody
	//请求对应视频的所有评论
	//需发送一个json对象，json对象内只有一个元素id
	public List<Comment> commentrecive(@RequestBody recivemodel recive) {
		int id=recive.getId();
		System.out.println(id);
		//初始化hibernate管理类
		Manager manager=new Manager();
		//建立返回都评论都容器
		List<Comment> response=new ArrayList<>();
		//查询Comment表内的所有数据
		List<Comment> list=manager.query("Comment");
		for(Comment comment:list) {
			//将评论内的与服务器接收的id相同的评论加入容器
			if(comment.getItem()==id) {
				response.add(comment);
			}
		}
		//如果容器为空则返回null
		if(response.isEmpty()) {
			return null;
		}
		System.out.println(list);
		return response;
	}
	@RequestMapping(value="addlikes.do",method=RequestMethod.POST)
	@ResponseBody
	//对图片、视频或评论点赞，需发送一个包含id和type两个关键字对json对象，int id,String type
	public HashMap<String,String> addlike(@RequestBody typemodel model) {
		int id=model.getId();
		String type=model.getType();
		HashMap<String,String> map=new HashMap<>();
		//开启hibernate事务
		try {
		session=sf.openSession();
		tx=session.beginTransaction();
		//通过type关键字判断是对图片、视频或评论中对哪一个对操作,如果成功则返回yes
		if(type.equals("Comment")) {
			Comment t=(Comment)session.get(Comment.class, id);
			t.setLikes(t.getLikes()+1);
			tx.commit();
			map.put("ifsuccess", "yes");
			return map;
		}
		else if(type.equals("Image")) {
			Image t=(Image)session.get(Image.class, id);
			t.setLikes(t.getLikes()+1);
			tx.commit();
			map.put("ifsuccess", "yes");
			return map;
		}
		else if(type.equals("Video")) {
			Video t=(Video)session.get(Video.class, id);
			t.setLikes(t.getLikes()+1);
			tx.commit();
			map.put("ifsuccess", "yes");
			return map;
		}
		}catch(Exception e) {
			map.put("ifsuccess", "no");
			return map;
		}finally {
			session.close();
		}
		//操作失败
		map.put("ifsuccess", "no");
		return map;
	}
	@RequestMapping(value="usermodifyall.do",method=RequestMethod.POST)
	@ResponseBody
	//用户个人信息更改
	public HashMap<String,String> usermodifyall(@RequestBody User user){
		HashMap<String,String> map=new HashMap<>();
		Manager manager=new Manager();
		try {
			session=sf.openSession();
			tx=session.beginTransaction();
			//从数据库中获取用户更改前的数据
			User aduser=(User)session.get(User.class, user.getId());
			//判断用户是否要更改用户头像
			if(user.getHeadurl()!=null) {
				//根据传过来的string转回为二进制图片信息
				byte[] image=new BASE64Decoder().decodeBuffer(user.getHeadurl());
				//覆盖用户原来的图片信息
				BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(Global.path+aduser.getHeadurl()));
				out.write(image);
				out.flush();
				out.close();
			}
			user.setHeadurl(aduser.getHeadurl());
			//用户更新后的信息上传数据库并判断更新是否成功
			if(manager.alter(user)) {
				map.put("ifsuccess", "true");
			}
			else {
				map.put("ifsuccess", "false");
			}
		}catch(Exception e) {
			map.put("ifsuccess", "false");
		}finally {
			session.close();
		}
		return map;
	}
	@RequestMapping(value="myvideo.do",method=RequestMethod.POST)
	@ResponseBody
	//返回用户发布的所有视频信息
	public List<Video> myvideo(@RequestBody recivemodel model){
		List<Video> list=new ArrayList<>();
		//用户id
		int id=model.getId();
		session=sf.openSession();
		//根据用户id查询数据库
		list=(List<Video>)session.createQuery("from Video where owner=?").setParameter(0, id).list();
		session.close();
		return list;
	}
	@RequestMapping(value="myimage.do",method=RequestMethod.POST)
	@ResponseBody
	//返回用户所有图片信息
	public List<Image> myimage(@RequestBody recivemodel model){
		List<Image> list=new ArrayList<>();
		//用户id
		int id=model.getId();
		session=sf.openSession();
		//根据用户id查询数据库
		list=(List<Image>)session.createQuery("from Image where owner=?").setParameter(0, id).list();
		session.close();
		return list;
	}
}
