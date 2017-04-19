package net.logvv.ftp.jmockit;

public class MockService {
	
	private MockDao mockDao;
	
	public void setDao(MockDao dao) {
		this.mockDao = dao;
	}
	
	public String getStock(String deposit)
	{
		String result = "";
		int left = mockDao.queryStock(deposit);
		if(left < 0){
			result = "库存透支";
		}else if (left == 0) {
			result = "库存不足";
		}else {
			result = "库存盈余";
		}
		
		return result;
	}
}
