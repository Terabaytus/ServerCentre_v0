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
	
   // --------��Ȩ� ������--------
	@SuppressWarnings("unused")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
	           InputStream in = socket.getInputStream();
	           DataInputStream din = new DataInputStream(in);
	          
	           int ping = din.readInt();//���� �������
	           
	           if (ping == 1){
	        	   int pong = 1;
	               DataOutputStream outD;// ���������� ������ ������������ ������
	               outD = new DataOutputStream(socket.getOutputStream());//������������ � socket   
	               outD.writeInt(pong);//������������ ����� ������� �������
	               outD.flush();
	           } 
	            
	           int namberQuery= din.readInt();
	          
	          if(namberQuery == 8418){
	        	  System.out.println("������ ������ �� ����������.");
	        	  LogicUpdatings updatings = new LogicUpdatings();
	        	  double versioNumber = din.readDouble(); //���� ������ ������
	        	  updatings.conn(pathToUpdatings, versioNumber, socket);
	        	 }
	          
	          if(namberQuery == 8814){
	         System.out.println("������ ���������� GPS.");  
	           String id_telephone = din.readUTF(); //���� ������ ����������
	           String latitude     = din.readUTF(); //���� ������
	           String longitude    = din.readUTF(); //���� �������
	           String timemodify   = din.readUTF(); //���� ������� 
	           String speed        = din.readUTF(); //���� ��������
	           
	        LogicGPS logicGPS = new LogicGPS(URLGPS, USERGPS, PASSWORDGPS, id_telephone,latitude,longitude,timemodify,speed);
	          }
	           
	          if(namberQuery == 8184){
	        	  System.out.println("������ ���� ����� �����.");
	        	  LogicFotoVideo logicFotoVideo = new LogicFotoVideo();
	        	  int filesCount = din.readInt();//�������� ���������� ������  
	        	 
	        	  System.out.println("���������� " + filesCount + " ������\n");
	              
	              for(int i = 0; i<filesCount; i++){
	              	System.out.println("������ " + (i+1) + " ����: \n");
	                  
	                  long fileSize = din.readLong(); // �������� ������ �����
	                              
	                  String fileName = din.readUTF(); //���� ����� �����
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

