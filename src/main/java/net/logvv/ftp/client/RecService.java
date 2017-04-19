package net.logvv.ftp.client;

import net.logvv.ftp.utils.FtpUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RecService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RecService.class);

	// ftp 用户信息
	@Value("${ftp.server.host}")
	private String ftpHost;
	@Value("${ftp.server.port}")
	private int ftpPort;
	@Value("${ftp.server.username}")
	private String username;
	@Value("${ftp.server.password}")
	private String password;
	
	/**
	 * 测试文件上传
	 * @Description
	 * @author wangwei
	 * @date 2017年3月29日 下午5:22:15
	 */
	public boolean uploadTest()
	{
		String localfile = "D:\\temp\\代理商系统版本smsa-4.5.3.0提测单.docx";
		boolean success = FtpUtils.upload(ftpHost, ftpPort, username, password, "file", localfile);
		
		LOGGER.info("upload file to server, status:{}",success);
		
		return success;
	}
	
}
