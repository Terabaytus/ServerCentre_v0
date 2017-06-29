package ru.cod;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainServer {
	 
	public static final String PATH_TO_PROPERTIES = "serverConfig.properties";
		
	@SuppressWarnings("unused")
	public static void main(String[] args){
		// TODO Auto-generated method stub
		
		InputStream fileInputStream;
        //�������������� ����������� ������ Properties
        //���� Hashtable ��� ������� ������ � �������
        Properties prop = new Properties();
 
        try {
            //���������� � ����� � �������� ������
            fileInputStream = ClassLoader.getSystemResourceAsStream(PATH_TO_PROPERTIES);
            prop.load(fileInputStream);
 
            String URL 					= prop.getProperty("URL");
            String USER 				= prop.getProperty("USER");
            String PASSWORD 			= prop.getProperty("PASSWORD");
            
            String URLGPS 				= prop.getProperty("URLGPS");
            String USERGPS 				= prop.getProperty("USERGPS");
            String PASSWORDGPS 			= prop.getProperty("PASSWORDGPS");
 
            String nuberPort 			= prop.getProperty("nuberPort");
            String pathToFilesFotoVideo = prop.getProperty("pathToFilesFotoVideo");
            String pathToUpdatings      = prop.getProperty("pathToUpdatings");
            
            DemonQuery ds = new DemonQuery (URL, USER, PASSWORD, URLGPS, USERGPS, PASSWORDGPS, nuberPort, pathToFilesFotoVideo, pathToUpdatings);
        } catch (IOException e) {
            System.out.println("������ � ���������: ���� " + PATH_TO_PROPERTIES + " �� ����������");
            e.printStackTrace();
        }
		
		
  }
}
