package Socks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Random;

import DataManager.Global;
import DataManager.Manager;
import DataManager.Video;
import net.sf.json.JSONObject;

public class Videorecive implements Runnable {
	InputStream in;
	OutputStream out;
	Socket client;
	Video video;
	File file;
	public Videorecive(Socket client) {
		this.client=client;
	}
	private boolean init(JSONObject obj) {
		video=new Video();
		Random random=new Random();
		Manager manager=new Manager();
		//视频上传用户的id（唯一）
		video.setOwner(obj.getInt("owner"));
		//上传的视频的名字
		video.setVideoname(obj.getString("videoname"));
		video.setLikes(1);
		int count=random.nextInt();
		//上传的视频在数据库中的id（唯一）
		while(manager.get(Video.class, count)!=null) {
			count=random.nextInt();
		}
		video.setId(count);
		//视频在服务器中的url地址，可以通过url直接访问
		video.setVideourl("/Video/"+"@"+"video"+video.getId());
		if(obj.has("location")) {
			video.setLocation(obj.getString("location"));
		}
		if(obj.has("abstract")) {
			video.setAbstract_(obj.getString("abstract"));
		}
		file=new File(Global.path+video.getVideourl());
		//将视频插入数据库并返回是否成功
		return manager.insert(video);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			in=client.getInputStream();
			out=client.getOutputStream();
			byte[] data=new byte[3000];
			String json="";
			int len;
			BufferedInputStream buf=new BufferedInputStream(in);
			len=buf.read(data);
			json+=new String(data,0,len);
			JSONObject obj=JSONObject.fromObject(json);
			//视频信息插入数据库是否成功
			if(!init(obj)) {
				throw new IOException();
			}
			PrintWriter write=new PrintWriter(out);
			//告诉app服务器准备好接收视频
			write.write("ready to recive video");
			write.flush();
			FileOutputStream videoout=new FileOutputStream(file);
			//准备读取的视频的大小
			int size=obj.getInt("size");
			while(size>0) {
				len=buf.read(data);
				videoout.write(data, 0, len);
				size-=len;
				videoout.flush();
			}
			//视频上传成功
			write.write("seuccess");
			write.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
