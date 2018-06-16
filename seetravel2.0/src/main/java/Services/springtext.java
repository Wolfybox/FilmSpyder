package Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import DataManager.Comment;
import DataManager.Manager;
import DataManager.User;
import Springmodel.typemodel;




@Controller
public class springtext {
	@RequestMapping("test.do")
	@ResponseBody
	public List<Comment> test() {
		/*
		User user=new User();
		user.setId(2);
		user.setEmil("1232");
		User b=new User();
		b.setBirt("97");
		b.setCity("china");
		//User[] a=new User[] {user,b};
		List<User> a=new ArrayList<>();
		a.add(user);
		a.add(b);
		return a;*/
		
		int id=1;
		Manager manager=new Manager();
		List<Comment> response=new ArrayList<>();
		List<Comment> list=manager.query("Comment");
		for(Comment comment:list) {
			if(comment.getItem()==id) {
				response.add(comment);
			}
		}
		return response;
		
	}
	@RequestMapping(value="test2.do")
	@ResponseBody
	public HashMap<String, String> test2(){
		HashMap<String,String> map=new HashMap();
		map.put("hello", "yes");
		return map;
	}
}
