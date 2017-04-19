package net.logvv.ftp.jmockit;

import java.util.HashMap;
import java.util.Map;

public class MockDao {
	
	private Map<String, Integer> depositsMap = new HashMap<String, Integer>();
	
	{
		this.depositsMap.put("bus", 100);
		this.depositsMap.put("car", 200);
		this.depositsMap.put("byc", 300);
	}
	
	// 查库存
	public int queryStock(String deposit){
		Integer count = this.depositsMap.get(deposit);
		return null == count ? 0 : count.intValue();
	}
}
