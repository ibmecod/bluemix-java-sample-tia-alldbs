/*-------------------------------------------------------------------*/
/*                                                                   */
/*                                                                   */
/* Copyright IBM Corp. 2013 All Rights Reserved                      */
/*                                                                   */
/*                                                                   */
/*-------------------------------------------------------------------*/
/*                                                                   */
/*        NOTICE TO USERS OF THE SOURCE CODE EXAMPLES                */
/*                                                                   */
/* The source code examples provided by IBM are only intended to     */
/* assist in the development of a working software program.          */
/*                                                                   */
/* International Business Machines Corporation provides the source   */
/* code examples, both individually and as one or more groups,       */
/* "as is" without warranty of any kind, either expressed or         */
/* implied, including, but not limited to the warranty of            */
/* non-infringement and the implied warranties of merchantability    */
/* and fitness for a particular purpose. The entire risk             */
/* as to the quality and performance of the source code              */
/* examples, both individually and as one or more groups, is with    */
/* you. Should any part of the source code examples prove defective, */
/* you (and not IBM or an authorized dealer) assume the entire cost  */
/* of all necessary servicing, repair or correction.                 */
/*                                                                   */
/* IBM does not warrant that the contents of the source code         */
/* examples, whether individually or as one or more groups, will     */
/* meet your requirements or that the source code examples are       */
/* error-free.                                                       */
/*                                                                   */
/* IBM may make improvements and/or changes in the source code       */
/* examples at any time.                                             */
/*                                                                   */
/* Changes may be made periodically to the information in the        */
/* source code examples; these changes may be reported, for the      */
/* sample code included herein, in new editions of the examples.     */
/*                                                                   */
/* References in the source code examples to IBM products, programs, */
/* or services do not imply that IBM intends to make these           */
/* available in all countries in which IBM operates. Any reference   */
/* to the IBM licensed program in the source code examples is not    */
/* intended to state or imply that IBM's licensed program must be    */
/* used. Any functionally equivalent program may be used.            */
/*-------------------------------------------------------------------*/
package com.sampleapp.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.json.JSONArray;
import org.apache.commons.json.JSONObject;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


// Uses Cloudant as a Data store
public class CloudantDAOFactory extends DAOFactory {

	// For grabbing vcap_services info
	
	private String url, username, password, database;

	// For interacting with Cloudant 
	// We use the Apache Commons HttpClient as 
	// Cloudant has a REST API
	private static CredentialsProvider credsProvider = new BasicCredentialsProvider();
	private CloseableHttpClient httpClient;

	public CloudantDAOFactory(Properties serviceProperties) {

		if (serviceProperties == null) {
			System.out.println("No VCAP_SERVICES found");
			return;
		}

		System.out.println("VCAP_SERVICES found");

		try {
		
			// Grab Cloudant credentials from VCAP_SERVICES
		
			url = serviceProperties.getProperty("url");
			username = serviceProperties.getProperty("username");
			password = serviceProperties.getProperty("password");
			database = serviceProperties.getProperty("database");

			System.out.println("Found all the params");

			// Setting up to access Cloudant db
			credsProvider.setCredentials(new AuthScope(username
					+ ".cloudant.com", 443), new UsernamePasswordCredentials(
					username, password));

			httpClient = HttpClients.custom()
					.setDefaultCredentialsProvider(credsProvider).build();

			System.out.println("Using Cloudant DB at url  " + url + "/"
					+ database);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void delSelected(String twitterName) {
		try {
			String record = getRecordAsJson(twitterName);
			JSONObject jsonObject = new JSONObject(record);
			jsonObject.put("_deleted", true);
			HttpPost post = new HttpPost(url + "/" + database);

			StringEntity parameters = new StringEntity(jsonObject.toString());
			parameters.setContentType("application/json");
			post.setEntity(parameters);
			CloseableHttpResponse response = httpClient.execute(post);
			int rc = response.getStatusLine().getStatusCode();
			if (rc == HttpStatus.SC_OK || rc == HttpStatus.SC_CREATED || rc == HttpStatus.SC_ACCEPTED) {
				String result = EntityUtils.toString(response.getEntity());
				System.out.println("Cloudant POST to delete data returned "
						+ result);
			} else {
				System.err.println("Fatal error deleting data: "
						+ response.getStatusLine());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void clearAll() {
		try {

			HttpGet get = new HttpGet(url + "/" + database + "/_all_docs");

			CloseableHttpResponse response = httpClient.execute(get);

			String result = EntityUtils.toString(response.getEntity());
			response.close();
			System.out.println(result);
			JSONObject obj = new JSONObject(result);

			JSONArray arr = obj.getJSONArray("rows");
			for (int i = 0; i < arr.length(); i++) {
			    String id = arr.getJSONObject(i).getString("id");
			    JSONObject revObj = arr.getJSONObject(i).getJSONObject("value");
			    String rev = revObj.getString("rev");
			    System.out.println("Deleting record with twitterName = " + id + " and rev = " + rev);
				deleteRecord(id, rev);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Deleted all records");

	}

	@Override
	public int getCount() {
        int count = 0;

		try {

			HttpGet get = new HttpGet(url + "/" + database + "/_all_docs");

			CloseableHttpResponse response = httpClient.execute(get);

			String result = EntityUtils.toString(response.getEntity());
			response.close();
			System.out.println(result);
			JSONObject obj = new JSONObject(result);
			
			count = obj.getInt("total_rows");


		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		return count;
		
	}

	@Override
	public List<Influencer> getAll() {
		List<Influencer> influencers = new ArrayList<Influencer>();

		try {
            // Get id of all docs and then lookup each doc to get the details
			HttpGet get = new HttpGet(url + "/" + database + "/_all_docs");

			CloseableHttpResponse response = httpClient.execute(get);

			String result = EntityUtils.toString(response.getEntity());
			response.close();
			System.out.println(result);
			JSONObject obj = new JSONObject(result);

			JSONArray arr = obj.getJSONArray("rows");
			for (int i = 0; i < arr.length(); i++) {
				Influencer influencer = new Influencer();
				JSONObject record = new JSONObject(getRecordAsJson(arr
						.getJSONObject(i).getString("id")));
				influencer.setTwitterHandle(record.getString("_id"));
				influencer.setFcount(record.getInt("fcount"));
				influencer.setMcount(record.getInt("mcount"));
				influencer.setFscore(record.getInt("fscore"));
				influencer.setRtcount(record.getInt("rtcount"));
				influencer.setTotalscore(record.getInt("totalscore"));
				influencers.add(influencer);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return influencers;
	}

	@Override
	public void saveData(Influencer influencer) {
		// check whether the document is present in the database
		// if not present just insert new doc or else just update the existing
		// doc.
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("_id", influencer.getTwitterHandle());
			jsonObject.put("mcount", influencer.getMcount());
			jsonObject.put("fcount", influencer.getFcount());
			jsonObject.put("fscore", influencer.getFscore());
			jsonObject.put("rtcount", influencer.getRtcount());
			jsonObject.put("rtscore", influencer.getRtscore());
			jsonObject.put("totalscore", influencer.getTotalscore());
			HttpPost post = new HttpPost(url + "/" + database);
			System.out.println("New data: " + jsonObject.toString());
			StringEntity parameters = new StringEntity(jsonObject.toString());
			parameters.setContentType("application/json");
			post.setEntity(parameters);
			CloseableHttpResponse response = httpClient.execute(post);
			int rc = response.getStatusLine().getStatusCode();
			if (rc == HttpStatus.SC_OK || rc == HttpStatus.SC_CREATED || rc == HttpStatus.SC_ACCEPTED) {
				String result = EntityUtils.toString(response.getEntity());
				System.out.println("Cloudant POST to add new data returned "
						+ result);
			} else {
				System.err.println("Fatal error saving data: "
						+ response.getStatusLine());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Returns single record as JSON
	private String getRecordAsJson(String key) throws Exception {
		String result;
		HttpGet httpget = new HttpGet(url + "/" + database + "/" + key);
		CloseableHttpResponse response = httpClient.execute(httpget);
		int rc = response.getStatusLine().getStatusCode();
		if (rc == HttpStatus.SC_OK || rc == HttpStatus.SC_CREATED || rc == HttpStatus.SC_ACCEPTED) {
			result = EntityUtils.toString(response.getEntity());
			System.out.println("Get record returns " + result);
		} else {
			throw new Exception("getRecord returns " + response.getStatusLine());
		}

		return result;
	}
	
	// Deletes a single record using the id and rev
	private String deleteRecord(String key, String rev) throws Exception {
		String result;
		HttpDelete httpdelete = new HttpDelete(url + "/" + database + "/" + key  + "?rev=" + rev);
		CloseableHttpResponse response = httpClient.execute(httpdelete);
		int rc = response.getStatusLine().getStatusCode();
		if (rc == HttpStatus.SC_OK || rc == HttpStatus.SC_CREATED || rc == HttpStatus.SC_ACCEPTED) {
			result = EntityUtils.toString(response.getEntity());
			System.out.println("Delete  record returns " + result);
		} else {
			throw new Exception("deleteRecord  returns " + response.getStatusLine());
		}

		return result;
	}


}
