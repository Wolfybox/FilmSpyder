package sockettext;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class http {
	public static void main(String[] args) throws ProtocolException {
		// TODO Auto-generated method stub
		try {
			URL url=new URL("http://localhost:8080/seetravel2.0/test2.do");
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 
			connection.setRequestProperty("accept", "*/*");  
            connection.setRequestProperty("connection", "Keep-Alive");  
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");  
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");  
            connection.setConnectTimeout(5000);  
            connection.setReadTimeout(500000); 
            connection.setDoOutput(true);  
            connection.setDoInput(true);  
            connection.setUseCaches(false);   
            connection.setRequestMethod("POST");
            connection.connect();
            String temp="";
            JSONObject json=new JSONObject();
            json.put("id", 1);
            OutputStream out=connection.getOutputStream();
            BufferedOutputStream bufo=new BufferedOutputStream(out);
            //out.write(json.toString().getBytes("UTF-8"));
            bufo.write(json.toString().getBytes("UTF-8"));
            bufo.flush();
            int len;

            InputStream in=connection.getInputStream();
            BufferedInputStream buf=new BufferedInputStream(in);
            byte[] b=new byte[1024];
            while((len=buf.read(b))!=-1) {
            	temp+=new String(b,0,len);
            }
            System.out.println(temp);
            if(temp.equals("")) {
            	System.out.println("no thing");
            	return;
            }
            JSONArray arry=JSONArray.fromObject(temp);
            System.out.println(arry.size());
            System.out.println(arry);
            
            System.out.println("finish");
            //System.out.println(json+"/t"+json.getString("ifconfig"));
            
           
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
