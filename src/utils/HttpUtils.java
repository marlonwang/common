package net.logvv.ftp.utils;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Http请求工具,支持ssl单向加密</br>
 * 基于httpclient的http工具类 GET/POST/PUT/DELETE
 * @author wangwei
 * @date 2017年4月19日 上午11:48:43
 */
public class HttpUtils {

	private static Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);
	
	private final static int DEFAULT_STRINGBUFFER_LENGTH = 100;
	private final static int DEFAULT_TIMEOUT = 3000;
	
	/**
	 * GET请求，不支持SSL,无请求参数</br>
	 * 基于java.net工具包
	 * @Description
	 * @param requestUrl
	 * @return
	 * @author wangwei
	 * @date 2017年4月19日 下午1:28:23
	 */
	@Deprecated
	public static String doGet(String requestUrl)
	{
		String response;
		HttpURLConnection conn;
		BufferedReader in = null;
		try {
			URL url = new URL(requestUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			
			int responseCode = conn.getResponseCode();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
			StringBuffer buffer = new StringBuffer();
			String line;
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
			
			in.close();
			conn.disconnect();
			response = buffer.toString();
			LOGGER.info("finish get request, response code:{}",responseCode);
			
		} catch (IOException e) {
			LOGGER.error("failed to request {}, error:{}",requestUrl,e);
			return null;
		}finally{
			if(null != in){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return response;
		
	}


	/**
	 * GET请求,uri后不带任何参数 (推荐使用)</br>
	 * 基于apache httpcomponents工具包
	 * @Description
	 * @param requestUrl
	 * @param ssl
	 * @return
	 * @author wangwei
	 * @date 2017年4月19日 下午3:23:36
	 */
	public static String doGet(String requestUrl, boolean ssl)
	{
		String result = null;
		HttpClient httpClient = buildHttpClient(ssl);
		HttpGet request = new HttpGet(requestUrl);
		
		try {
			HttpResponse response = httpClient.execute(request);
			int responseCode = response.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK == responseCode){
				result = EntityUtils.toString(response.getEntity(),"utf-8");
			}else {
				LOGGER.error("response code {}",responseCode);
			}
			LOGGER.debug(">>>> response:\n{}",response);
		} catch (ClientProtocolException e) {
			LOGGER.error("HttpClient protocol error:{}",e);
			return result;
		} catch (IOException e) {
			LOGGER.error("HttpClient io error:{}",e);
			return result;
		}
			
		return result;
	}
	
	
	/**
	 * GET请求, 请求参数封装成map形式
	 * @Description
	 * @param requestUrl
	 * @param paraMap
	 * @param ssl
	 * @return
	 * @author wangwei
	 * @date 2017年4月19日 下午2:01:03
	 */
	public static String doGet(String requestUrl, Map<String, String> paraMap, boolean ssl)
	{
		// uri后&连接请求参数
		StringBuffer sb = new StringBuffer(DEFAULT_STRINGBUFFER_LENGTH);
        sb.append(requestUrl).append('?');
        for (Iterator<String> it = paraMap.keySet().iterator(); it.hasNext();)
        {
            String paramKey = it.next();
            String paramValue = paraMap.get(paramKey);
            sb.append(paramKey).append('=').append(paramValue);
            if(it.hasNext()) {
                sb.append('&');
            }
        }
        
		return doGet(sb.toString(), ssl);
	}
	
	/**
	 * POST请求，不支持SSL</br>
	 * 基于java.net工具包
	 * @Description
	 * @param requestUrl
	 * @return
	 * @author wangwei
	 * @date 2017年4月19日 下午1:28:23
	 */
	@Deprecated
	public static String doPost(String requestUrl,String param)
	{
		String response;
		PrintWriter out = null;
		BufferedReader in = null;
		HttpURLConnection conn;
		try {
			URL url = new URL(requestUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			// post请求需要设置如下
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			
			out = new PrintWriter(conn.getOutputStream());
			out.print(param);
			
			out.flush();
			
			int responseCode = conn.getResponseCode();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
			StringBuffer buffer = new StringBuffer();
			String line;
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
			
			out.close();
			in.close();
			conn.disconnect();
			response = buffer.toString();
			LOGGER.info("finish get request, response code:{}",responseCode);
		} catch (Exception e) {
			LOGGER.error("failed to post {}, error:{}",requestUrl,e);
			return null;
		}finally{
			try {
				if(null != out){
					out.close();
				}
				if(null != in){
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return response;
	}
	
	/**
	 * POST请求,支持SSL,请求参数格式为 json </br>
	 * 基于apache httpcomponent工具包
	 * @Description
	 * @param requestUrl
	 * @param content
	 * @param ssl
	 * @return
	 * @author wangwei
	 * @date 2017年4月19日 下午4:14:53
	 */
	public static String doPost(String requestUrl,String content,boolean ssl)
	{
		String result = null;
		HttpClient httpClient = buildHttpClient(ssl);
		HttpPost httpPost = new HttpPost(requestUrl);
		// timeout
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(DEFAULT_TIMEOUT)
				.setConnectTimeout(DEFAULT_TIMEOUT)
				.build();
		httpPost.setConfig(requestConfig);
		httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
		
		try {
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(content.getBytes("utf-8")));
			requestBody.setContentLength(content.getBytes("utf-8").length);
			httpPost.setEntity(requestBody);
			
			HttpResponse response = httpClient.execute(httpPost);
			int responseCode = response.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK == responseCode){
				result = EntityUtils.toString(response.getEntity(),"utf-8");
			}else {
				LOGGER.error("response code {}",responseCode);
			}
			LOGGER.debug(">>>> response:\n{}",response);
		}catch (ClientProtocolException e) {
			LOGGER.error("HttpClient protocol error:{}",e);
			return result;
		} catch (IOException e) {
			LOGGER.error("HttpClient io error:{}",e);
			return result;
		}
		
		return result;
	}
	
	/**
	 * POST 请求, post参数格式为 x-www-form-urlencoded
	 * @Description
	 * @param requestUrl
	 * @param paraMap
	 * @param ssl
	 * @return
	 * @author wangwei
	 * @date 2017年4月19日 下午5:37:01
	 */
	public static String doPost(String requestUrl,Map<String, String> paraMap,boolean ssl)
	{
		
		String result = null;
		HttpClient httpClient = buildHttpClient(ssl);
		HttpPost httpPost = new HttpPost(requestUrl);
		// timeout
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(DEFAULT_TIMEOUT)
				.setConnectTimeout(DEFAULT_TIMEOUT)
				.build();
		
		httpPost.setConfig(requestConfig);
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		
		try {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
	        if(paraMap != null && paraMap.keySet().size() > 0) 
	        {  
	            Iterator<Map.Entry<String, String>> iterator = paraMap.entrySet().iterator();  
	            while (iterator.hasNext()) {  
	                Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();  
	                nvps.add(new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));  
	            }  
	        }  
	        httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));  
			
			HttpResponse response = httpClient.execute(httpPost);
			int responseCode = response.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK == responseCode){
				result = EntityUtils.toString(response.getEntity(),Consts.UTF_8);
			}else {
				LOGGER.error("response code {} \n",responseCode);
			}
			LOGGER.debug(">>>> response:\n{}",response);
		}catch (ClientProtocolException e) {
			LOGGER.error("HttpClient protocol error:{}",e);
			return result;
		} catch (IOException e) {
			LOGGER.error("HttpClient io error:{}",e);
			return result;
		}
		
		return result;

	}
	
	/**
	 * PUT 方法,支持SSL,请求参数格式为 json
	 * @Description
	 * @param requestUrl
	 * @param content
	 * @param ssl
	 * @return
	 * @author wangwei
	 * @date 2017年4月19日 下午7:46:20
	 */
	public static String doPut(String requestUrl,String content,boolean ssl)
	{
		String result = null;
		HttpClient httpClient = buildHttpClient(ssl);
		HttpPut httpPut = new HttpPut(requestUrl);
		// timeout
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(DEFAULT_TIMEOUT)
				.setConnectTimeout(DEFAULT_TIMEOUT)
				.build();
		httpPut.setConfig(requestConfig);
		httpPut.setHeader("Content-Type", "application/json; charset=utf-8");
		
		try {
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(content.getBytes("utf-8")));
			requestBody.setContentLength(content.getBytes("utf-8").length);
			httpPut.setEntity(requestBody);
			
			HttpResponse response = httpClient.execute(httpPut);
			int responseCode = response.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK == responseCode){
				result = EntityUtils.toString(response.getEntity(),"utf-8");
			}else {
				LOGGER.error("response code {}",responseCode);
			}
			LOGGER.debug(">>>> response:\n{}",response);
		}catch (ClientProtocolException e) {
			LOGGER.error("HttpClient protocol error:{}",e);
			return result;
		} catch (IOException e) {
			LOGGER.error("HttpClient io error:{}",e);
			return result;
		}
		
		return result;
	}

	/**
	 * POST 请求, post参数格式为 x-www-form-urlencoded
	 * @Description
	 * @param requestUrl
	 * @param paraMap
	 * @param ssl
	 * @return
	 * @author wangwei
	 * @date 2017年4月19日 下午7:46:55
	 */
	public static String doPut(String requestUrl,Map<String, String> paraMap,boolean ssl)
	{
		
		String result = null;
		HttpClient httpClient = buildHttpClient(ssl);
		HttpPut httpPut = new HttpPut(requestUrl);
		// timeout
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(DEFAULT_TIMEOUT)
				.setConnectTimeout(DEFAULT_TIMEOUT)
				.build();
		
		httpPut.setConfig(requestConfig);
		httpPut.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		
		try {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
	        if(paraMap != null && paraMap.keySet().size() > 0) 
	        {  
	            Iterator<Map.Entry<String, String>> iterator = paraMap.entrySet().iterator();  
	            while (iterator.hasNext()) {  
	                Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();  
	                nvps.add(new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));  
	            }  
	        }  
	        httpPut.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));  
			
			HttpResponse response = httpClient.execute(httpPut);
			int responseCode = response.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK == responseCode){
				result = EntityUtils.toString(response.getEntity(),Consts.UTF_8);
			}else {
				LOGGER.error("response code {} \n",responseCode);
			}
			LOGGER.debug(">>>> response:\n{}",response);
		}catch (ClientProtocolException e) {
			LOGGER.error("HttpClient protocol error:{}",e);
			return result;
		} catch (IOException e) {
			LOGGER.error("HttpClient io error:{}",e);
			return result;
		}
		
		return result;

	}

	/**
	 * DELETE 方法, 适用于无参数或参数拼接在url后
	 * @Description
	 * @param requestUrl
	 * @param ssl
	 * @return
	 * @author wangwei
	 * @date 2017年4月19日 下午7:32:15
	 */
	public static boolean doDelete(String requestUrl,boolean ssl)
	{
		boolean status = false;
		HttpClient httpClient = buildHttpClient(ssl);
		HttpDelete httpDelete = new HttpDelete(requestUrl);
		try {
			HttpResponse response = httpClient.execute(httpDelete);
			int responseCode = response.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK == responseCode){
				// EntityUtils.toString(response.getEntity(),"utf-8");
				status = true;
			}else {
				LOGGER.error("response code {}",responseCode);
			}
			LOGGER.debug(">>>> response:\n{}",response);
		} catch (ClientProtocolException e) {
			LOGGER.error("HttpClient protocol error:{}",e);
			return false;
		} catch (IOException e) {
			LOGGER.error("HttpClient io error:{}",e);
			return false;
		}
		
		return status;
	}
	
	public static boolean doDelete(String requestUrl,Map<String, String> paraMap,boolean ssl)
	{
		boolean status = false;
		
		try {
			StringBuffer sb = new StringBuffer(DEFAULT_STRINGBUFFER_LENGTH);
            // 1. 添加?号
            if (paraMap.size() > 0){
                sb.append(requestUrl).append("?");
            }
            else {
                status = doDelete(requestUrl, ssl);
                return status;
            }
            // 拼接URI
            for (Iterator<String> it = paraMap.keySet().iterator(); it.hasNext();)
            {
                String key = it.next();
                sb.append(key).append("=").append(paraMap.get(key));
                if (it.hasNext()) {
                    sb.append("&");
                }
            }
            status = doDelete(sb.toString(), ssl);
		} catch (Exception e) {
			LOGGER.error("Failed to request {}, Method: DELETE, error:\n{}",requestUrl,e);
			return false;
		}
		
		return status;
	}
	
	/**
	 * 针对https是否开启SSL认证,创建相应的HttpClient
	 * @Description
	 * @param ssl
	 * @return
	 * @author wangwei
	 * @date 2017年4月19日 下午3:26:40
	 */
	private static HttpClient buildHttpClient(boolean ssl)
	{
		HttpClient httpClient = null;
		// http
		if(!ssl){
			httpClient = HttpClientBuilder.create().build();
			return httpClient;
		}
		// https
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy(){
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
					return true;  // 信任任何连接
				}
			}).build();
			SSLConnectionSocketFactory sslcsf = new SSLConnectionSocketFactory(sslContext);
			httpClient = HttpClients.custom().setSSLSocketFactory(sslcsf).build();
			
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return httpClient;
	}

	/**
	 * httpclient的文件上传
	 * TODO 返回上传后的文件的url
	 * @Description
	 * @param requestUrl  上传服务器地址
	 * @param localFile   文件路径
	 * @return
	 * @author wangwei
	 * @date 2017年4月20日 上午10:31:27
	 */
	public static boolean upload(String requestUrl,String localFile)
	{
		boolean status = false;
		String result = "";
		HttpClient httpClient = buildHttpClient(false); // 非https

		try {
			HttpPost httpPost = new HttpPost(requestUrl);
			// 文件转成流对象FileBody
			FileBody fileBody = new FileBody(new File(localFile));
			// addPart 相当于 <input type="file" name="file" />
			HttpEntity reqEntity = (HttpEntity) MultipartEntityBuilder.create().addPart("file", fileBody);
			httpPost.setEntity(reqEntity);

			HttpResponse response = httpClient.execute(httpPost);
			int responseCode = response.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK == responseCode){
				status = true;
				result = EntityUtils.toString(response.getEntity(),"utf-8");
			}else {
				LOGGER.error("response code {}",responseCode);
			}

			LOGGER.debug(">>>> response:\n{}",result);

		} catch (ClientProtocolException e) {
			LOGGER.error("HttpClient protocol error:{}",e);
			return false;
		} catch (IOException e) {
			LOGGER.error("HttpClient io error:{}",e);
			return false;
		}

		return status;
	}

	// 文件下载
	public static void download(String remotePath,String localPath)
	{
		HttpClient httpClient = buildHttpClient(false);
		OutputStream out = null;
		InputStream in = null;

		try {
			HttpGet httpGet = new HttpGet(remotePath);
			HttpResponse response = httpClient.execute(httpGet);
			int responserCode = response.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK == responserCode){
				in = response.getEntity().getContent();
				long length = response.getEntity().getContentLength();
				if(length <= 0){
					LOGGER.info("download file not exist.");
					return ;
				}

				// 保存文件到本地
				File file = new File(localPath);
				if(!file.exists()){
					file.createNewFile();
				}
				out = new FileOutputStream(file);
				byte[] buffer = new byte[4096];
				int readLength = 0;
				while ((readLength = in.read(buffer)) > 0) {
					byte[] bytes = new byte[readLength];
					System.arraycopy(buffer, 0, bytes, 0, readLength);
					out.write(bytes);
				}
				out.flush();
			}else {
				LOGGER.error("response code:{}",responserCode);
			}
		} catch (IOException e) {
			LOGGER.error("download io exception:{}",e);
			return ;
		} catch (Exception e) {
			LOGGER.error("downlod exception:{}",e);
			return ;
		}finally{
			try {
				if(in != null){
					in.close();
				}
				if(out != null){
					out.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
