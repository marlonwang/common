package net.logvv.ftp.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class JsonUtilsTest {

	@Test
	public void testJson2obj() {

	}

	@Test
	public void testJson2map() {
		Map<String, Map<String, String>> map = new HashMap<String, Map<String,String>>();
		Map<String, String> tmp = new HashMap<String, String>();
		tmp.put("type", "mountain");
		tmp.put("price", "fiixed");
		
		map.put("zhangjiajie", tmp);
		map.put("jiuzhaigou", tmp);
		
		String mapStr2 = JsonUtils.obj2json(map);
		System.out.println(mapStr2);
		
		System.out.println(JsonUtils.json2map(mapStr2, Map.class));
	}

	@Test
	public void testJson2list() {
		Map<String, Map<String, String>> map = new HashMap<String, Map<String,String>>();
		Map<String, String> tmp = new HashMap<String, String>();
		tmp.put("type", "mountain");
		tmp.put("price", "fiixed");
		
		map.put("zhangjiajie", tmp);
		map.put("jiuzhaigou", tmp);
		
	}

	@Test
	public void testGetJsonValue() {
		fail("Not yet implemented");
	}

	@Test
	public void testObj2json() {
		List<String> list = new ArrayList<String>();
		list.add("zhangjiajie");
		list.add("jiuzhaigou");
		
		System.out.println(JsonUtils.obj2json(list));
	}

	@Test
	public void testObj2node() {
		fail("Not yet implemented");
	}

	@Test
	public void testObj2T() {
		fail("Not yet implemented");
	}

}
