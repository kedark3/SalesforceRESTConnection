/*
 * This class is written to provide the user with Access token and Instance url to 
 * help user to communicate to Salesforce Rest services. 
 * Licensed under GNU General Public License v3.0.  
 */
package com.redhat.test.itsales.influx.publisher.connections;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class SalesforceRESTConnection {
	
   /*
    * Providing these default values here, but getters-setters for appropriate attributes
    * should be helpful to set values for all of those and then establish a Salesforce REST
    * connection 
    */
    private String LOGINURL     = "https://test.salesforce.com";
    private String GRANTSERVICE = "/services/oauth2/token";
    private String USERNAME     = "PUT YOUR SALESFORCE USERNAME HERE";
    private String PASSWORD     = "PUT YOUR SALESFORCE PASSWORD";
    /*
     *To generate Client Id and Client Secret: please go to Salesforce, login to your instance, 
     *go to Setup->Create->Apps, and under Connected Apps section, click New and create a new app
     *and by filling up the required details, you can create your app and after doing so, 
     *you will get ClientId and ClientSecret. 
     */
    private String CLIENTID     = "CLIENT ID FOR YOUR SALESFORCE APP";
    private String CLIENTSECRET = "CLIENT SECRET FOR SALESFORCE APP";
    private String REST_ENDPOINT = "/services/data" ;
    private String API_VERSION = "/v38.0" ;
    private Header oauthHeader; 
    private String baseUri;
    private String accessToken;
    private String instanceUrl;
    private Map<String,String> connectionMap= new HashMap<String,String>();
	
    /**
     * @param loginUrl :  Url that can be used to login to your Salesforce instance, could be 
     * https://test.salesforce.com
     * @param grantService : Oauth endpoint, example: /services/oauth2/token
     * @param username : Username used to login to Salesforce instance
     * @param password : Password+Token used to login to Salesforce instance
     * @param clientId : Cliend ID for your app
     * @param clientSecret : Cliend Secret for your app
     * @param restEndpoint : Rest Endpoint where to make query, e.g./services/data
     * @param apiVersion : API Version ,/v38.0
     * @return Map<String,String> : This class provides establishOAuth2Connection method which 
     * returns Map with keys "baseUri","accessToken" on successful connection
     * otherwise, returns null
     */
    public SalesforceRESTConnection(){}
    
    public SalesforceRESTConnection(String loginUrl, String grantService, String username, 
                                    String password,String clientId, String clientSecret, 
                                    String restEndpoint, String apiVersion) {
        super();
        LOGINURL = loginUrl;
        GRANTSERVICE = grantService;
        USERNAME = username;
        PASSWORD = password;
        CLIENTID = clientId;
        CLIENTSECRET = clientSecret;
        REST_ENDPOINT = restEndpoint;
        API_VERSION = apiVersion;
    }

    public Map<String,String> establishOAuth2Connection(){
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(LOGINURL+GRANTSERVICE);
        List nameValuePairs = new ArrayList();
        nameValuePairs.add(new BasicNameValuePair("client_id", CLIENTID)); 
        nameValuePairs.add(new BasicNameValuePair("client_secret", CLIENTSECRET));
        nameValuePairs.add(new BasicNameValuePair("username", USERNAME));
        nameValuePairs.add(new BasicNameValuePair("password", PASSWORD));
        nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));

        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        HttpResponse response=null;
        try {
            response = client.execute(post);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if(response.getStatusLine().getStatusCode()!=200){
            throw new RuntimeException("HTTP Response code is: "+ 
                                        response.getStatusLine().getStatusCode());
        }
		
        String responseString=null;
        try{
            responseString=EntityUtils.toString(response.getEntity());
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
        //we store responseString in this jsonObject then to find instance_url and access_token 
        JSONObject jsonObject=null;
		
        try{
			
            jsonObject=(JSONObject) new JSONTokener(responseString).nextValue();
            accessToken=jsonObject.getString("access_token");
            instanceUrl=jsonObject.getString("instance_url");
            oauthHeader = new BasicHeader("Authorization", "Bearer " + accessToken);
        }catch(JSONException e){
            e.printStackTrace();
            return null;
        }
        baseUri= instanceUrl+REST_ENDPOINT+API_VERSION;

        post.releaseConnection();
		
        connectionMap.put("baseUri", baseUri);
        connectionMap.put("accessToken", accessToken);
        return connectionMap;
    }

    public String getLOGINURL() {
        return LOGINURL;
    }

    public void setLOGINURL(String loginUrl) {
        LOGINURL = loginUrl;
    }

    public String getGRANTSERVICE() {
        return GRANTSERVICE;
    }

    public void setGRANTSERVICE(String grantService) {
        GRANTSERVICE = grantService;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String username) {
        USERNAME = username;
    }

    public String getCLIENTID() {
        return CLIENTID;
    }

    public void setCLIENTID(String clientId) {
        CLIENTID = clientId;
    }

    public String getCLIENTSECRET() {
        return CLIENTSECRET;
    }

    public void setCLIENTSECRET(String clientSecret) {
        CLIENTSECRET = clientSecret;
    }

    public String getREST_ENDPOINT() {
        return REST_ENDPOINT;
    }

    public void setREST_ENDPOINT(String restEndpoint) {
        REST_ENDPOINT = restEndpoint;
    }

    public String getAPI_VERSION() {
        return API_VERSION;
    }

    public void setAPI_VERSION(String apiVersion) {
        API_VERSION = apiVersion;
    }

    public Header getOauthHeader() {
        return oauthHeader;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getInstanceUrl() {
        return instanceUrl;
    }

    public void setPASSWORD(String password) {
        PASSWORD = password;
    }

	
}

