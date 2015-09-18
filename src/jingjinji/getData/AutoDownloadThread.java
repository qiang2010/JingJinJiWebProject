package jingjinji.getData;

import java.net.ConnectException;
import java.util.TimerTask;



public class AutoDownloadThread implements Runnable {

	 static FTPFilesDownloader downloader  = new FTPFilesDownloader();

	 private long timeInterval = 60*1000; //每隔两分钟执行一次 
	@Override
	public void run() {
		System.out.println("downloading...");
		try {
			downloader.downloadFiles("/rtic/", "F:/ftp_rtic/");
			Thread.sleep(timeInterval);
		} catch (ConnectException e) {
			//e.printStackTrace();
			System.err.println("连接错误，将重新启动线程。" );
//			do{
//				downloader = new FTPFilesDownloader();
//			}while(!downloader.loginCheck);
//			System.out.println("新连接已经建立。");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
