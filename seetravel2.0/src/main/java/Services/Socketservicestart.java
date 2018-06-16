package Services;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import Socks.Imagesockserves;
import Socks.Videosockserves;

/**
 * Application Lifecycle Listener implementation class Socketservicestart
 *
 */
@WebListener
public class Socketservicestart implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public Socketservicestart() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    	//视频上传监听线程
    	Videosockserves video=new Videosockserves();
    	//图片上传监听线程
    	Imagesockserves image=new Imagesockserves();
    	//设置为守护线程
    	video.setDaemon(true);
    	image.setDaemon(true);
    	video.start();
    	image.start();
    }
	
}
