package Services;

import java.io.BufferedReader;
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

import DataManager.Image;
import DataManager.Manager;
import DataManager.Video;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class Download
 */
@WebServlet("/Download")
public class Download extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Download() {
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
        Manager manager=new Manager();
        response.setContentType("text/json; charset=UTF-8");
		PrintWriter writer = response.getWriter();
	    HashMap map=new HashMap();
	    JSONObject j;
	    //所要下载的视频或图片的id
	    int id=json.getInt("ID");
	    //视频下载或者图片下载
        String type=json.getString("type");
        if(type.equals("Image")) {
        	List<Image> list=manager.query(type);
        	for(Image image:list) {
        		if(image.getId()==id) {
        			//如果是图片请求时返回的json内容
        			map.put("id", image.getId());
        			map.put("url", image.getImageurl());
        			map.put("owner", image.getOwner());
        			map.put("likes", image.getLikes());
        			map.put("location", image.getLocation());
        			break;
        		}
        	}
        }
        else {
        	List<Video> list=manager.query(type);
        	for(Video video:list) {
        		if(video.getId()==id) {
        			//如果是视频请求时返回的json对象内容
        			map.put("id", video.getId());
        			map.put("url", video.getVideourl());
        			map.put("owner",video.getOwner());
        			map.put("likes", video.getLikes());
        			map.put("location",video.getLocation());
        			map.put("videoname", video.getVideoname());
        			map.put("abstract", video.getAbstract_());
        			break;
        		}
        	}
        }
        j=JSONObject.fromObject(map);
        writer.println(j);
	}

}
