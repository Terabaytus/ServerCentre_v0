package ru.cod;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogicFotoVideo {
	
   public static Connection conn;
   public static Statement statmt;
   public static ResultSet resSet;
   
   static String fileNameUTF;
   static String date;
   static String dataGPS;
   private static double dataGPS1;
   static String dataGPS2;
   private static double dataGPS3;
   static String number;
   static String informationAboutFile;
   
   
   static long inumber1;
   static int idate1;
		
	/*********************************������ �������� �����**********************/
    public void logicFiles(String URL, String USER, String PASSWORD, String pathToFilesFotoVideo, int filesCount, long fileSize, String fileName, DataInputStream din, Socket socket){
    	
    	String hostname = "jdbc:mysql://"+URL+"?useSSL=false&characterEncoding=utf8";
					
					File f1 = new File(pathToFilesFotoVideo + fileName);

                 System.out.println(f1);
                 System.out.println(fileName);
                 
                    if(f1.exists()){//��������� �� ������������� ����� � �����
                        
                        System.out.println("����� ���� ����");//������ ���� ����
                        
                      if(fileSize == f1.length()){// ��������� ����� �� �������
                          System.out.println("������� ���������");     
                      long missingBytes = -2;
                      System.out.println("����� " + missingBytes + " �������� �������");
                  
                      try {
                      
                      DataOutputStream outD;// ���������� ������ ������������ ������
                      outD = new DataOutputStream(socket.getOutputStream());//������������ � socket   
                      outD.writeLong(missingBytes);//������������ ����� ������� �������
                      outD.flush();
                   
                      } catch (IOException e) {
                  e.printStackTrace();
                  } 
                } else { 
                              System.out.println("������ ����� �� ���������");
                              
                              long missingBytes = fileSize - f1.length();
                              long missingBytes1 = fileSize - missingBytes;
                              System.out.println( missingBytes1 +  " �� ��������� ����, �������� �������");
                          
                              try {
                              
                              DataOutputStream outD;// ���������� ������ ������������ ������
                              outD = new DataOutputStream(socket.getOutputStream());//������������ � socket   
                              outD.writeLong(missingBytes1);//������������ ����� ������� �������
                              outD.flush();
                              fileSize = missingBytes;//�������� ��������� ������ ����� ����������� ������� ���������� �� ��������� �� ���������� ������� �����
                              System.out.println("���������� " +fileSize);
                              System.out.println("���������� ���������� �����");
                              
                              numberMissingBytes(pathToFilesFotoVideo, filesCount, fileSize, fileName, din, socket);
                              
                              
                              } catch (IOException e) {
                          e.printStackTrace();
                         }  
              }
                    } else {
                      
                  System.out.println("������ ����� ���");//��� ���� ����
                  
                  try {
                      long missingBytes = -3;
                      System.out.println("����� " + missingBytes + " �������� �������");
                      
                      DataOutputStream outD;// ���������� ������ ������������ ������
                      outD = new DataOutputStream(socket.getOutputStream());//������������ � socket   
                      outD.writeLong(missingBytes);//������������ ����� ������� �������
                      outD.flush();
                     
                      downloadFiles(hostname, USER, PASSWORD, pathToFilesFotoVideo, filesCount, fileSize, fileName, din, socket); 
                  
                      } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            }
/*********************************�����*********************************/ 
      
      /*********************************�������� �����**********************/
    public static void downloadFiles(String hostname, String USER, String PASSWORD, String pathToFilesFotoVideo, int filesCount, long fileSize, String fileName, DataInputStream din, Socket socket){
           
     	//---------------������� ����� ��� ��������-----------------------
          File directory = new File(pathToFilesFotoVideo);
          if (!directory.exists())
         	 directory.mkdirs();
          //---------------�����-----------------------
     	 
     	 try {    
	        	 
         	 	 parsingDate(fileName);
	             parsingGPS1(fileName);
	             parsingGPS2(fileName);
	             parsingIMEI(fileName);
	             parsinginformationAboutFile(fileName);
	             
	            System.out.println("��� �����: " + fileName+"\n");
	            System.out.println("������ �����: " + fileSize + " ����\n");
         
                byte[] buffer = new byte[64*1024];
                FileOutputStream outF = new FileOutputStream(pathToFilesFotoVideo + fileName);
               
         dataBase(hostname, USER, PASSWORD, fileName);
                
                int count, total = 0;
                
                while ((count = din.read(buffer, 0, (int) Math.min(buffer.length, fileSize-total))) != -1){               
                    total += count;
                    outF.write(buffer, 0, count);
                
                    if(total == fileSize){
                        break;
                    }
                }
                
                outF.flush();
                outF.close();
               
                System.out.println("���� ������\n---------------------------------\n");            
            
        }catch(Exception e){
            e.printStackTrace();
           }
        }      
               
     /*********************************�����*********************************/             
      
      /*********************************�������� ���������� ����**********************/
private static void numberMissingBytes(String pathToFilesFotoVideo, int filesCount, long fileSize, String fileName, DataInputStream din, Socket socket){
             
			System.out.println("��� �����: " + fileName+"\n");
			System.out.println("������ �����: " + fileSize + " ����\n");
       try { 
               byte[] buffer = new byte[64*1024];
               FileOutputStream outF = new FileOutputStream(pathToFilesFotoVideo + fileName, true);
              
               int count, total = 0;
               
               while ((count = din.read(buffer, 0, (int) Math.min(buffer.length, fileSize-total))) != -1){               
                   total += count;
                   outF.write(buffer, 0, count);
               
                   if(total == fileSize){
                       break;
                   }
                }
               
               outF.flush();
               outF.close();
               System.out.println("���� ������\n---------------------------------\n");            
           
       }catch(Exception e){
           e.printStackTrace();
          }
         }
 /*********************************�����*********************************/ 
      
	/*********************************������ � ��* @throws ClassNotFoundException **********************/
@SuppressWarnings("unused")
public static void dataBase(String hostname, String USER, String PASSWORD, String fileName) throws SQLException, ClassNotFoundException{
		
		Connection con = null;
		Statement stmt = null;
		int rs;
		
		// --------���������� ������--------
			try{
				Class.forName("com.mysql.jdbc.Driver");
				String query = "INSERT INTO  files (name_file, id_telephone, coordinate1, coordinate2\n"// ��������� ������ � ������� files
						+ ",about,timemodify)\n"
							+ "VALUES('"+fileName+"','"+inumber1+"','"+dataGPS1+"','"+dataGPS3+"'\n"
									+ ",'"+informationAboutFile+"','"+date+"');";
				
				
				// opening database connection to MySQL server
				con = DriverManager.getConnection(hostname, USER, PASSWORD);
	
	            // getting Statement object to execute query
	            stmt = con.createStatement();
	
	           // executing SELECT query
	          rs = stmt.executeUpdate(query);
	         
	        } catch (SQLException sqlEx) {
	            sqlEx.printStackTrace();
	        } finally {
	            //close connection ,stmt and resultset here
	            try { con.close(); } catch(SQLException se) { /*can't do anything */}
	            try { stmt.close(); } catch(SQLException se) { /*can't do anything */}
	
	            }
			
			}
	/*********************************�����*********************************/
	
	public static void parsingDate(String fileName){
	               
	               //fileName ��� ����:
	                  // ��� ����: photo 20170605  084154   00.0000    00.0000     353346056397888      ���������� ��� ������� � ����� �����..jpg
	                  String rx = "(\\w{5})\\s(\\d{4})(\\d{2})(\\d{2})\\s+(\\d{2})(\\d{2})(\\d{2})\\s{3}(\\d{0,}.\\d{0,})\\s{4}(\\d{0,}.\\d{0,})\\s{5}(\\d{1,})\\s{6}([0-9�-��-߸�,.()\\s]*+\\w{3})";
	                  Pattern pattern = Pattern.compile(rx);
	                  Matcher matcher = pattern.matcher(fileName);
	                  StringBuffer strbuf = new StringBuffer();
	                  while (matcher.find()) {
	                      // �� ��� ������:
	                      matcher.appendReplacement(strbuf, "$2-$3-$4 $5:$6:$7");//������
	                  }
	                  matcher.appendTail(strbuf);
	                  // Result: 09-07-2016 15:49:15
	                  date = strbuf.toString().trim();
	                  System.out.println(strbuf.toString().trim());
	                 
	            }
	            /*********************************�����*********************************/ 
	
	            /*********************************������������� DataGPS**********************/
    public static void parsingGPS1(String fileName){
		 try {
	        Pattern pattern = Pattern.compile("(\\s{3}(\\d){1,}\\.(\\d){1,}\\s{4})"); 
	        Matcher matcher = pattern.matcher(fileName); 
	
	       while (matcher.find()) {
	       dataGPS = matcher.group();
	       dataGPS1 = Double.parseDouble(dataGPS.trim());
	       System.out.println(dataGPS1);
	       }
		 } catch (Exception e){}
	}
	
	public static void parsingGPS2(String fileName){
		try {
	    Pattern pattern = Pattern.compile("(\\s{4}(\\d){1,}\\.(\\d){1,}\\s{5})"); 
	    Matcher matcher = pattern.matcher(fileName); 
	
	  while (matcher.find()){ 
	  dataGPS2 = matcher.group();
	  dataGPS3 = Double.parseDouble(dataGPS2.trim());
	  System.out.println(dataGPS3);
	  }
		} catch (Exception e){}
	}
	/*********************************�����*********************************/ 
	           
	/*********************************������������� IMEI**********************/
	 public static void parsingIMEI(String fileName){
	            
	                Pattern pattern = Pattern.compile("\\s{5}(\\d){9,}\\s{6}"); 
	                Matcher matcher = pattern.matcher(fileName); 
	
	                while (matcher.find()) 
	                number = matcher.group();
	                
	                try {
	                	
	                	inumber1 = Long.parseLong(number.trim());
	                   
	                } catch (NumberFormatException e) {
	                    System.err.println("�������� ������ ������!");
	                }                  
	                System.out.println(inumber1);
	            }
	            /*********************************�����*********************************/ 
	            
	            /*********************************������������� informationAboutFile**********************/
	  public static void parsinginformationAboutFile(String fileName) throws UnsupportedEncodingException{
	            	 
	                    Pattern pattern = Pattern.compile("\\s{6}[0-9�-��-߸�,.()\\s]*+"); 
	                    Matcher matcher = pattern.matcher(fileName); 
	                    while (matcher.find()) 
	                    informationAboutFile = matcher.group();
	                    informationAboutFile.trim();
	                    System.out.println(informationAboutFile.trim());
	            }
	  /*********************************�����*********************************/ 
}

