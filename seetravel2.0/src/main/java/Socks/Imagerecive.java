package Socks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

import DataManager.Global;
import DataManager.Image;
import DataManager.Manager;
import DataManager.Video;
import net.sf.json.JSONObject;

public class Imagerecive implements Runnable{
	InputStream in;
	OutputStream out;
	Socket client;
	Image image;
	File file;
	public Imagerecive(Socket client) {
		this.client=client;
	}
	private boolean init(JSONObject obj) {
		Manager manager=new Manager();
		Random random=new Random();
		//上传用户都id（唯一）
		int i=obj.getInt("owner");
		image=new Image();
		image.setOwner(i);
		image.setLikes(1);
		int count=random.nextInt();
		//上传图片在数据库中都id（唯一）
		while(manager.get(Image.class, count)!=null) {
			count=random.nextInt();
		}
		//上传都图片在服务器中的url，可通过url直接访问
		image.setId(count);
		image.setImageurl("/Image/"+"@"+"Image"+image.getId());
		//上传的图片是否包含位置信息
		if(obj.has("location")) {
			image.setLocation("location");
		}
		file=new File(Global.path+image.getImageurl());
		//上传的图片的信息插入数据库，并返回是否成功
		return manager.insert(image);
	}
	public void run() {
		try {
			in=client.getInputStream();
			out=client.getOutputStream();
			byte[] data=new byte[3000];
			int len=0;
			String json="";
			BufferedInputStream buf=new BufferedInputStream(in);
			len=buf.read(data);
			json+=new String(data,0,len);
			JSONObject obj=JSONObject.fromObject(json);
			//信息插入数据库是否成功
			if(!init(obj)) {
				throw new IOException();
			}
			PrintWriter write=new PrintWriter(out);
			//告诉app服务器准备好接收图片
			write.write("ready to recive Image");
			write.flush();
			FileOutputStream Imageoout=new FileOutputStream(file);
			//size表示将要读取的图片的大小
			int size=obj.getInt("size");
			while(size>0) {
				len=buf.read(data);
				Imageoout.write(data, 0, len);
				size-=len;
				Imageoout.flush();
			}
			//告诉app上传图片成功
			write.write("seuccess");
			write.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			PrintWriter write=new PrintWriter(out);
			write.write("e");
			e.printStackTrace();
		}
	}
}
