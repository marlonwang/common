package net.logvv.ftp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FtpUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(FtpUtils.class);
	
	// 文件编码
	private static String LOCAL_CHARSET = "UTF-8";
	private static String SERVER_CHARSET = "ISO-8859-1";
	
	
	/**
     * 上传文件（可供Action/Controller层使用）
     * @param hostname FTP服务器地址
     * @param port   FTP服务器端口号
     * @param username   FTP登录帐号
     * @param password   FTP登录密码
     * @param pathname   FTP服务器保存目录
     * @param fileName   上传到FTP服务器后的文件名称
     * @param inputStream 输入文件流
     * @return
     */
    public static boolean upload(String hostname, int port, String username, String password, 
    		String pathname, String fileName, InputStream inputStream)
    {
        boolean flag = false;
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding("UTF-8");
        try {
            //连接FTP服务器
            ftpClient.connect(hostname, port);
            //登录FTP服务器
            if(ftpClient.login(username, password))
            {
                //是否成功登录FTP服务器
                int replyCode = ftpClient.getReplyCode();
                if(!FTPReply.isPositiveCompletion(replyCode)){
                    return flag;
                }
                
                // 测试ftp server是否支持utf-8
                if(FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8", "ON"))){
                	LOCAL_CHARSET = "UTF-8";
                }
                ftpClient.setControlEncoding(LOCAL_CHARSET);
                
                /**
                 * active与passive区别： 
                 *   active模式ftp server监听20(数据) 21(命令)端口; passive模式ftp server监听 21(命令) 随机(数据)
                 *   passive适用开启防火墙的情景
                 * 参考：
                 *   http://stackoverflow.com/questions/1699145/what-is-the-difference-between-active-and-passive-ftp
                 */
                ftpClient.enterLocalPassiveMode(); // passive mode
                
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);//ASCII_FILE_TYPE
                
                boolean mkdirs_enter = multiMkdir(pathname,ftpClient);
                if(!mkdirs_enter){
                	logger.info("failed to enter remote dir: {}",pathname);
                	inputStream.close();
                	ftpClient.logout();
                	return flag;
                }
                // notice: filename should not contain remote file path
                fileName = new String(fileName.getBytes(LOCAL_CHARSET),SERVER_CHARSET);
                flag = ftpClient.storeFile(fileName, inputStream);
                logger.info("store file, status:{}",flag);
                inputStream.close();
                ftpClient.logout();
            }else {
				logger.error("ftp login failed.");
				return flag;
			}

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(ftpClient.isConnected()){
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }
     
     
    /**
	 * 上传文件（不可以进行文件的重命名操作）
	 * @param hostname FTP服务器地址
	 * @param port   FTP服务器端口号
	 * @param username   FTP登录帐号
	 * @param password   FTP登录密码
	 * @param pathname   FTP服务器保存目录
	 * @param originfilename 待上传文件的名称（绝对地址）
	 * @return
	 */
	public static boolean upload(String hostname, int port, String username, String password, String pathname, String originfilename){
	    boolean flag = false;
	    try {
	        String fileName = new File(originfilename).getName();
	        InputStream inputStream = new FileInputStream(new File(originfilename));
	        flag = upload(hostname, port, username, password, pathname, fileName, inputStream);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return flag;
	}


	/**
     * 上传文件（可对文件进行重命名）
     * @param hostname FTP服务器地址
     * @param port   FTP服务器端口号
     * @param username   FTP登录帐号
     * @param password   FTP登录密码
     * @param pathname   FTP服务器保存目录
     * @param filename   上传到FTP服务器后的文件名称
     * @param originfilename 待上传文件的名称（绝对地址）
     * @return
     */
    public static boolean uploadRename(String hostname, int port, String username, String password, String pathname, String filename, String originfilename){
        boolean flag = false;
        try {
            InputStream inputStream = new FileInputStream(new File(originfilename));
            flag = upload(hostname, port, username, password, pathname, filename, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
     
    /**
     * 删除文件
     * @param hostname FTP服务器地址
     * @param port   FTP服务器端口号
     * @param username   FTP登录帐号
     * @param password   FTP登录密码
     * @param pathname   FTP服务器保存目录
     * @param filename   要删除的文件名称
     * @return
     */
    public static boolean deleteFile(String hostname, int port, String username, String password, String pathname, String filename){
        boolean flag = false;
        FTPClient ftpClient = new FTPClient();
        try {
            //连接FTP服务器
            ftpClient.connect(hostname, port);
            //登录FTP服务器
            ftpClient.login(username, password);
            //验证FTP服务器是否登录成功
            int replyCode = ftpClient.getReplyCode();
            if(!FTPReply.isPositiveCompletion(replyCode)){
                return flag;
            }
            //切换FTP目录
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.dele(filename);
            ftpClient.logout();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(ftpClient.isConnected()){
                try {
                    ftpClient.logout();
                } catch (IOException e) {
                 
                }
            }
        }
        return flag;
    }
     
    /**
     * 下载文件
     * @param hostname FTP服务器地址
     * @param port   FTP服务器端口号
     * @param username   FTP登录帐号
     * @param password   FTP登录密码
     * @param pathname   FTP服务器文件目录
     * @param filename   文件名称
     * @param localpath 下载后的文件路径
     * @return
     */
    public static boolean downloadFile(String hostname, int port, String username, String password, String pathname, String filename, String localpath){
        boolean flag = false;
        FTPClient ftpClient = new FTPClient();
        try {
            //连接FTP服务器
            ftpClient.connect(hostname, port);
            //登录FTP服务器
            ftpClient.login(username, password);
            
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            //验证FTP服务器是否登录成功
            int replyCode = ftpClient.getReplyCode();
            if(!FTPReply.isPositiveCompletion(replyCode)){
                return flag;
            }

            ftpClient.enterLocalPassiveMode(); // 不设置会一直处于连接等待状态
            
            //切换FTP目录
            ftpClient.changeWorkingDirectory(pathname);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for(FTPFile file : ftpFiles){
            	// 解决中文乱码
            	String fname = new String(filename.getBytes(LOCAL_CHARSET),SERVER_CHARSET);
                if(fname.equalsIgnoreCase(file.getName())){
                    File localFile = new File(localpath + "/" + filename);
                    OutputStream os = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(file.getName(), os);
                    os.close();
                    flag = true;
                }
            }
            if(!flag){
            	logger.error("file not found.");
            }
            ftpClient.logout();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(ftpClient.isConnected()){
                try {
                    ftpClient.logout();
                } catch (IOException e) {
                 
                }
            }
        }
        return flag;
    }
    
    private static boolean multiMkdir(String path,FTPClient client) throws IOException
    {
    	boolean status = false;
    	String[] dirs = path.split("/");
    	for(int i = 0;i<dirs.length;i++)
    	{
    		if(!client.changeWorkingDirectory(dirs[i]))
    		{
    			if(!client.makeDirectory(dirs[i])){
    				return false;
    			}else {
    				// 创建成功,进入子目录
					client.changeWorkingDirectory(dirs[i]);
				}
    		}
    		
    		status = true;
    	}
    	return status;
    }
 
}
