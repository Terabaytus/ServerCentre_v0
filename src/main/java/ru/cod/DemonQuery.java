package ru.cod;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class DemonQuery {

    @SuppressWarnings("rawtypes")
    static ArrayList clientList = new ArrayList();

    
    public DemonQuery(String URL, String USER, String PASSWORD, String URLGPS, String USERGPS, String PASSWORDGPS, String nuberPort, String pathToFilesFotoVideo, String pathToUpdatings){
    	try {
			connect(URL, USER, PASSWORD, URLGPS, USERGPS, PASSWORDGPS, nuberPort, pathToFilesFotoVideo, pathToUpdatings);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
   

    @SuppressWarnings("unchecked")
    public static void connect(String URL, String USER, String PASSWORD, String URLGPS, String USERGPS, String PASSWORDGPS, String nuberPort, String pathToFilesFotoVideo, String pathToUpdatings) throws IOException {

        int port = Integer.parseInt(nuberPort);/*60000*/
        int connection = 1000;

        @SuppressWarnings("resource")
        ServerSocket ss = new ServerSocket(port, connection);
        System.out.println("Server wait connect..." + "\n");

        while (true) {
            Socket socket = ss.accept();// ������������ ���� � �������� ����������� ������� accept()
            System.out.println("Client connected");
            TheQueryProcessor theQueryProcessor = new TheQueryProcessor(URL, USER, PASSWORD, URLGPS, USERGPS, PASSWORDGPS, pathToFilesFotoVideo, pathToUpdatings, socket);//������ ������ �� ����� 
            clientList.add(theQueryProcessor);//��������� ������ � ������
            Thread stream = new Thread(theQueryProcessor);//������ �� ����� steram
            stream.start();//��������� �����
            
        }
    }
           public void ShutdownClient(TheQueryProcessor theQueryProcessor){//�������� ����������� �� ������
	        clientList.remove(theQueryProcessor);
	    }/**/
}	
