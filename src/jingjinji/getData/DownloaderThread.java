package jingjinji.getData;

import java.util.Timer;

public class DownloaderThread {

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Timer timer  = new Timer();
		timer.schedule(new AutoDownloadTask(),30000);
	}

}
