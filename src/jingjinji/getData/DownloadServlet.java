package jingjinji.getData;

import java.util.Timer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;


/**
 * 
 * 当部署的时候，可以将该类改成servlet，然后当web app启动的时候，自动实例化该类
 * 
 * 
 * @author jq
 *
 */

public class DownloadServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long timeInterval = 120; //每隔两分钟执行一次 
	
	@Override
	public void init() throws ServletException {
//		super.init();
//		Timer timer  = new Timer();
//		timer.schedule(new AutoDownloadTask(),timeInterval);
	}
}
