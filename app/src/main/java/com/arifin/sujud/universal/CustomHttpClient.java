package com.arifin.sujud.universal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.entity.BufferedHttpEntity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
public class CustomHttpClient {
	
	private static HttpClient custHttpClient;
	public static final int MAX_TOTAL_CONNECTIONS = 100;
	public static final int MAX_CONNECTIONS_PER_ROUTE = 50;
	public static final int TIMEOUT_CONNECT = 15000;
	public static final int TIMEOUT_READ = 15000;
	
	public static HttpClient getHttpClient() {
		
	if (custHttpClient == null) {
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schemeRegistry.register(new Scheme("https",SSLSocketFactory.getSocketFactory(), 443));

			HttpParams connManagerParams = new BasicHttpParams();
			ConnManagerParams.setMaxTotalConnections(connManagerParams, MAX_TOTAL_CONNECTIONS);
			ConnManagerParams.setMaxConnectionsPerRoute(connManagerParams, new ConnPerRouteBean(MAX_CONNECTIONS_PER_ROUTE));
			HttpConnectionParams.setConnectionTimeout(connManagerParams, TIMEOUT_CONNECT);
			HttpConnectionParams.setSoTimeout(connManagerParams, TIMEOUT_READ);
			ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(new BasicHttpParams(), schemeRegistry);

			custHttpClient = new DefaultHttpClient(cm, null);
			HttpParams para = custHttpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(para, (30 * 1000));
			HttpConnectionParams.setSoTimeout(para, (30 * 1000));
			ConnManagerParams.setTimeout(para, (30 * 1000));
		}
		return custHttpClient;
	}
	
	public static String executePost(String urlPostFix,ArrayList<NameValuePair> postedValues)
	throws Exception {
		String url = urlPostFix;
		BufferedReader in = null;
		try {
			
			HttpClient client = getHttpClient();
			HttpPost request = new HttpPost(url);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-Type", "application/x-www-form-urlencoded");
			
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postedValues);
			formEntity.setContentType("application/json");

			request.setEntity(formEntity);
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String result = sb.toString();
			return result;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}
	public static String executeGet(String urlPostFix)
			throws Exception {
				String url = urlPostFix;
				BufferedReader in = null;
				try {
					HttpClient client = getHttpClient();
					HttpGet request = new HttpGet(url);
					request.setHeader("Accept", "application/json");
					request.setHeader("Content-Type", "application/x-www-form-urlencoded");
					HttpResponse response = client.execute(request);
					in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					
					StringBuffer sb = new StringBuffer("");
					String line = "";
					String NL = System.getProperty("line.separator");
					while ((line = in.readLine()) != null) {
						sb.append(line + NL);
					}
					in.close();
					String result = sb.toString();
					return result;
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
						}
					}
				}
			}
	public static Bitmap executeImageGet(String urlPostFix)
			throws Exception {
				String url = urlPostFix;
				InputStream in = null;
				try {
					HttpClient client = getHttpClient();
					HttpGet request = new HttpGet(url);
					HttpResponse response = client.execute(request);
					HttpEntity entity = response.getEntity();
					BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity); 
					in = bufHttpEntity.getContent();
					Bitmap bitmap = BitmapFactory.decodeStream(in);
					
					in.close();
					return bitmap;

				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
						}
				}
				}
	}
		
	
}
