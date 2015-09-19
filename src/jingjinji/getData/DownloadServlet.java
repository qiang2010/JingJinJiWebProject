package jingjinji.getData;

import java.lang.Thread.State;


/**
 * 
 * �������ʱ�򣬿��Խ�����ĳ�servlet��Ȼ��web app������ʱ���Զ�ʵ��������
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
