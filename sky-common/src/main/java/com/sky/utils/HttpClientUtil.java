package com.sky.utils;

import com.alibaba.fastjson2.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HttpClientUtil {
    static final int TIMEOUT_MSEC = 5000;

    /*
        发送GET方式请求
        @Param url
        @Param paramMap
        @return
     */
    public static String doGet(String url, Map<String,String> paramMap) {
        CloseableHttpClient httpClient= HttpClients.createDefault();
        String result="";
        CloseableHttpResponse response=null;

        try{
            // 统一资源标识符
            URIBuilder builder=new URIBuilder(url);
            if(paramMap!=null){
                for(String key:paramMap.keySet()){
                    builder.addParameter(key, paramMap.get(key));
                }
            }

            URI uri=builder.build();

            //创建GET请求
            HttpGet httpGet=new HttpGet(uri);

            response=httpClient.execute(httpGet);
            if(response.getStatusLine().getStatusCode()==200){
                result= EntityUtils.toString(response.getEntity(),"UTF-8");

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                response.close();
                httpClient.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return result;
    }

    /*
        发送POST请求
        @Param url
        @Param paramMap
        @return
        @throws IOException
     */
    public static String doPost(String url, Map<String,String> paramMap) throws IOException {
        // 创建HttpClient 对象
        CloseableHttpClient httpClient= HttpClients.createDefault();
        CloseableHttpResponse response=null;
        String result="";

        try{
            HttpPost httpPost=new HttpPost(url);

            if(paramMap!=null){
                //  把Map转成List
                List<NameValuePair> paramList=new ArrayList<>();
                // paramMap.entrySet()中每个元素都是 Map.Entry<String,String>类型
                // Map.Entry表示Map中的一个键值对
                for(Map.Entry<String,String> entry:paramMap.entrySet()){
                    paramList.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));

                }
                UrlEncodedFormEntity entity=new UrlEncodedFormEntity(paramList);
                httpPost.setEntity(entity);
            }

            httpPost.setConfig(builderRequestConfig());

            response=httpClient.execute(httpPost);
            result=EntityUtils.toString(response.getEntity(),"UTF-8");
        }catch (Exception e){
            throw e;
        }finally{
            try{
                httpClient.close();
                response.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return result;
    }


    /*
        发送POST请求
        @Param url
        @Param paramMap
        @return
        @throw IOException
        使用JSON方式
     */
    public static String doPost4Json(String url, Map<String, String> paramMap) throws IOException{
        CloseableHttpClient httpClient= HttpClients.createDefault();
        CloseableHttpResponse response=null;
        String result="";

        try{
            HttpPost httpPost=new HttpPost(url);
            if(paramMap!=null){
                JSONObject jsonObject=new JSONObject();
                for(Map.Entry<String,String> entry:paramMap.entrySet()){
                    jsonObject.put(entry.getKey(),entry.getValue());

                }
                StringEntity entity=new StringEntity(jsonObject.toString(),"UTF-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }
            httpPost.setConfig(builderRequestConfig());
            response=httpClient.execute(httpPost);

            result=EntityUtils.toString(response.getEntity(),"UTF-8");
        }catch (Exception e){
            throw e;

        }finally {
            try{
                response.close();
                httpClient.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return result;

    }
    private static RequestConfig builderRequestConfig(){
        return RequestConfig.custom()
                .setSocketTimeout(TIMEOUT_MSEC)
                .setConnectTimeout(TIMEOUT_MSEC)
                .setConnectionRequestTimeout(TIMEOUT_MSEC)
                .build();
    }
}
