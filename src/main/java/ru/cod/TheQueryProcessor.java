package ru.cod;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class TheQueryProcessor implements Runnable {
	
	private Socket socket;
	private String URL;
	private String USER;
	private String PASSWORD;
	private String URLGPS;
	private String USERGPS;
	private String PASSWORDGPS;
	private String pathToFilesFotoVideo;
	private String pathToUpdatings;
	LogicFotoVideo logicServer = new LogicFotoVideo();
	
	public TheQueryProcessor(String URL, String USER, String PASSWORD, String URLGPS, String USERGPS, String PASSWORDGPS, String pathToFilesFotoVideo, String pathToUpdatings, Socket socket) {
		// TODO Auto-generated constructor stub
					 this.socket=socket;
					 this.URL=URL;
					 this.USER=USER;
					 this.PASSWORD=PASSWORD;
					 this.URLGPS=URLGPS;
					 this.USERGPS=USERGPS;
					 this.PASSWORDGPS=PASSWORDGPS;
					 this.pathToUpdatings=pathToUpdatings;
					 this.pathToFilesFotoVideo=pathToFilesFotoVideo;
	}
	
   // --------ПРИЁМ ДАННЫХ--------
	@SuppressWarnings("unused")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
	           InputStream in = socket.getInputStream();
	           DataInputStream din = new DataInputStream(in);
	          
	           int ping = din.readInt();//приём запроса
	           
	           if (ping == 1){
	        	   int pong = 1;
	               DataOutputStream outD;// переменная потока отправляемых данных
	               outD = new DataOutputStream(socket.getOutputStream());//подключаемся к socket   
	               outD.writeInt(pong);//отправляется ответ сервера клиенту
	               outD.flush();
	           } 
	            
	           int namberQuery= din.readInt();
	          
	          if(namberQuery == 8418){
	        	  System.out.println("Пришёл запрос на обновление.");
	        	  LogicUpdatings updatings = new LogicUpdatings();
	        	  double versioNumber = din.readDouble(); //приём номера версии
	        	  updatings.conn(pathToUpdatings, versioNumber, socket);
	        	 }
	          
	          if(namberQuery == 8814){
	         System.out.println("Пришли координаты GPS.");  
	           String id_telephone = din.readUTF(); //приём номера устройства
	           String latitude     = din.readUTF(); //приём широты
	           String longitude    = din.readUTF(); //приём долготы
	           String timemodify   = din.readUTF(); //приём времени 
	           String speed        = din.readUTF(); //приём скорости
	           
	        LogicGPS logicGPS = new LogicGPS(URLGPS, USERGPS, PASSWORDGPS, id_telephone,latitude,longitude,timemodify,speed);
	          }
	           
	          if(namberQuery == 8184){
	        	  System.out.println("Пришли фото видео файлы.");
	        	  LogicFotoVideo logicFotoVideo = new LogicFotoVideo();
	        	  int filesCount = din.readInt();//получаем количество файлов  
	        	 
	        	  System.out.println("Передается " + filesCount + " файлов\n");
	              
	              for(int i = 0; i<filesCount; i++){
	              	System.out.println("Принят " + (i+1) + " файл: \n");
	                  
	                  long fileSize = din.readLong(); // получаем размер файла
	                              
	                  String fileName = din.readUTF(); //приём имени файла
	                  logicFotoVideo.logicFiles(URL, USER, PASSWORD, pathToFilesFotoVideo, filesCount, fileSize, fileName, din, socket);
	              }
	          
	          }
	           
	          
		}catch(IOException e){
			e.printStackTrace();
		  }finally {
			  try {
			    	socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		  }
	   }
	}

