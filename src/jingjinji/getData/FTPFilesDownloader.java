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
 *  ʹ��apache ftp ����Զ��ftp�������ļ�
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
            // FTP���������ӻش�  
            int reply = this.ftpClient.getReplyCode();  
            if (!FTPReply.isPositiveCompletion(reply)) {  
                this.ftpClient.disconnect();  
                System.out.println("��¼FTP����ʧ�ܣ�");  
                return isLogin;  
            }  
            this.ftpClient.login(this.user, this.pass);  
            // ���ô���Э��  
            this.ftpClient.enterLocalPassiveMode();  
            this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  
            System.out.println("�ɹ���½FTP������");  
            isLogin = true;  
            
        }catch (Exception e) {  
            e.printStackTrace();  
            System.out.println(this.user + "��¼FTP����ʧ�ܣ�" + e.getMessage());  
        }  
        this.ftpClient.setBufferSize(1024 * 2);  
        this.ftpClient.setDataTimeout(6000 * 1000);  
        return isLogin;  
	}
	  
    /** 
     * @�˳��رշ��������� 
     * */  
    public void ftpLogOut() {  
        if (null != this.ftpClient && this.ftpClient.isConnected()) {  
            try {  
                boolean reuslt = this.ftpClient.logout();// �˳�FTP������  
                if (reuslt) {  
                    System.out.println("�ɹ��˳�������");  
                }  
            } catch (IOException e) {  
                e.printStackTrace();  
                System.out.println("�˳�FTP�������쳣��" + e.getMessage());  
            } finally {  
                try {  
                    this.ftpClient.disconnect();// �ر�FTP������������  
                } catch (IOException e) {  
                    e.printStackTrace();  
                    System.out.println("�ر�FTP�������������쳣��");  
                }  
            }  
        }  
    }  
	/**
	 * ��ȡftp��ָ���ļ���������ļ��б�
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
	 * ��ȡftp��ָ���ļ���������ļ��б�
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
	private long timeInterval = 5*60*60*1000; // ���Сʱ֮ǰ���ļ�
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
//					// ɾ���ļ�,ʮ��Сʱ��ǰ���ļ�ɾ����
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
	
 
	
	
	// ���ļ���������ʱ��
	static public String fileNameToTime(String name){
		//String test = "RTIC_CU_BEJ_G1_20150917114003.txt";
		if(name == null || name.indexOf("_")==-1) return null;
		return name.substring(name.lastIndexOf("_")+1,name.length()-6)+"00";
		
	}
	
	// 20150917114000 to long ʱ���
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

