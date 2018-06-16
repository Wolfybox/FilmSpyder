package Services;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DataManager.Manager;
import DataManager.User;
import net.sf.json.JSONObject;


/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"utf-8"));
		StringBuffer sb = new StringBuffer("");  
        String temp="";  
        while ((temp = br.readLine()) != null) {  
            sb.append(temp);  
        }
        JSONObject json=JSONObject.fromObject(sb.toString());
        System.out.println(json);
        //用户名
        String name=json.getString("username");
        //用户输入密码
        String password=json.getString("password");
        response.setContentType("text/json; charset=UTF-8");
		PrintWriter writer = response.getWriter();
	    HashMap map=new HashMap();
	    Manager dataManager=new Manager();
	    JSONObject j;
	    List<User> list=dataManager.query("User");
	    for(User u:list){
	    	//如果用户的用户名与密码都正确则返回以下信息
	    	if(u.getUsername().equals(name)&&u.getPassword().equals(password)){
	    		map.put("ifconfig", "true");
	    		map.put("id", u.getId());
	    		map.put("username",u.getUsername());
	    		map.put("sex", u.getSex());
	    		map.put("phone", u.getPhone());
	    		map.put("head", u.getHeadurl());
	    		map.put("emil", u.getEmil());
	    		map.put("birt", u.getBirt());
	    		map.put("city", u.getCity());
	    		j=JSONObject.fromObject(map);
	    		writer.println(j);
	    		return;
	    	}
	    }
	    //用户名或密码不正确
	    map.put("ifconfig", "fales");
	    j=JSONObject.fromObject(map);
	    writer.println(j);
	}

}
