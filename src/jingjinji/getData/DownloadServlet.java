package jingjinji.getData;

import java.util.Timer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;


/**
 * 
 * �������ʱ�򣬿��Խ�����ĳ�servlet��Ȼ��web app������ʱ���Զ�ʵ��������
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

	private long timeInterval = 120; //ÿ��������ִ��һ�� 
	
	@Override
	public void init() throws ServletException {
//		super.init();
//		Timer timer  = new Timer();
//		timer.schedule(new AutoDownloadTask(),timeInterval);
	}
}
