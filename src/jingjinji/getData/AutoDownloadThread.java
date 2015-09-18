package jingjinji.getData;

import java.net.ConnectException;
import java.util.TimerTask;



public class AutoDownloadThread implements Runnable {

	 static FTPFilesDownloader downloader  = new FTPFilesDownloader();

	 private long timeInterval = 60*1000; //ÿ��������ִ��һ�� 
	@Override
	public void run() {
		System.out.println("downloading...");
		try {
			downloader.downloadFiles("/rtic/", "F:/ftp_rtic/");
			Thread.sleep(timeInterval);
		} catch (ConnectException e) {
			//e.printStackTrace();
			System.err.println("���Ӵ��󣬽����������̡߳�" );
//			do{
//				downloader = new FTPFilesDownloader();
//			}while(!downloader.loginCheck);
//			System.out.println("�������Ѿ�������");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
