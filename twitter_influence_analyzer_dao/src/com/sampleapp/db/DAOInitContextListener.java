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

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.json.JSONArray;
import org.apache.commons.json.JSONObject;

// Inspects attached services and looks for a database service
// Once it finds a service it instantiates the appropriate  concrete DAOFactory for
// use throughout the application. If multiple database services are attached to the application
// it will stop after the first one it finds based on the search order in the code
public class DAOInitContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {

	}

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		String dao = null;
		Properties  serviceProperties = null;

		
		// Find database service - assumes only one
		Map<String, String> env;
		String vcap;
		JSONObject jsonProperties, credentials;

		env = System.getenv();
		vcap = env.get("VCAP_SERVICES");
		try {
			JSONObject vcap_services = new JSONObject(vcap);
			Iterator iter = vcap_services.keys();
			
			if (vcap == null) {
				System.err
						.println("DAOContextListener: No VCAP_SERVICES found!");
				return;
			}

			while (iter.hasNext()) {
				String key = (String) iter.next();
				if (key.startsWith("mongodb")) {
					dao = DAOFactory.MONGO;
					jsonProperties = vcap_services.getJSONArray(key).getJSONObject(0);
					credentials = jsonProperties.getJSONObject("credentials");
					serviceProperties = new Properties();
					serviceProperties.put("hostname", credentials.getString("hostname"));
					serviceProperties.put("port", credentials.getString("port"));
					serviceProperties.put("username", credentials.getString("username"));
					serviceProperties.put("password", credentials.getString("password"));
					serviceProperties.put("db", credentials.getString("db"));
				
					break;
				}
				if (key.startsWith("SQLDB")) {
					dao = DAOFactory.SQLDB;
					jsonProperties = vcap_services.getJSONArray(key).getJSONObject(0);
					credentials = jsonProperties.getJSONObject("credentials");
					serviceProperties = new Properties();
					serviceProperties.put("jdbcurl", credentials.getString("jdbcurl"));				
					serviceProperties.put("username", credentials.getString("username"));
					serviceProperties.put("password", credentials.getString("password"));
					break;
				}
				if (key.startsWith("mysql")) {
					dao = DAOFactory.MYSQL;
					jsonProperties = vcap_services.getJSONArray(key).getJSONObject(0);
					credentials = jsonProperties.getJSONObject("credentials");
					serviceProperties = new Properties();
					serviceProperties.put("name", credentials.getString("name"));
					serviceProperties.put("port", credentials.getString("port"));
					serviceProperties.put("username", credentials.getString("username"));
					serviceProperties.put("password", credentials.getString("password"));
					serviceProperties.put("host", credentials.getString("host"));
					break;
				}
				if (key.startsWith("postgresql")) {
					dao = DAOFactory.POSTGRES;
					jsonProperties = vcap_services.getJSONArray(key).getJSONObject(0);
					credentials = jsonProperties.getJSONObject("credentials");
					serviceProperties = new Properties();
					serviceProperties.put("name", credentials.getString("name"));
					serviceProperties.put("port", credentials.getString("port"));
					serviceProperties.put("username", credentials.getString("username"));
					serviceProperties.put("password", credentials.getString("password"));
					serviceProperties.put("host", credentials.getString("host"));
					break;
				}
				if (key.startsWith("user-provided")) {
					JSONArray array = vcap_services.getJSONArray(key);
					JSONObject obj = array.getJSONObject(0);
					if (obj.getString("name").startsWith("Cloudant")) {
						dao = DAOFactory.CLOUDANT;
						credentials = obj.getJSONObject("credentials");
						serviceProperties = new Properties();
						serviceProperties.put("url", credentials.getString("url"));				
						serviceProperties.put("username", credentials.getString("username"));
						serviceProperties.put("password", credentials.getString("password"));
						serviceProperties.put("database", credentials.getString("database"));
						break;
					}
				}
				if (key.startsWith("JSONDB")) {
					dao = DAOFactory.JSONDB;
					jsonProperties = vcap_services.getJSONArray(key).getJSONObject(0);
					credentials = jsonProperties.getJSONObject("credentials");
					serviceProperties = new Properties();
					serviceProperties.put("hostname", credentials.getString("hostname"));
					serviceProperties.put("port", credentials.getString("port"));
					serviceProperties.put("username", credentials.getString("username"));
					serviceProperties.put("password", credentials.getString("password"));
					serviceProperties.put("db", credentials.getString("db"));
					break;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		if (dao == null) {
			System.err.println("DAOContextListener: Fatal error no DB service found !");
			return;
		}
		
		System.out.println("DAOContextListener: " + dao + " database service found");
		final DAOFactory daoFactory = DAOFactory.getDAOFactory(dao, serviceProperties);
		contextEvent.getServletContext().setAttribute("daoname", dao);
		contextEvent.getServletContext().setAttribute("daofactory", daoFactory);

	}

}
