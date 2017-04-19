package net.logvv.ftp.jmockit;

import junit.framework.Assert;
import mockit.Mock;
import mockit.MockUp;

import org.junit.Test;

/**
 * 基于状态的mock测试
 */
public class StateTest {
	
	private MockDao dao;
	
	private MockService service;
	
	@Test
	public void test(){
		// 1. mock 对象 构造一个无关的MockDao
		MockUp<MockDao> mockUp = new MockUp<MockDao>() {
			@Mock
			public int getStock(String deposit){
				return 1000;
			}
		};
		
		// 2. 获取实例
		dao = mockUp.getMockInstance();
		service = new MockService();
		service.setDao(dao);
		
		// 3. 调用
		Assert.assertEquals("库存不足", service.getStock("motocycle"));
		
		
	}
}
