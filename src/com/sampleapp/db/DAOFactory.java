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

import java.util.List;
import java.util.Properties;

// Basic implementation of the Data Access Object JEE Pattern
// This abstract class is used by the rest of the code to access the underlying 
// database. The getDAOFactory methods instantiates the right
// concrete (ie db specific) implementation of the DAOFactory 
public abstract class DAOFactory {

	// Supported database backends
	public static final String MONGO = "MONGO";
	public static final String SQLDB = "SQLDB";
	public static final String MYSQL = "MYSQL";
	public static final String POSTGRES = "POSTGRES";
	public static final String JSONDB = "JSONDB";
	public static final String CLOUDANT = "CLOUDANT";

	/* DAO methods implemented by all concrete DAOs */
	public abstract void delSelected(String twitterName);

	public abstract void clearAll();
	
	public abstract void saveData(Influencer influencer);

	public abstract int getCount();

	public abstract List<Influencer> getAll();

	public static DAOFactory getDAOFactory(String factory, Properties properties) {

		switch (factory) {
		case MONGO:
			return new MongoDAOFactory(properties);
		case SQLDB:
			return new SQLDBDAOFactory(properties);
		case CLOUDANT:
			return new CloudantDAOFactory(properties);
		case MYSQL:
			return new MySQLDAOFactory(properties);
		case POSTGRES:
			return new PostgreSQLDAOFactory(properties);
		case JSONDB:
			return new JSONDBDAOFactory(properties);
		default:
			return null;
		}
	}

}
