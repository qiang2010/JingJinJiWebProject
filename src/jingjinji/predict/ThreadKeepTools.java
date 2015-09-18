package jingjinji.predict;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class ThreadKeepTools {
	public static boolean isRunning(String processName) {

		BufferedReader bufferedReader = null;
		try {
			Process proc = Runtime.getRuntime().exec(
					"tasklist /FI \"IMAGENAME eq " + processName + "\"");
			bufferedReader = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				// System.out.println(line);
				if (line.contains(processName))
				{
					return true;
				}
			}
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public static void ExcuteThread() throws InterruptedException {
		String strFilePath = "transport.exe";

		 //String strBatPath = "../run.bat";
		String strBatPath = "D:/transport/bin/run.bat";

		while (true) {
			// System.out.println("strFilePath:" + strFilePath);
			boolean RunOrNOT = isRunning(strFilePath);
			System.out.println("RunOrNot:" + RunOrNOT);

			if (!RunOrNOT) {
				try {
					System.out.print("restart program");

					Runtime.getRuntime().exec("cmd /k start " + strBatPath);
					// Runtime.getRuntime().exec(strBatPath);
					System.out.println(" path:" + strBatPath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Thread.sleep(30000);
		}
	}

	public static void main(String[] args) {
		try {
			ExcuteThread();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}