package sockettext;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import net.sf.json.JSONObject;

public class sockettext {
	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		HashMap map=new HashMap();
		map.put("owner", 1);
		File file=new File("/Users/lijiahui/Desktop/W.png");
		Socket s=new Socket("localhost", 8798);
		BufferedInputStream in=new BufferedInputStream(s.getInputStream());
		InputStream buf=new FileInputStream("/Users/lijiahui/Desktop/W.png");
		map.put("size",file.length());
		//BufferedInputStream buf=new BufferedInputStream(new FileInputStream("/Users/lijiahui/Desktop/W.png"));
		JSONObject j=JSONObject.fromObject(map);
		BufferedOutputStream out=new BufferedOutputStream(s.getOutputStream());
		out.write(j.toString().getBytes());
		out.flush();
		byte[] b=new byte[10240];
		String temp="";
		String e="";
		int len;
		len=in.read(b);
		temp+=new String(b, 0, len);
		System.out.println(temp);
		b=new byte[1024];
		while((buf.read(b))!=-1) {
			out.write(b);
			out.flush();
		}
		len=in.read(b);
		System.out.println(new String(b,0,len));
	}
}
