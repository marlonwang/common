package net.logvv.ftp.jmockit;

import junit.framework.Assert;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * 基于行为的Mock 测试，一共三个阶段：record、replay、verify。
 * 1）record：在这个阶段，各种在实际执行中期望被调用的方法都会被录制。
 * 2）repaly：在这个阶段，执行单元测试Case，原先在record 阶段被录制的调用都可能有机会被执行到。
 * 			    这里有“有可能”强调了并不是录制了就一定会严格执行。
 * 3）verify：在这个阶段，断言测试的执行结果或者其他是否是原来期望的那样。
 */

// 当前运行错误 mockit.internal.util.ClassLoad.isGeneratedSubclass

@RunWith(JMockit.class)
public class BehaviorTest {
	
	@Mocked
	MockDao dao = new MockDao();
	
	private MockService service = new MockService();
	
	@Test
	public void test()
	{
		// 1. record
		new NonStrictExpectations() {
			{
				/* 录制的方法 */
				dao.queryStock(anyString); // 不管传入何值,返回相同的结果
				
				/*预期结果*/
				result = 200;
				
				/* times必须调用两次。在Expectations中，必须调用，否则会报错，因此不需要作校验。
				      在NonStrictExpectations中不强制要求，但要进行verify验证.但似乎已经强制要求了
				      此外还有maxTimes，minTimes
				*/
				times = 1;
			}
		};
		
		service.setDao(dao);
		
		// 2. reply 调用
		Assert.assertEquals("库存盈余", service.getStock("bus"));
		Assert.assertEquals("库存不足", service.getStock("car"));
		
		// 3. verify
		new Verifications() {
			{
				dao.queryStock(anyString);
				times = 1;
			}
		};
	}
	
}
