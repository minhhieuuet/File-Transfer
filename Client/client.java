import java.net.*;
import java.io.*;
import java.util.Scanner;  

public class client {
	private static String serverIpAddr;

	public static void main(String[] args){
		try {
	        Scanner input = new Scanner(System.in);	
	        System.out.print("\nServer's IP Address: ");
	        serverIpAddr = input.nextLine();
	        InetAddress serverAddr = InetAddress.getByName(serverIpAddr);
	
	    	Socket socket = new Socket(serverAddr,8888);
        
       
	       	DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        
	       	DataInputStream dis = new DataInputStream(socket.getInputStream());
		
			System.out.print("\nConnect succesfull!");

			int flag = dis.readInt();

			System.out.println("\nMy flag: "+flag);

			String fileName = dis.readUTF();

			if(flag==1){
				long fileSize = dis.readLong();
				System.out.println("We will receive  " +fileName+ " direct");
				System.out.println("\nSize of file: " +fileSize+ " bytes");
				FileOutputStream fileOutput = new FileOutputStream(fileName);
				byte[] buff = new byte[1024];
				long size = 0;
				while (size < fileSize) {
					int count;
					count = dis.read(buff);
					fileOutput.write(buff, 0, count);
					size += count;
				}
				System.out.println("Received file successfully!");
				fileOutput.close();				
			} else {
				System.out.println("\nWe will receive from other client.");
			}
		} catch(Exception e) {
		    e.printStackTrace();
		}

	}
}