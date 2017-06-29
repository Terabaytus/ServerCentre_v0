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
		
	/*********************************ЛОГИКА ДОГРУЗКИ ФАЙЛА**********************/
    public void logicFiles(String URL, String USER, String PASSWORD, String pathToFilesFotoVideo, int filesCount, long fileSize, String fileName, DataInputStream din, Socket socket){
    	
    	String hostname = "jdbc:mysql://"+URL+"?useSSL=false&characterEncoding=utf8";
					
					File f1 = new File(pathToFilesFotoVideo + fileName);

                 System.out.println(f1);
                 System.out.println(fileName);
                 
                    if(f1.exists()){//проверяем на существование файла в папке
                        
                        System.out.println("Такой файл есть");//истина если есть
                        
                      if(fileSize == f1.length()){// проверяем файлы по размеру
                          System.out.println("Размеры совпадают");     
                      long missingBytes = -2;
                      System.out.println("Число " + missingBytes + " отослано клиенту");
                  
                      try {
                      
                      DataOutputStream outD;// переменная потока отправляемых данных
                      outD = new DataOutputStream(socket.getOutputStream());//подключаемся к socket   
                      outD.writeLong(missingBytes);//отправляется ответ сервера клиенту
                      outD.flush();
                   
                      } catch (IOException e) {
                  e.printStackTrace();
                  } 
                } else { 
                              System.out.println("Размер файла не совпадает");
                              
                              long missingBytes = fileSize - f1.length();
                              long missingBytes1 = fileSize - missingBytes;
                              System.out.println( missingBytes1 +  " не закаченых байт, отослано клиенту");
                          
                              try {
                              
                              DataOutputStream outD;// переменная потока отправляемых данных
                              outD = new DataOutputStream(socket.getOutputStream());//подключаемся к socket   
                              outD.writeLong(missingBytes1);//отправляется ответ сервера клиенту
                              outD.flush();
                              fileSize = missingBytes;//заменяем настоящий размер файла оставшимеся байтами получеными от вычетания из настоящего размера файла
                              System.out.println("Закачиваем " +fileSize);
                              System.out.println("Закачиваем оставшиеся байты");
                              
                              numberMissingBytes(pathToFilesFotoVideo, filesCount, fileSize, fileName, din, socket);
                              
                              
                              } catch (IOException e) {
                          e.printStackTrace();
                         }  
              }
                    } else {
                      
                  System.out.println("Такого файла нет");//лож если нету
                  
                  try {
                      long missingBytes = -3;
                      System.out.println("Число " + missingBytes + " отослано клиенту");
                      
                      DataOutputStream outD;// переменная потока отправляемых данных
                      outD = new DataOutputStream(socket.getOutputStream());//подключаемся к socket   
                      outD.writeLong(missingBytes);//отправляется ответ сервера клиенту
                      outD.flush();
                     
                      downloadFiles(hostname, USER, PASSWORD, pathToFilesFotoVideo, filesCount, fileSize, fileName, din, socket); 
                  
                      } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            }
/*********************************КОНЕЦ*********************************/ 
      
      /*********************************ЗАГРУЗКА ФАЙЛА**********************/
    public static void downloadFiles(String hostname, String USER, String PASSWORD, String pathToFilesFotoVideo, int filesCount, long fileSize, String fileName, DataInputStream din, Socket socket){
           
     	//---------------СОЗДАЁМ ПАПКУ ДЛЯ ЗАГРУЗОК-----------------------
          File directory = new File(pathToFilesFotoVideo);
          if (!directory.exists())
         	 directory.mkdirs();
          //---------------КОНЕЦ-----------------------
     	 
     	 try {    
	        	 
         	 	 parsingDate(fileName);
	             parsingGPS1(fileName);
	             parsingGPS2(fileName);
	             parsingIMEI(fileName);
	             parsinginformationAboutFile(fileName);
	             
	            System.out.println("Имя файла: " + fileName+"\n");
	            System.out.println("Размер файла: " + fileSize + " байт\n");
         
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
               
                System.out.println("Файл принят\n---------------------------------\n");            
            
        }catch(Exception e){
            e.printStackTrace();
           }
        }      
               
     /*********************************КОНЕЦ*********************************/             
      
      /*********************************ЗАГРУЗКА ОСТАВШИХСЯ БАЙТ**********************/
private static void numberMissingBytes(String pathToFilesFotoVideo, int filesCount, long fileSize, String fileName, DataInputStream din, Socket socket){
             
			System.out.println("Имя файла: " + fileName+"\n");
			System.out.println("Размер файла: " + fileSize + " байт\n");
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
               System.out.println("Файл принят\n---------------------------------\n");            
           
       }catch(Exception e){
           e.printStackTrace();
          }
         }
 /*********************************КОНЕЦ*********************************/ 
      
	/*********************************ЗАПРОС К БД* @throws ClassNotFoundException **********************/
@SuppressWarnings("unused")
public static void dataBase(String hostname, String USER, String PASSWORD, String fileName) throws SQLException, ClassNotFoundException{
		
		Connection con = null;
		Statement stmt = null;
		int rs;
		
		// --------ДОБАВЛЕНИЕ ДАННЫХ--------
			try{
				Class.forName("com.mysql.jdbc.Driver");
				String query = "INSERT INTO  files (name_file, id_telephone, coordinate1, coordinate2\n"// добавляем данные в таблицу files
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
	/*********************************КОНЕЦ*********************************/
	
	public static void parsingDate(String fileName){
	               
	               //fileName где ищем:
	                  // что ищем: photo 20170605  084154   00.0000    00.0000     353346056397888      Фотография без подписи и аудио файла..jpg
	                  String rx = "(\\w{5})\\s(\\d{4})(\\d{2})(\\d{2})\\s+(\\d{2})(\\d{2})(\\d{2})\\s{3}(\\d{0,}.\\d{0,})\\s{4}(\\d{0,}.\\d{0,})\\s{5}(\\d{1,})\\s{6}([0-9а-яА-ЯёЁ,.()\\s]*+\\w{3})";
	                  Pattern pattern = Pattern.compile(rx);
	                  Matcher matcher = pattern.matcher(fileName);
	                  StringBuffer strbuf = new StringBuffer();
	                  while (matcher.find()) {
	                      // на что меняем:
	                      matcher.appendReplacement(strbuf, "$2-$3-$4 $5:$6:$7");//группа
	                  }
	                  matcher.appendTail(strbuf);
	                  // Result: 09-07-2016 15:49:15
	                  date = strbuf.toString().trim();
	                  System.out.println(strbuf.toString().trim());
	                 
	            }
	            /*********************************КОНЕЦ*********************************/ 
	
	            /*********************************РАСПОЗНОВАНИЕ DataGPS**********************/
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
	/*********************************КОНЕЦ*********************************/ 
	           
	/*********************************РАСПОЗНОВАНИЕ IMEI**********************/
	 public static void parsingIMEI(String fileName){
	            
	                Pattern pattern = Pattern.compile("\\s{5}(\\d){9,}\\s{6}"); 
	                Matcher matcher = pattern.matcher(fileName); 
	
	                while (matcher.find()) 
	                number = matcher.group();
	                
	                try {
	                	
	                	inumber1 = Long.parseLong(number.trim());
	                   
	                } catch (NumberFormatException e) {
	                    System.err.println("Неверный формат строки!");
	                }                  
	                System.out.println(inumber1);
	            }
	            /*********************************КОНЕЦ*********************************/ 
	            
	            /*********************************РАСПОЗНОВАНИЕ informationAboutFile**********************/
	  public static void parsinginformationAboutFile(String fileName) throws UnsupportedEncodingException{
	            	 
	                    Pattern pattern = Pattern.compile("\\s{6}[0-9а-яА-ЯёЁ,.()\\s]*+"); 
	                    Matcher matcher = pattern.matcher(fileName); 
	                    while (matcher.find()) 
	                    informationAboutFile = matcher.group();
	                    informationAboutFile.trim();
	                    System.out.println(informationAboutFile.trim());
	            }
	  /*********************************КОНЕЦ*********************************/ 
}

