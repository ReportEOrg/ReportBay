package org.reportbay.publish.service.impl;

import static org.reportbay.publish.service.DropBoxParamConstant.ACCESS_TOKEN;
import static org.reportbay.publish.service.DropBoxParamConstant.BASE64_ENCODED_CONTENT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Base64;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.reportbay.publish.service.PublishService;
import org.reportbay.publish.service.exception.PublishServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWriteMode;
import com.dropbox.core.http.StandardHttpRequestor;

/**
 * Drop Box Publishing Service implementation 
 *
 */
public class DropBoxPublishServiceImpl implements PublishService{
	
	private static final Logger LOG = LoggerFactory.getLogger(DropBoxPublishServiceImpl.class);
			
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getServiceName() {
		return "Drop Box";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void publish(String publishName, Properties parameters)
			throws PublishServiceException {
		//0. validate input
		validatePublishParam(publishName, parameters);
		
		//1. setup client
		DbxClient client = setupDropBoxClient(parameters);
		
		File tempFile = null;
		FileOutputStream fos = null;
		
		try{
			//2. create a temp file as SDK feed on file
			tempFile = createTemporaryFile(parameters);
			
			//3. write the decoded content to file out stream of temp file
			fos = new FileOutputStream(tempFile);
			fos.write(decodeBase64Content(parameters));

			try(FileInputStream fis = new FileInputStream(tempFile)){
				//4. publish to drop box via SDK
				DbxEntry.File uploadedFile = client.uploadFile("/"+publishName,
					   									   	   DbxWriteMode.add(), 
					   									   	   tempFile.length(), 
					   									   	   fis);
				
				LOG.info("uploaded success : {}",uploadedFile.toString());
			}
			catch(DbxException e) {
				throw new PublishServiceException("Error upload to drop box",e);
			}
		}
		catch(IOException e){
			throw new PublishServiceException(e);
		}
		finally{
			if(tempFile!=null){
				//remove the temp file
				tempFile.delete();
			}
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					LOG.warn("Failed to close file output stream",e);
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareParamFromProfile(Properties prop, Object profile) {
		//TODO: obtain the access token from profile 
		prop.put(ACCESS_TOKEN, "Y9xnDyZNbhAAAAAAAAAACHCXh3i41MnsYL5XJfyqsrTZ9LzoO0sg9BCacLo-9nlj");		
	}
	/**
	 * 
	 * @param publishName
	 * @param parameters
	 * @throws PublishServiceException
	 */
	private void validatePublishParam(String publishName, Properties parameters) throws PublishServiceException{
		
		StringBuilder sb = new StringBuilder();
		
		if(StringUtils.isBlank(publishName)){
			sb.append("publish name,");
		}
		
		if(StringUtils.isBlank(parameters.getProperty(ACCESS_TOKEN))){
			sb.append("access token,");
		}
		
		if(StringUtils.isBlank(parameters.getProperty(BASE64_ENCODED_CONTENT))){
			sb.append("base64 encoded content,");
		}
		
		if(sb.length()>0){
			sb.insert(0, "Missing value for : ");
			//truncate last comma character
			sb.setLength(sb.length()-1);
			
			throw new PublishServiceException(sb.toString());
		}
	}
	
	/**
	 * 
	 * @param parameters
	 * @return
	 */
	private DbxClient setupDropBoxClient(Properties parameters){
		//1. obtain the config either direct or via proxy
		DbxRequestConfig config =createDbxRequestConfig();
		
		//2. obtain the oauth2 access token
		String accessToken = parameters.getProperty(ACCESS_TOKEN);
		
		//3. setup client
		return new DbxClient(config, accessToken);
	}
	/**
	 * 
	 * @param parameters
	 * @return
	 * @throws IOException
	 */
	private File createTemporaryFile(Properties parameters) throws IOException{
		return File.createTempFile("dropbox", 
								   ".tmp");
	}
	
	/**
	 * 
	 * @param parameters
	 * @return
	 */
	private byte[] decodeBase64Content(Properties parameters){
		String base64Content = parameters.getProperty(BASE64_ENCODED_CONTENT);
		
		return Base64.getDecoder().decode(base64Content);
	}
	
	/**
	 * 
	 * @return
	 */
	private DbxRequestConfig createDbxRequestConfig(){
		DbxRequestConfig config;

        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPort = System.getProperty("http.proxyPort");

        //if proxy server required
        if(proxyHost !=null && proxyPort!=null){
        	Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.valueOf(proxyPort)));
        	StandardHttpRequestor httpRequester = new StandardHttpRequestor(proxy);
        
        	config = new DbxRequestConfig("ReportBay/1.0",
					   Locale.getDefault().toString(),
					   httpRequester);
        }
        else{
        	config = new DbxRequestConfig("ReportBay/1.0",
        								  Locale.getDefault().toString());
        }
        
        return config;
	}
}