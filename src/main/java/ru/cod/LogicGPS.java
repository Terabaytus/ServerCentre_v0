package ru.cod;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/*********************************������ �������� GPS ���������**********************/
public class LogicGPS {
	
		//JDBC variables for opening and managing connection
		
		private static Connection con;
		
		private static Statement stmt;
		
		@SuppressWarnings("unused")
		private static int rs;
		
		public LogicGPS(String URLGPS, String USERGPS, String PASSWORDGPS, String id_telephone, String latitude, String longitude, String timemodify, String speed) {
			
			String hostname = "jdbc:mysql://"+URLGPS+"?useSSL=false";
			
			System.out.println("id_telephone =" + id_telephone + "\n" + "���������� =" + " " + latitude + "\n"+ " " + longitude + "\n"+ " " + timemodify + "\n"+ " " + speed + "\n");
					               
					               // --------���������� ������--------
					   			try{
					   				try {
										Class.forName("com.mysql.jdbc.Driver");
									} catch (ClassNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
					   				String query = "INSERT INTO  coordinates_centre (id_telephone, latitude, longitude\n"// ��������� ������ � ������� coordinates_centre
					   						+", timemodify, speed)\n"
					   							+ "VALUES('"+id_telephone+"', '"+latitude+"', '"+longitude+"', '"+timemodify+"', '"+speed+"');";
					   				
					   				// opening database connection to MySQL server
					   	            con = DriverManager.getConnection(hostname, USERGPS, PASSWORDGPS);
					   	
					   	            // getting Statement object to execute query
					   	            stmt = con.createStatement();
					   	
					   	           // executing SELECT query
					   	            rs = stmt.executeUpdate(query);
					   	         
					   	        } catch (SQLException sqlEx) {
					   	            sqlEx.printStackTrace();
					   	        } finally {
					   	            //close connection ,stmt and resultset here
					   	            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
					   	            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
					   	        }
					   		// --------�����--------
						}
					}
/*********************************������ �������� GPS ���������*********************************/   		
					
