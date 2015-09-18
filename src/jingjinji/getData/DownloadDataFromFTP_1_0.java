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
 * Java自带的API对FTP的操作
 * 
 * @Jdk:version 1.7
 * @Title:Ftp.java
 * @author: boonya
 * @notice: 貌似此方法有个缺陷，不能操作大文件
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
		 * 本地文件名
		 */
		private String localfilename;
		/**
		 * 
		 * 远程文件名
		 */
		private String remotefilename;
		/**
		 * 
		 * FTP客户端
		 */
		private FtpClient ftpClient;

		/**
		 * 服务器连接
		 * 
		 * @param ip
		 *            服务器IP
		 * @param port
		 *            服务器端口
		 * @param user
		 *            用户名
		 * @param password
		 *            密码
		 * @param path
		 *            服务器路径
		 * @throws FtpProtocolException
		 * 
		 */
		public void connectServer(String ip, int port, String user,
				String password, String path) throws FtpProtocolException {
			try {
				/* ******连接服务器的两种方法****** */
				// 第一种方法
				/*
				 * ftpClient =FtpClient.create(); ftpClient.connect(new
				 * InetSocketAddress(ip, port));
				 */
				// 第二种方法
				ftpClient = FtpClient.create(ip);
				ftpClient.login(user, null, password);
				// 设置成2进制传输
				ftpClient.setBinaryType();
				System.out.println("login success!");

				if (path.length() != 0) {
					// 把远程系统上的目录切换到参数path所指定的目录
					ftpClient.changeDirectory(path);
				}
				ftpClient.setBinaryType();
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}

		}

		public void deleteFile(String serverName, String remotePath, String fileName) {

			// 连接至服务器

			try {
				// 删除文件
				ftpClient.deleteFile(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (FtpProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				// 登出服务器并断开连接
				if (ftpClient.isConnected()) {
					try {
						// 断开连接
						ftpClient.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}

		/**
		 * 关闭连接
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
		 * 上传文件
		 * 
		 * @param localFile
		 *            本地文件
		 * @param remoteFile
		 *            远程文件
		 * @throws FtpProtocolException
		 */
		public void upload(String localFile, String remoteFile)
				throws FtpProtocolException {
			this.localfilename = localFile;
			this.remotefilename = remoteFile;
			TelnetOutputStream os = null;
			FileInputStream is = null;
			try {
				// 将远程文件加入输出流中
				os = (TelnetOutputStream) ftpClient.putFileStream(
						this.remotefilename, true);

				// 获取本地文件的输入流
				File file_in = new File(this.localfilename);
				is = new FileInputStream(file_in);

				// 创建一个缓冲区
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
				// 连接至服务器
				result = false;
				// 获取文件操作目录下所有文件名称
				Iterator<FtpDirEntry> remoteNames = ftpClient.listFiles(remotePath);
				// 循环比对文件名称，判断是否含有当前要下载的文件名
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
				// 登出服务器并断开连接
				try {
					// 断开连接
					ftpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return result;

		}

		/**
		 * 
		 * 下载文件
		 * 
		 * @param remoteFile
		 *            远程文件路径(服务器端)
		 * @param localFile
		 *            本地文件路径(客户端)
		 * @throws FtpProtocolException
		 * 
		 */
		public void download(String remoteFile, String localFile)
				throws FtpProtocolException {
			TelnetInputStream is = null;
			FileOutputStream os = null;
			try {
				System.out.println(remoteFile);

				// 获取远程机器上的文件filename，借助TelnetInputStream把该文件传送到本地。
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
		 * 函数入口
		 * 
		 * @param agrs
		 */
		public static void main(String agrs[]) {

			String filepath[] = { "RTIC_CU_BEJ_G1_20150916114503.txt" };
			String localfilepath[] = { "C:\\1.txt" };
			DownloadDataFromFTP_1_0 ftp = new DownloadDataFromFTP_1_0();
			/*
			 * 使用默认的端口号、用户名、密码以及根目录连接FTP服务器
			 */
			try {
				ftp.connectServer("120.52.8.202", 10004, "jin", "jin2015",
						"/rtic");
			} catch (FtpProtocolException e) {
				e.printStackTrace();
			}
			// 下载
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

