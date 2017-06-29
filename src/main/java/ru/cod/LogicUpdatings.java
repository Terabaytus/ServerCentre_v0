package ru.cod;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/*********************************������ �������� ����������**********************/
public class LogicUpdatings  {
	
	static String pathToFileName;
	static String path;
    
    public void conn(String pathToUpdatings, double versioNumber, Socket socket) {
    	
    	//---------------������� ����� ��� ��������-----------------------
        File directory = new File(pathToUpdatings);
        if (!directory.exists())
       	 directory.mkdirs();
        //---------------�����-----------------------
       
    	//---------------���� ����� ���� � ����� ��� ��������----------------------
    	File folder = new File(pathToUpdatings);
		 File[] listOfFiles = folder.listFiles();
		    String name = null;
			for (File file : listOfFiles){
				pathToFileName = file.getPath();
				name =file.getName();
		    	}
				if(name == null ){
					System.out.println("���������� � ������� ��������� �����!" );
				
			}else{
			String nameVersion = name.substring(0, name.length()-4);//������������ ��������� ��� ����� ��������� ������������� � �����
		    	 double versionApk = Double.parseDouble(nameVersion);	
		    	 //---------------�����-----------------------
		    	 
		    	 if(versioNumber < versionApk){//���������� ����� � �������� ����� � ����� � ������� ��������� �� �������
        	
        int pong = 0;
  	    
 	     System.out.println("���������� ����, ����� " + pong + " ������� �������.");
       
        DataOutputStream outD;// ���������� ������ ������������ ������
       try {   
        outD = new DataOutputStream(socket.getOutputStream());//������������ � socket   
        outD.writeInt(pong);//������������ ����� ������� �������
        outD.flush();
	 
		
			downloadFiles(socket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
           } 
		}
    }
    
    /*********************************�������� �����**********************/
    public static void downloadFiles(Socket socket) throws IOException{
    	
    	try {    
       	 
    		DataOutputStream outD = new DataOutputStream(socket.getOutputStream());
        
            File file = new File(pathToFileName);//����� ���������� ����� ����������
            System.out.println(pathToFileName);
            outD.writeLong(file.length());//�������� ������ �����
            outD.writeUTF(file.getName());//�������� ��� �����
           
         FileInputStream in = new FileInputStream(file);
         byte[] buffer = new byte[64 * 1024];// ������ ������
         int count;//����������� ����������� ����

         while ((count = in.read(buffer)) != -1) {
             outD.write(buffer, 0, count);

         }
         
         System.out.println("��������� ���� ������� "+ count);
         
         path = null;//�������� ��������� ������ ����� ������ ��������
         outD.flush();
         in.close();
         
      }catch(Exception e){
          e.printStackTrace();
          socket.close();
         }
      }      
   /*********************************�����*********************************/   
   
 }
/*********************************������ �������� ���������� �����*********************************/   
