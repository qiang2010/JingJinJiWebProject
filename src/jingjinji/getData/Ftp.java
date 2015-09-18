package jingjinji.getData;


import java.io.BufferedInputStream;  
import java.io.BufferedOutputStream;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.util.TimeZone;  
import org.apache.commons.net.ftp.FTPClient;  
import org.apache.commons.net.ftp.FTPClientConfig;  
import org.apache.commons.net.ftp.FTPFile;  
import org.apache.commons.net.ftp.FTPReply;  
  
public class Ftp {  
    private FTPClient ftpClient;  
    private String strIp;  
    private int intPort;  
    private String user;  
    private String password;  
  
    /* * 
     * Ftp���캯�� 
     */  
    public Ftp(String strIp, int intPort, String user, String Password) {  
        this.strIp = strIp;  
        this.intPort = intPort;  
        this.user = user;  
        this.password = Password;  
        this.ftpClient = new FTPClient();  
    }  
    /** 
     * @return �ж��Ƿ����ɹ� 
     * */  
    public boolean ftpLogin() {  
        boolean isLogin = false;  
        FTPClientConfig ftpClientConfig = new FTPClientConfig();  
        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());  
        this.ftpClient.setControlEncoding("GBK");  
        this.ftpClient.configure(ftpClientConfig);  
        try {  
            if (this.intPort > 0) {  
                this.ftpClient.connect(this.strIp, this.intPort);  
            } else {  
                this.ftpClient.connect(this.strIp);  
            }  
            // FTP���������ӻش�  
            int reply = this.ftpClient.getReplyCode();  
            if (!FTPReply.isPositiveCompletion(reply)) {  
                this.ftpClient.disconnect();  
                System.out.println("��¼FTP����ʧ�ܣ�");  
                return isLogin;  
            }  
            this.ftpClient.login(this.user, this.password);  
            // ���ô���Э��  
            this.ftpClient.enterLocalPassiveMode();  
            this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  
            System.out.println("��ϲ" + this.user + "�ɹ���½FTP������");  
            isLogin = true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            System.out.println(this.user + "��¼FTP����ʧ�ܣ�" + e.getMessage());  
        }  
        this.ftpClient.setBufferSize(1024 * 2);  
        this.ftpClient.setDataTimeout(30 * 1000);  
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
  
    /*** 
     * �ϴ�Ftp�ļ� 
     * @param localFile �����ļ� 
     * @param romotUpLoadePath�ϴ�������·�� - Ӧ����/���� 
     * */  
    public boolean uploadFile(File localFile, String romotUpLoadePath) {  
        BufferedInputStream inStream = null;  
        boolean success = false;  
        try {  
            this.ftpClient.changeWorkingDirectory(romotUpLoadePath);// �ı乤��·��  
            inStream = new BufferedInputStream(new FileInputStream(localFile));  
            System.out.println(localFile.getName() + "��ʼ�ϴ�.....");  
            success = this.ftpClient.storeFile(localFile.getName(), inStream);  
            if (success == true) {  
                System.out.println(localFile.getName() + "�ϴ��ɹ�");  
                return success;  
            }  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
            System.out.println(localFile + "δ�ҵ�");  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (inStream != null) {  
                try {  
                    inStream.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return success;  
    }  
  
    /*** 
     * �����ļ� 
     * @param remoteFileName   �������ļ����� 
     * @param localDires ���ص������Ǹ�·���� 
     * @param remoteDownLoadPath remoteFileName���ڵ�·�� 
     * */  
  
    public boolean downloadFile(String remoteFileName, String localDires,  
            String remoteDownLoadPath) {  
        String strFilePath = localDires + remoteFileName;  
        BufferedOutputStream outStream = null;  
        boolean success = false;  
        try {  
            this.ftpClient.changeWorkingDirectory(remoteDownLoadPath);  
            outStream = new BufferedOutputStream(new FileOutputStream(  
                    strFilePath));  
            System.out.println(remoteFileName + "��ʼ����....");  
            success = this.ftpClient.retrieveFile(remoteFileName, outStream);  
            if (success == true) {  
                System.out.println(remoteFileName + "�ɹ����ص�" + strFilePath);  
                return success;  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
            System.out.println(remoteFileName + "����ʧ��");  
        } finally {  
            if (null != outStream) {  
                try {  
                    outStream.flush();  
                    outStream.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        if (success == false) {  
            System.out.println(remoteFileName + "����ʧ��!!!");  
        }  
        return success;  
    }  
  
    /*** 
     * @�ϴ��ļ��� 
     * @param localDirectory 
     *            �����ļ��� 
     * @param remoteDirectoryPath 
     *            Ftp ������·�� ��Ŀ¼"/"���� 
     * */  
    public boolean uploadDirectory(String localDirectory,  
            String remoteDirectoryPath) {  
        File src = new File(localDirectory);  
        try {  
            remoteDirectoryPath = remoteDirectoryPath + src.getName() + "/";  
            this.ftpClient.makeDirectory(remoteDirectoryPath);  
            // ftpClient.listDirectories();  
        } catch (IOException e) {  
            e.printStackTrace();  
            System.out.println(remoteDirectoryPath + "Ŀ¼����ʧ��");  
        }  
        File[] allFile = src.listFiles();  
        for (int currentFile = 0; currentFile < allFile.length; currentFile++) {  
            if (!allFile[currentFile].isDirectory()) {  
                String srcName = allFile[currentFile].getPath().toString();  
                uploadFile(new File(srcName), remoteDirectoryPath);  
            }  
        }  
        for (int currentFile = 0; currentFile < allFile.length; currentFile++) {  
            if (allFile[currentFile].isDirectory()) {  
                // �ݹ�  
                uploadDirectory(allFile[currentFile].getPath().toString(),  
                        remoteDirectoryPath);  
            }  
        }  
        return true;  
    }  
  
    /*** 
     * @�����ļ��� 
     * @param localDirectoryPath���ص�ַ 
     * @param remoteDirectory Զ���ļ��� 
     * */  
    public boolean downLoadDirectory(String localDirectoryPath,String remoteDirectory) {  
        try {  
            String fileName = new File(remoteDirectory).getName();  
            localDirectoryPath = localDirectoryPath + fileName + "//";  
            new File(localDirectoryPath).mkdirs();  
            FTPFile[] allFile = this.ftpClient.listFiles(remoteDirectory);  
            for (int currentFile = 0; currentFile < allFile.length; currentFile++) {  
                if (!allFile[currentFile].isDirectory()) {  
                    downloadFile(allFile[currentFile].getName(),localDirectoryPath, remoteDirectory);  
                }  
            }  
            for (int currentFile = 0; currentFile < allFile.length; currentFile++) {  
                if (allFile[currentFile].isDirectory()) {  
                    String strremoteDirectoryPath = remoteDirectory + "/"+ allFile[currentFile].getName();  
                    downLoadDirectory(localDirectoryPath,strremoteDirectoryPath);  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
            System.out.println("�����ļ���ʧ��");  
            return false;  
        }  
        return true;  
    }  
    // FtpClient��Set �� Get ����  
    public FTPClient getFtpClient() {  
        return ftpClient;  
    }  
    public void setFtpClient(FTPClient ftpClient) {  
        this.ftpClient = ftpClient;  
    }  
      
    public static void main(String[] args) throws IOException {  
        Ftp ftp=new Ftp("120.52.8.202",10004,"jin","jin2015");  
        ftp.ftpLogin();  
        //�ϴ��ļ���  
//        ftp.uploadDirectory("d://DataProtemp", "/home/data/");  
        //�����ļ���  
        ftp.downLoadDirectory("D:\\data\\����������\\", "rtic");
        //�����ļ�
//        ftp.downloadFile("RTIC_CU_BEJ_G1_20150916114503.txt", "D:\\data\\����������\\", "rtic");
        ftp.ftpLogOut();          
    }  
}  