package ru.cod;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/*********************************ЛОГИКА ЗАГРУЗКИ ОБНОВЛЕНИЙ**********************/
public class LogicUpdatings  {
	
	static String pathToFileName;
	static String path;
    
    public void conn(String pathToUpdatings, double versioNumber, Socket socket) {
    	
    	//---------------СОЗДАЁМ ПАПКУ ДЛЯ ЗАГРУЗОК-----------------------
        File directory = new File(pathToUpdatings);
        if (!directory.exists())
       	 directory.mkdirs();
        //---------------КОНЕЦ-----------------------
       
    	//---------------ИЩЕМ НОВЫЙ ФАЙЛ В ПАПКЕ ДЛЯ ЗАГРУЗОК----------------------
    	File folder = new File(pathToUpdatings);
		 File[] listOfFiles = folder.listFiles();
		    String name = null;
			for (File file : listOfFiles){
				pathToFileName = file.getPath();
				name =file.getName();
		    	}
				if(name == null ){
					System.out.println("Дериктория с файлами обновлеий пуста!" );
				
			}else{
			String nameVersion = name.substring(0, name.length()-4);//корекктируем полученое имя файла оновления содержащегося в папке
		    	 double versionApk = Double.parseDouble(nameVersion);	
		    	 //---------------КОНЕЦ-----------------------
		    	 
		    	 if(versioNumber < versionApk){//сравниваем номер в название файла в папке с номером пришедшим от клиента
        	
        int pong = 0;
  	    
 	     System.out.println("Обновления есть, ответ " + pong + " отослан клиенту.");
       
        DataOutputStream outD;// переменная потока отправляемых данных
       try {   
        outD = new DataOutputStream(socket.getOutputStream());//подключаемся к socket   
        outD.writeInt(pong);//отправляется ответ сервера клиенту
        outD.flush();
	 
		
			downloadFiles(socket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
           } 
		}
    }
    
    /*********************************ОТГРУЗКА ФАЙЛА**********************/
    public static void downloadFiles(Socket socket) throws IOException{
    	
    	try {    
       	 
    		DataOutputStream outD = new DataOutputStream(socket.getOutputStream());
        
            File file = new File(pathToFileName);//место нахождения файла обновления
            System.out.println(pathToFileName);
            outD.writeLong(file.length());//отсылаем размер файла
            outD.writeUTF(file.getName());//отсылаем имя файла
           
         FileInputStream in = new FileInputStream(file);
         byte[] buffer = new byte[64 * 1024];// размер буфера
         int count;//колличество отправленых байт

         while ((count = in.read(buffer)) != -1) {
             outD.write(buffer, 0, count);

         }
         
         System.out.println("Последний байт отослан "+ count);
         
         path = null;//обнуляем перменную адресс после каждой отправки
         outD.flush();
         in.close();
         
      }catch(Exception e){
          e.printStackTrace();
          socket.close();
         }
      }      
   /*********************************КОНЕЦ*********************************/   
   
 }
/*********************************ЛОГИКА ЗАГРУЗКИ ОБНОВЛЕНИЙ КОНЕЦ*********************************/   
