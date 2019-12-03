import java.net.*;
import java.io.*;
import java.util.Scanner;  

public class client {
	private static String serverIpAddr;

	public static void main(String[] args){
		Socket socket = null;
		try {
	        Scanner input = new Scanner(System.in);	
	        System.out.print("Server's IP Address: ");
	        serverIpAddr = input.nextLine();
	        InetAddress serverAddr = InetAddress.getByName(serverIpAddr);
	
	    	socket = new Socket(serverAddr,8888);
        
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
       		
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		
			System.out.print("\nConnect succesfull!");

			String fileName = dis.readUTF();

			long fileSize = dis.readLong();
			System.out.println("We will receive  " +fileName+ " direct");
			System.out.println("\nSize of file: " +fileSize+ " bytes");
			FileOutputStream fileOutput = new FileOutputStream(fileName);
			byte[] buff = new byte[1024];
			long size = 0;
			while (size < fileSize) {
				int count;
				count = is.read(buff);
				fileOutput.write(buff, 0, count);
				size += count;
			}
			System.out.println("Received file successfully!");
			fileOutput.close();
			socket.close();				
			 
		} catch(Exception e) {
		    e.printStackTrace();
		}
		finally{
			//socket.close();
		}

	}
}
