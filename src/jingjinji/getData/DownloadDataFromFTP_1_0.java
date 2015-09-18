package jingjinji.getData;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import sun.net.TelnetInputStream;
import sun.net.TelnetOutputStream;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpDirEntry;
import sun.net.ftp.FtpProtocolException;

/**
 * Java�Դ���API��FTP�Ĳ���
 * 
 * @Jdk:version 1.7
 * @Title:Ftp.java
 * @author: boonya
 * @notice: ò�ƴ˷����и�ȱ�ݣ����ܲ������ļ�
 */

/**
 * 
 * 
 * @author jq
 *
 */
public class DownloadDataFromFTP_1_0 {
		/**
		 * 
		 * �����ļ���
		 */
		private String localfilename;
		/**
		 * 
		 * Զ���ļ���
		 */
		private String remotefilename;
		/**
		 * 
		 * FTP�ͻ���
		 */
		private FtpClient ftpClient;

		/**
		 * ����������
		 * 
		 * @param ip
		 *            ������IP
		 * @param port
		 *            �������˿�
		 * @param user
		 *            �û���
		 * @param password
		 *            ����
		 * @param path
		 *            ������·��
		 * @throws FtpProtocolException
		 * 
		 */
		public void connectServer(String ip, int port, String user,
				String password, String path) throws FtpProtocolException {
			try {
				/* ******���ӷ����������ַ���****** */
				// ��һ�ַ���
				/*
				 * ftpClient =FtpClient.create(); ftpClient.connect(new
				 * InetSocketAddress(ip, port));
				 */
				// �ڶ��ַ���
				ftpClient = FtpClient.create(ip);
				ftpClient.login(user, null, password);
				// ���ó�2���ƴ���
				ftpClient.setBinaryType();
				System.out.println("login success!");

				if (path.length() != 0) {
					// ��Զ��ϵͳ�ϵ�Ŀ¼�л�������path��ָ����Ŀ¼
					ftpClient.changeDirectory(path);
				}
				ftpClient.setBinaryType();
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}

		}

		public void deleteFile(String serverName, String remotePath, String fileName) {

			// ������������

			try {
				// ɾ���ļ�
				ftpClient.deleteFile(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (FtpProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				// �ǳ����������Ͽ�����
				if (ftpClient.isConnected()) {
					try {
						// �Ͽ�����
						ftpClient.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}

		/**
		 * �ر�����
		 * 
		 */

		public void closeConnect() {
			try {
				ftpClient.close();
				System.out.println("disconnect success");
			} catch (IOException ex) {
				System.out.println("not disconnect");
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}
		}

		/**
		 * 
		 * �ϴ��ļ�
		 * 
		 * @param localFile
		 *            �����ļ�
		 * @param remoteFile
		 *            Զ���ļ�
		 * @throws FtpProtocolException
		 */
		public void upload(String localFile, String remoteFile)
				throws FtpProtocolException {
			this.localfilename = localFile;
			this.remotefilename = remoteFile;
			TelnetOutputStream os = null;
			FileInputStream is = null;
			try {
				// ��Զ���ļ������������
				os = (TelnetOutputStream) ftpClient.putFileStream(
						this.remotefilename, true);

				// ��ȡ�����ļ���������
				File file_in = new File(this.localfilename);
				is = new FileInputStream(file_in);

				// ����һ��������
				byte[] bytes = new byte[1024];
				int c;
				while ((c = is.read(bytes)) != -1) {
					os.write(bytes, 0, c);
				}
				System.out.println("upload success");
			} catch (IOException ex) {
				System.out.println("not upload");
				ex.printStackTrace();
				throw new RuntimeException(ex);
			} finally {
				try {
					if (is != null) {
						is.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (os != null) {
							os.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		public boolean checkFile(String remotePath, String fileName) {
			boolean result = false;
			try {
				// ������������
				result = false;
				// ��ȡ�ļ�����Ŀ¼�������ļ�����
				Iterator<FtpDirEntry> remoteNames = ftpClient.listFiles(remotePath);
				// ѭ���ȶ��ļ����ƣ��ж��Ƿ��е�ǰҪ���ص��ļ���
				while (remoteNames.hasNext()) {
					FtpDirEntry remoteName = remoteNames.next();

					if (fileName.equals(remoteName)) {
						result = true;
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (FtpProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				// �ǳ����������Ͽ�����
				try {
					// �Ͽ�����
					ftpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return result;

		}

		/**
		 * 
		 * �����ļ�
		 * 
		 * @param remoteFile
		 *            Զ���ļ�·��(��������)
		 * @param localFile
		 *            �����ļ�·��(�ͻ���)
		 * @throws FtpProtocolException
		 * 
		 */
		public void download(String remoteFile, String localFile)
				throws FtpProtocolException {
			TelnetInputStream is = null;
			FileOutputStream os = null;
			try {
				System.out.println(remoteFile);

				// ��ȡԶ�̻����ϵ��ļ�filename������TelnetInputStream�Ѹ��ļ����͵����ء�
				is = (TelnetInputStream) ftpClient.getFileStream(remoteFile);
				File file_in = new File(localFile);
				os = new FileOutputStream(file_in);

				byte[] bytes = new byte[1024];
				int c;
				while ((c = is.read(bytes)) != -1) {
					os.write(bytes, 0, c);
				}
				System.out.println("download success");
			} catch (IOException ex) {
				System.out.println("not download");
				ex.printStackTrace();
				throw new RuntimeException(ex);
			} finally {
				try {
					if (is != null) {
						is.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (os != null) {
							os.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		/**
		 * �������
		 * 
		 * @param agrs
		 */
		public static void main(String agrs[]) {

			String filepath[] = { "RTIC_CU_BEJ_G1_20150916114503.txt" };
			String localfilepath[] = { "C:\\1.txt" };
			DownloadDataFromFTP_1_0 ftp = new DownloadDataFromFTP_1_0();
			/*
			 * ʹ��Ĭ�ϵĶ˿ںš��û����������Լ���Ŀ¼����FTP������
			 */
			try {
				ftp.connectServer("120.52.8.202", 10004, "jin", "jin2015",
						"/rtic");
			} catch (FtpProtocolException e) {
				e.printStackTrace();
			}
			// ����
			for (int i = 0; i < filepath.length; i++) {
				try {
					ftp.download(filepath[i], localfilepath[i]);
				} catch (FtpProtocolException e) {
					e.printStackTrace();
				}
			}

			ftp.closeConnect();
		}

	}

