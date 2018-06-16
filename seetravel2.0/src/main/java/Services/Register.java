package Services;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DataManager.Global;
import DataManager.Manager;
import DataManager.User;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;


/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
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
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF-8"));
		StringBuffer sb = new StringBuffer("");  
        String temp="";  
        while ((temp = br.readLine()) != null) {  
            sb.append(temp);  
        }
        JSONObject json=JSONObject.fromObject(sb.toString());
        String username=json.getString("username");
        //用户头像利用BASE64Encoder转化的String
        String image=json.getString("head");
        Manager dataManager=new Manager();
        User user=new User();
        user.setUsername(username);
        user.setPassword(json.getString("password"));
        //是否包含性别
        if(json.has("sex")) {
        	user.setSex(json.getString("sex"));
        }
        //是否包含生日
        if(json.has("bird")) {
        	user.setBirt(json.getString("birt"));
        }
        //是否包含邮件
        if(json.has("emil")) {
        	user.setEmil(json.getString("emil"));
        }
        //是否包含用户所在城市
        if(json.has("city")) {
        	user.setCity(json.getString("city"));
        }
        int count=dataManager.count("User");
        //count表示新用户的id（唯一）
        count++;
        user.setId(count);
        //用户头像在服务器中的url地址，可直接通过url获取用户头像
        user.setHeadurl("/Head/"+"@"+"head"+user.getId());
        BufferedOutputStream outputStream=new BufferedOutputStream(new FileOutputStream(Global.path+user.getHeadurl()));
        byte[] imageout=new BASE64Decoder().decodeBuffer(image);
        outputStream.write(imageout);
        outputStream.flush();
        outputStream.close();
        response.setContentType("text/json; charset=UTF-8");
        PrintWriter writer = response.getWriter();
        //讲用户注册信息插入数据库，若失败则返回insert erro
        if(!dataManager.insert(user)) {
        	writer.println("insert erro");
        	writer.flush();
        	return;
        }
        //用户注册成功
        writer.println("success");
        writer.flush();
	}

}
