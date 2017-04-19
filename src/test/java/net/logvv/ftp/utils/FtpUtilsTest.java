package net.logvv.ftp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import junit.framework.TestCase;

public class FtpUtilsTest extends TestCase {

	private String ftpHost = "115.28.95.126";
	private int port = 2121;
	private String username = "admin";
	private String password = "admin";
	
	/** 文本上传 */
	public void testUploadFile() 
	{
		boolean success = false;
		String pathname = "file";
		String fileName = "hello_00.txt";
		String localfile = "D:\\temp\\poem.txt";
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(new File(localfile));
			success = FtpUtils.upload(ftpHost, port, username, password, pathname, fileName, inputStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("upload file status:"+success);
	}

	public void testUploadOrigin() 
	{
		boolean success = false;
		String pathname = "excel/2015/2015-01";
		String localfile = "D:\\temp\\googlelogo.png";
		success = FtpUtils.upload(ftpHost, port, username, password, pathname, localfile);
		
		System.out.println("upload picture, status:"+success);
	}

	public void testUploadRename() {
		// 上传docx
		String localfile = "D:\\temp\\代理商系统版本smsa-4.5.3.0提测单.docx";
		boolean success = FtpUtils.upload(ftpHost, port, username, password, "file", localfile);
		
		System.out.println("upload docx:"+success);
	}

	public void testDeleteFile() {
		String localPath = "D:\\temp\\smsp\\1111.txt";
		File localFile = new File(localPath);
		if(localFile.exists()){
			localFile.delete();
			System.out.println("done.");
		}else {
			System.out.println("file not found");
		}

	}

	public void testDownloadFile() {
		String savePath = "D:\\temp\\download";
		String remoteFileName = "2017-04-18短信发送记录.xls"; // googlelogo.png
		long begin = System.currentTimeMillis();
		boolean success = FtpUtils.downloadFile(ftpHost, port, username, password,"excel/2017/2017-04", remoteFileName, savePath);
		long end = System.currentTimeMillis();
		System.out.println("download file from ftp: "+success+", cost mills:"+(end-begin));
	}
	
	
}
