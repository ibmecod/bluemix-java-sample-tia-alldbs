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
import java.util.List;
import java.util.Properties;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class MongoDAOFactory extends DAOFactory {

	// For grabbing vcap_services info

	private String host, port, username, password, database;

	// For interacting with mongo
	private Mongo mongoClient;
	private DB db;
	private DBCollection infColl; // influencer collection


	
	public MongoDAOFactory(Properties serviceProperties) {

	
		if (serviceProperties == null) {
			System.out.println("No VCAP_SERVICES found");
			return;
		}

		System.out.println("VCAP_SERVICES found");

		try {
		
			host = serviceProperties.getProperty("hostname");
			port = serviceProperties.getProperty("port");
			username = serviceProperties.getProperty("username");
			password = serviceProperties.getProperty("password");
			database = serviceProperties.getProperty("db");

			System.out.println("Found all the params");

			// Mongo initialization
			mongoClient = new Mongo(host, Integer.parseInt(port));
			db = mongoClient.getDB(database);

			System.out.println("Connected to mongoDB on " + host + ":" + port);

			if (db.authenticate(username, password.toCharArray())) {
				infColl = db.getCollection("infcollection");
				System.out.println("Authenticated with mongoDB successfully");
			} else {
				throw new Exception("Authentication Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void delSelected(String twitterName) {
		infColl.remove(new BasicDBObject().append("twitname", twitterName));
		System.out.println(twitterName + " record deleted");

	}

	@Override
	public void clearAll() {
		infColl.remove(new BasicDBObject());
		System.out.println("Deleted all records");

	}

	@Override
	public int getCount() {
	
		return (int) infColl.getCount();
	}

	@Override
	public List<Influencer> getAll() {
	   List<Influencer>	influencers = new ArrayList<Influencer>();
	   
	   List<DBObject> records = infColl.find().toArray();
		
	
		for (DBObject record : records) {
			Influencer influencer = new Influencer();
			influencer.setTwitterHandle(record.get("twitname").toString());
			
			influencer.setMcount(Integer.parseInt(record.get("mcount").toString()));
			influencer.setFcount(Integer.parseInt(record.get("fcount").toString()));
			influencer.setFscore(Integer.parseInt(record.get("fscore").toString()));
			influencer.setRtcount(Integer.parseInt(record.get("rtcount").toString()));
			influencer.setRtcount(Integer.parseInt(record.get("rtscore").toString()));
			influencer.setTotalscore(Integer.parseInt(record.get("totalscore").toString()));
			influencers.add(influencer);
		}
		
		return influencers;
	}
	
	@Override
	public void saveData(Influencer influencer) {
		// check whether the document is present in the database
		// if not present just insert new doc or else just update the existing doc. 
		BasicDBObject query = new BasicDBObject("twitname", influencer.getTwitterHandle());
		
		boolean documentExists = infColl.find(query).count() != 0;
		
		if (documentExists) {
			// Update the existing record
			infColl.update(new BasicDBObject().append("twitname",influencer.getTwitterHandle()), new BasicDBObject().append("$set",new BasicDBObject().append("totalscore",influencer.getTotalscore())));
			infColl.update(new BasicDBObject().append("twitname",influencer.getTwitterHandle()), new BasicDBObject().append("$set",new BasicDBObject().append("fcount",influencer.getFcount())));
			infColl.update(new BasicDBObject().append("twitname",influencer.getTwitterHandle()), new BasicDBObject().append("$set",new BasicDBObject().append("fscore",influencer.getFscore())));
			infColl.update(new BasicDBObject().append("twitname",influencer.getTwitterHandle()), new BasicDBObject().append("$set",new BasicDBObject().append("rtcount",influencer.getRtcount())));
			infColl.update(new BasicDBObject().append("twitname",influencer.getTwitterHandle()), new BasicDBObject().append("$set",new BasicDBObject().append("rtscore",influencer.getRtscore())));
			infColl.update(new BasicDBObject().append("twitname",influencer.getTwitterHandle()), new BasicDBObject().append("$set",new BasicDBObject().append("mcount",influencer.getMcount())));
			
			System.out.println("Existing document updated");
		} else {
			// Insert the new record
			BasicDBObject doc = new BasicDBObject("twitname", influencer.getTwitterHandle())
					.append("totalscore", influencer.getTotalscore())
					.append("fcount", influencer.getFcount())
					.append("fscore", influencer.getFscore())
					.append("rtcount", influencer.getRtcount())
					.append("rtscore", influencer.getRtscore())
					.append("mcount", influencer.getMcount());
			
			infColl.insert(doc);
			System.out.println("New record successfully inserted");
		}
	}

}
