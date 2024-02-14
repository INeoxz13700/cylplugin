package fr.karmaowner.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HTTPUtils {
  private HttpURLConnection httpConn;
  
  public HttpURLConnection sendGetRequest(String requestURL) throws IOException {
    URL url = new URL(requestURL);
    this.httpConn = (HttpURLConnection)url.openConnection();
    this.httpConn.setUseCaches(false);
    this.httpConn.setDoInput(true);
    this.httpConn.setDoOutput(false);
    return this.httpConn;
  }
  
  public HttpURLConnection sendPostRequest(String requestURL, Map<String, String> params) throws IOException {
    URL url = new URL(requestURL);
    this.httpConn = (HttpURLConnection)url.openConnection();
    this.httpConn.setUseCaches(false);
    this.httpConn.setDoInput(true);
    StringBuffer requestParams = new StringBuffer();
    if (params != null && params.size() > 0) {
      this.httpConn.setDoOutput(true);
      Iterator<String> paramIterator = params.keySet().iterator();
      while (paramIterator.hasNext()) {
        String key = paramIterator.next();
        String value = params.get(key);
        requestParams.append(URLEncoder.encode(key, "UTF-8"));
        requestParams.append("=").append(
            URLEncoder.encode(value, "UTF-8"));
        requestParams.append("&");
      } 
      OutputStreamWriter writer = new OutputStreamWriter(this.httpConn.getOutputStream());
      writer.write(requestParams.toString());
      writer.flush();
    } 
    return this.httpConn;
  }
  
  public HttpURLConnection sendPostRequest(String requestURL, Map<String, String> params, HashMap<String, String> headers) throws IOException {
    URL url = new URL(requestURL);
    this.httpConn = (HttpURLConnection)url.openConnection();
    for (Map.Entry<String, String> entry : headers.entrySet())
      this.httpConn.setRequestProperty(entry.getKey(), entry.getValue()); 
    this.httpConn.setUseCaches(false);
    this.httpConn.setDoInput(true);
    StringBuffer requestParams = new StringBuffer();
    if (params != null && params.size() > 0) {
      this.httpConn.setDoOutput(true);
      Iterator<String> paramIterator = params.keySet().iterator();
      while (paramIterator.hasNext()) {
        String key = paramIterator.next();
        String value = params.get(key);
        requestParams.append(URLEncoder.encode(key, "UTF-8"));
        requestParams.append("=").append(
            URLEncoder.encode(value, "UTF-8"));
        requestParams.append("&");
      } 
      OutputStreamWriter writer = new OutputStreamWriter(this.httpConn.getOutputStream());
      writer.write(requestParams.toString());
      writer.flush();
    } 
    return this.httpConn;
  }
  
  public String readSingleLineRespone() throws IOException {
    InputStream inputStream = null;
    if (this.httpConn != null) {
      inputStream = this.httpConn.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      String response = reader.readLine();
      reader.close();
      return response;
    } 
    return null;
  }
  
  public String[] readMultipleLinesRespone() throws IOException {
    InputStream inputStream = null;
    if (this.httpConn != null) {
      inputStream = this.httpConn.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      List<String> response = new ArrayList<>();
      String line = "";
      while ((line = reader.readLine()) != null)
        response.add(line); 
      reader.close();
      return response.<String>toArray(new String[0]);
    } 
    return null;
  }
  
  public void disconnect() {
    if (this.httpConn != null)
      this.httpConn.disconnect(); 
  }
}
