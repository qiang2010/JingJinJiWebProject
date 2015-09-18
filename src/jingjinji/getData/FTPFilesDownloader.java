package jingjinji.getData;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPCmd;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
/**
 *  
 *  使用apache ftp 连接远程ftp，下载文件
 *  
 * @author jq
 *
 */
public class FTPFilesDownloader { 
	
	private static FTPClient ftpClient = new FTPClient() ;
	
	private String ip;
	private int port;
	private String user;
	private String pass;
	public boolean loginCheck ;
	public FTPFilesDownloader(String ip,int port,String username,String pass){
		this.ip = ip;
		this.port = port;
		this.user = username;
		this.pass = pass;
		loginCheck = false;
	}
	
	public FTPFilesDownloader()    {
		this.ip = "120.52.8.202";
		this.port = 10004;
		this.user = "jin";
		this.pass = "jin2015";
		loginCheck = false;
		loginCheck = connectFTPServer();
		 
	}
	
	public boolean ftpCommand(){
		
		String args = "RTIC_CU_BEJ_G1_20150917114003.txt";
		try {
		
			ftpClient.changeWorkingDirectory("/rtic/");
			System.out.println(ftpClient.printWorkingDirectory());
			int code = ftpClient.sendCommand(FTPCmd.LIST,args);
			System.out.println(code);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	public boolean connectFTPServer()    {
		boolean isLogin = false;  
        FTPClientConfig ftpClientConfig = new FTPClientConfig();  
        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());  
         ftpClient.setControlEncoding("GBK");  
         ftpClient.configure(ftpClientConfig);  
        try {  
            if (this.port > 0) {  
                 ftpClient.connect(this.ip, this.port);  
            } else {  
                 ftpClient.connect(this.ip);  
            }  
            // FTP服务器连接回答  
            int reply = this.ftpClient.getReplyCode();  
            if (!FTPReply.isPositiveCompletion(reply)) {  
                this.ftpClient.disconnect();  
                System.out.println("登录FTP服务失败！");  
                return isLogin;  
            }  
            this.ftpClient.login(this.user, this.pass);  
            // 设置传输协议  
            this.ftpClient.enterLocalPassiveMode();  
            this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  
            System.out.println("成功登陆FTP服务器");  
            isLogin = true;  
            
        }catch (Exception e) {  
            e.printStackTrace();  
            System.out.println(this.user + "登录FTP服务失败！" + e.getMessage());  
        }  
        this.ftpClient.setBufferSize(1024 * 2);  
        this.ftpClient.setDataTimeout(6000 * 1000);  
        return isLogin;  
	}
	  
    /** 
     * @退出关闭服务器链接 
     * */  
    public void ftpLogOut() {  
        if (null != this.ftpClient && this.ftpClient.isConnected()) {  
            try {  
                boolean reuslt = this.ftpClient.logout();// 退出FTP服务器  
                if (reuslt) {  
                    System.out.println("成功退出服务器");  
                }  
            } catch (IOException e) {  
                e.printStackTrace();  
                System.out.println("退出FTP服务器异常！" + e.getMessage());  
            } finally {  
                try {  
                    this.ftpClient.disconnect();// 关闭FTP服务器的连接  
                } catch (IOException e) {  
                    e.printStackTrace();  
                    System.out.println("关闭FTP服务器的连接异常！");  
                }  
            }  
        }  
    }  
	/**
	 * 获取ftp上指定文件夹下面的文件列表。
	 * @param path
	 * @return
	 */
	public String[] getFTPFileNameList(String path){
		if(ftpClient == null) return null;
		
		try {
			if(!ftpClient.changeWorkingDirectory(path)){
				System.err.println("Change dir failed!");
			}
		//	System.out.println("Change dir:" + ftpClient.printWorkingDirectory());
			 
		//	System.out.println("ftpfilenull "+ftpClient.listFiles().length);
			
			return ftpClient.listNames();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Please check path.");
		}
		
		return null;
	}
	
	/**
	 * 获取ftp上指定文件夹下面的文件列表。
	 * @param path
	 * @return
	 */
	public FTPFile[] getFTPFileList(String path){
		if(ftpClient == null) return null;
		
		try {
			if(!ftpClient.changeWorkingDirectory(path)){
				System.err.println("Change dir failed!");
			}
			return ftpClient.listFiles();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Please check path.");
		}
		
		return null;
	}
	private long timeInterval = 5*60*60*1000; // 五个小时之前的文件
//	private long deleteInterval = 10*60*60*1000;
	
	public void downloadFiles(String ftpPaht,String localPath) throws ConnectException{

		TimeZone.setDefault(TimeZone.getTimeZone("GSM+8"));
		long timeContion = new Date().getTime() - timeInterval;
		
		System.out.println(timeContion);
		//FTPFile[] files = getFTPFileList(ftpPaht);
		  String []fileNames = getFTPFileNameList(ftpPaht);
				  
		System.out.println("Download file number: "+ fileNames.length);		
		BufferedOutputStream outputStream =null;
		
		try {
			System.out.println("current dir:" + ftpClient.printWorkingDirectory());
			File tempLocalFile;
			int count =0 ;
			int newDownFileCount=0;
			long  fileTime;
			String localName;
			for(String  curName:fileNames){
				fileTime = fileNameToStamp(fileNameToTime(curName));
				System.out.println(fileTime);
//				if(fileTime < timeContion) {
//					// 删除文件,十个小时以前的文件删除。
///*
// * 
//					if(fileTime < timeContion-timeInterval){
//						ftpClient.deleteFile(curName);
//					}
//*/	
//					continue;
//				}
				
				localName =localPath+ curName.substring(0,curName.length()-6)+".txt";
				tempLocalFile = new File(localName);
				count++;
				if(tempLocalFile.exists())continue;
				newDownFileCount++;
				outputStream = new BufferedOutputStream(new FileOutputStream(localName));
				boolean done = ftpClient.retrieveFile(curName,outputStream );
				if(done)
					System.out.println("done");
				else{
					System.err.println("download failed!" + localName);
				}
				outputStream.flush();
				outputStream.close();
			}
			System.err.println("count: " + count);
			System.err.println("newDownFileCount: " + newDownFileCount);
		}catch(ConnectException e){
			System.out.println("Connect timeout.");
			throw e;
		}catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
 
	
	
	// 从文件名解析出时间
	static public String fileNameToTime(String name){
		//String test = "RTIC_CU_BEJ_G1_20150917114003.txt";
		if(name == null || name.indexOf("_")==-1) return null;
		return name.substring(name.lastIndexOf("_")+1,name.length()-6)+"00";
		
	}
	
	// 20150917114000 to long 时间戳
	private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	private static Date date;
	public static long fileNameToStamp(String name){
		try {
			date = format.parse(name);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static void main(String[] args) throws ConnectException {
		FTPFilesDownloader downloader = new FTPFilesDownloader("120.52.8.202", 10004, "jin", "jin2015");
		downloader.connectFTPServer();
		downloader.downloadFiles("/rtic/", "F:/ftp_rtic/");
		//downloader.ftpCommand();
		
	}
	
	
	
}

