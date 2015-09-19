package jingjinji.getData;

import java.lang.Thread.State;


/**
 * 
 * 当部署的时候，可以将该类改成servlet，然后当web app启动的时候，自动实例化该类
 * 
 * 
 * @author jq
 *
 */

public class DownloadServlet{
 
	public static void main(String[] args) {
		Thread downloadThread = new Thread(new AutoDownloadThread());
		downloadThread.start();
		do{
			State state = downloadThread.getState();
			if(state == Thread.State.TERMINATED ){
				System.out.println("new Thread....");
				downloadThread = new Thread(new AutoDownloadThread());
				downloadThread.start();
			}else{
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}while(true);
		
	}
	
	
}
