package net.logvv.ftp.utils;

import static org.junit.Assert.*;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class HttpUtilsTest {

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	public static void main(String[] args) throws UnknownHostException 
	{
		String host0 = "http://127.0.0.1:20101/users";
		String host1 = "https://www.logvv.net";
		// HTTP GET
//		System.out.println(HttpUtils.doGet(host0));  // ok
		System.out.println(HttpUtils.doGet(host0, false));  // ok
//		System.out.println(HttpUtils.doGet(host1, true));  // ok
		
		// HTTP POST
		String host2 = "http://115.28.95.126:8099/sms/mopostx";
		String host3 = "http://127.0.0.1:20101/sms-partner/mopost2";
		String host4 = "http://127.0.0.1:20101/smsp/callback";
		// String host5 = "https://api.ucpaas.com/sms-partner/access/a000r5/sendsms";
		
		// HTTP PUT
		String host5 = "http://127.0.0.1:20101/users/123";
		
		String postdata ="{\"content\": \"ws线上测试01\","
				+ "\"extend\": \"00\","
				+ "\"mobile\": \"\","
				+ "\"moid\": \"cafe78q1-5167-4bd2-8373-834718811ac0\","
				+ "\"reply_time\": \"2017-04-12 16:57:42\","
				+ "\"sign\": \"\"}\"";
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("extend", "00");
		map.put("content", "djkajdkaa");
		map.put("mobile", "123");
		map.put("moid", "cafe78q1-5167-4bd2-8373-834718811ac0");
		map.put("reply_time", "2017-04-12 16:57:42");
		map.put("sign", "daddd");
		
//		Map<String, String> req = new HashMap<String, String>();
//		req.put("clientid", "a000r5");
//		req.put("password", "yyeyqeyquiyeqiq");
		
		// System.out.println(HttpUtils.doPost(host5, JsonUtils.obj2json(req),true));  // ok
		
//		System.out.println(HttpUtils.doPost(host2, postdata)); //host2 ok; host3 exception
//		System.out.println(HttpUtils.doPost(host3, map, false)); // json 提交
//		System.out.println(HttpUtils.doPost(host4, postdata, false)); // form 提交
		
		String user = "{\"id\":123,\"age\":18,\"name\":\"alex\"}";
		System.out.println(HttpUtils.doPut(host5, user, false));
		
		System.out.println(HttpUtils.doDelete(host5, false));
		
	}

}
