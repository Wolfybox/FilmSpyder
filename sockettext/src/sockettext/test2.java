package sockettext;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import net.sf.json.JSONObject;
import sun.misc.BASE64Encoder;

public class test2 {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			URL url=new URL("http://localhost:8080/seetravel2.0/Register");
			BufferedInputStream buf=new BufferedInputStream(new FileInputStream("/Users/lijiahui/Desktop/We.png"));
			HashMap map=new HashMap();
			String username="llls";
			String password="abc123";
			String image="";
			byte[] b=new byte[1024];
			int len;
			while((len=buf.read(b))!=-1) {
				image+=new BASE64Encoder().encode(b);
			}
			map.put("username", username);
			map.put("password", password);
			map.put("head",image);
			JSONObject json=JSONObject.fromObject(map);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 
			connection.setRequestProperty("accept", "*/*");  
            connection.setRequestProperty("connection", "Keep-Alive");  
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");  
            connection.setConnectTimeout(5000);  
            connection.setReadTimeout(5000); 
            connection.setDoOutput(true);  
            connection.setDoInput(true);  
            connection.setUseCaches(false);   
            connection.setRequestMethod("POST");
            connection.connect();
            OutputStream out=connection.getOutputStream();
            out.write(json.toString().getBytes("UTF-8"));
            out.flush();
            InputStream in=connection.getInputStream();
            buf=new BufferedInputStream(in);
            String temp="";
            while((len=buf.read(b))!=-1) {
            	temp+=new String(b,0,len);
            }
            System.out.println(temp);
            System.out.println("finishs");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
