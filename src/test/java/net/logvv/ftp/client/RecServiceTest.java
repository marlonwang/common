package net.logvv.ftp.client;

import net.logvv.ftp.AbstractTestBase;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RecServiceTest extends AbstractTestBase {

	@Autowired
	private RecService recService; 
	
	@Test
	public void testUploadTest() {
		System.out.println("test");
		System.out.println(recService.uploadTest());
	}

}
