package Socks;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Imagesockserves extends Thread{
	ServerSocket server;
	ExecutorService cachedThreadPool;
	{
		try {
			server=new ServerSocket(8798);
			cachedThreadPool=Executors.newCachedThreadPool();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("port erro");
			e.printStackTrace();
		}
	}
	public void run() {
		while(true) {
			try {
				Socket client=server.accept();
				cachedThreadPool.execute(new Imagerecive(client));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
