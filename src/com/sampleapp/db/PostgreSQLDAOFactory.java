package com.sampleapp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PostgreSQLDAOFactory extends DAOFactory {

	// For grabbing vcap_services info
	
	private String jdbcurl, database, port, host, username, password;

	// For interacting with MySQL
	private Connection connection;

	public PostgreSQLDAOFactory(Properties serviceProperties) {
		
		if (serviceProperties == null) {
			System.out.println("No VCAP_SERVICES found");
			return;
		}

		System.out.println("VCAP_SERVICES found");

		try {
			
			database = serviceProperties.getProperty("name");
			port = serviceProperties.getProperty("port");
			host  = serviceProperties.getProperty("host");
			username = serviceProperties.getProperty("username");
			password = serviceProperties.getProperty("password");  
		
            jdbcurl = "jdbc:postgresql://" + host + ":" + port + "/" + database;
			System.out.println("Found all the params");

			// MySQL initialization
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(jdbcurl, username,
					password);

			System.out.println("Connected to PostgreSQL using URL  " + jdbcurl);

			initDB();

			System.out.println("Created table for application");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	// Checks if single table used by this app exists and creates it if it doesn't
	private void initDB() throws SQLException {

		String checkIfExistsSql = "SELECT tablename FROM pg_catalog.pg_tables  WHERE schemaname = 'public' AND tablename = 'influencer'";
		String createTableSql = "create table influencer (twitterName varchar(32) NOT NULL, mcount int, fcount int, fscore int, rtcount int, rtscore int, totalscore int, PRIMARY KEY(twitterName))";

		Statement checkIfExistsStatement = connection.createStatement();
		ResultSet rs = checkIfExistsStatement.executeQuery(checkIfExistsSql);
		if (!rs.next()) {
			Statement createTableStatement = connection.createStatement();
			createTableStatement.execute(createTableSql);
			createTableStatement.close();
		}
		checkIfExistsStatement.close();

	}

	@Override
	public void delSelected(String twitterName) {
		String sql = "delete from influencer where twitterName=?";
		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, twitterName);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void clearAll() {
		String sql = "delete from influencer";
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void saveData(Influencer influencer) {
		String sqlCreate  = "insert into influencer (twitterName, mcount, fcount, fscore, rtcount, rtscore, totalscore) values(?,?,?,?,?,?,?)";
		String sqlUpdate  = "update influencer set mcount=?, fcount=?, fscore=?, rtcount=?, rtscore=?, totalscore=? where twitterName=?";
		int numRows = 0;
		// Try to update first,  if it isn't there then create it
		try {
        	PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate);       	
        	preparedStatement.setInt(1, influencer.getMcount());
        	preparedStatement.setInt(2, influencer.getFcount());
        	preparedStatement.setInt(3, influencer.getFscore());
        	preparedStatement.setInt(4, influencer.getRtcount());
        	preparedStatement.setInt(5, influencer.getRtscore());
        	preparedStatement.setInt(6, influencer.getTotalscore());
        	preparedStatement.setString(7, influencer.getTwitterHandle());
        	numRows = preparedStatement.executeUpdate();    	
        	if (numRows == 0) {
        	    preparedStatement = connection.prepareStatement(sqlCreate);  
        		preparedStatement.setString(1, influencer.getTwitterHandle());
            	preparedStatement.setInt(2, influencer.getMcount());
            	preparedStatement.setInt(3, influencer.getFcount());
            	preparedStatement.setInt(4, influencer.getFscore());
            	preparedStatement.setInt(5, influencer.getRtcount());
            	preparedStatement.setInt(6, influencer.getRtscore());
            	preparedStatement.setInt(7, influencer.getTotalscore());
            	preparedStatement.executeUpdate();
        	}
        	preparedStatement.close();
        }
        catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getCount() {

		int count = 0;

		String sql = "select count(*) from influencer";

		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			if (rs.next()) {
				count = rs.getInt(1);
			}
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return count;

	}

	@Override
	public List<Influencer> getAll() {
		String sql = "select twitterName, mcount, fcount, fscore, rtcount, rtscore, totalscore from influencer";
		List<Influencer> listOfInfluencers = new ArrayList<Influencer>();
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				Influencer influencer = new Influencer();
				influencer.setTwitterHandle(rs.getString(1));
				influencer.setMcount(rs.getInt(2));
				influencer.setFcount(rs.getInt(3));
				influencer.setFscore(rs.getInt(4));
				influencer.setRtcount(rs.getInt(5));
				influencer.setRtscore(rs.getInt(6));
				influencer.setTotalscore(rs.getInt(7));
				listOfInfluencers.add(influencer);
			}
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listOfInfluencers;
	}

}
