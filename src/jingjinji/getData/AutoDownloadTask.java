package jingjinji.getData;

import java.net.ConnectException;
import java.util.TimerTask;



public class AutoDownloadTask extends TimerTask {

	 static FTPFilesDownloader downloader  = new FTPFilesDownloader();

	@Override
	public void run() {
		System.out.println("downloading...");
		try {
			downloader.downloadFiles("/rtic/", "F:/ftp_rtic/");
		} catch (ConnectException e) {
			//e.printStackTrace();
			System.err.println("���Ӵ��󣬽����½�������" );
			do{
				downloader = new FTPFilesDownloader();
			}while(!downloader.loginCheck);
			System.out.println("�������Ѿ�������");
		}
	}

}
