import java.net.*;
import java.io.*;
import java.util.Scanner;  

public class client {

	private static String serverIpAddr;
	private static int _port = 8888;
	private static int _portClient = 4444;
	private static String clientAddr;

	public static void main(String[] args){
		try {
	        Scanner input = new Scanner(System.in);	
	        System.out.print("\nServer's IP Address: ");
	        serverIpAddr = input.nextLine();
	        InetAddress serverAddr = InetAddress.getByName(serverIpAddr);
	
	    	Socket socket = new Socket(serverAddr,_port);
        
       
	       	DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        
	       	DataInputStream dis = new DataInputStream(socket.getInputStream());
		
			System.out.print("\nConnect succesfull!");

			int flag = dis.readInt();

			System.out.println("\nMy flag: "+flag);

			String fileName = dis.readUTF();

			if(flag==1){
				long fileSize = dis.readLong();
				System.out.println("We will receive " +fileName+ " direct");
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

				// Listen port 4444.
				ServerSocket serverSocket = new ServerSocket(_portClient);
				System.out.print("\nClient is listening at port 8888.");

	        	while(true) {
	        		Socket conClient = serverSocket.accept(); 
	        		Thread multiClient = new MultiClient(conClient, fileName);
	        		multiClient.start();
	        	}

			} else {
				System.out.println("\nWe will receive from other client.");

				// Connect to client at port 4444

				Socket socketClient = new Socket(clientAddr,_portClient);
				System.out.println("\nConnect to client succesfull");

				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        		DataInputStream in = new DataInputStream(socket.getInputStream());

        		long fileSizeReceive = in.readLong();

        		System.out.println("\nSize of file: " +fileSizeReceive+ " bytes");
				FileOutputStream fOutput = new FileOutputStream(fileName);
				byte[] buffs = new byte[1024];
				long sizeReceive = 0;
				while (sizeReceive < fileSizeReceive) {
					int counter;
					counter = in.read(buffs);
					fOutput.write(buffs, 0, counter);
					sizeReceive += counter;	
				}
				System.out.println("Received file successfully!");
				fOutput.close();
				in.close();
				out.close();
				socketClient.close();

			}

		} catch(Exception e) {
		    e.printStackTrace();
		}

	}
}

class MultiClient extends Thread {
	private Socket socketThread;
	String fileNameSent;


	public MultiClient (Socket _socket, String _fileNameSent) {
		this.socketThread = _socket;
		this.fileNameSent = _fileNameSent;
	}

	public void run() {
		DataOutputStream out = null;
		DataInputStream in = null;

		try {
			out = new DataOutputStream(socketThread.getOutputStream());
			in = new DataInputStream(socketThread.getInputStream());

			File fileSent = new File(fileNameSent);

			long fileSizeSent = fileSent.length();
			out.writeLong(fileSizeSent);
			out.flush();

			FileInputStream fInput =  new FileInputStream(fileSent);
			byte[] buffs = new byte[1024];

			int counter;
			while ((counter = fInput.read(buffs)) != -1) {
				out.write(buffs, 0, counter);
				out.flush();
			}
			fInput.close();
			System.out.println("\nSent file successfully!");

		//	out.close();
		//	in.close();
		//	socketThread.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
	        	out.close();
	        	socketThread.close();
			} catch (IOException e) {
				e.printStackTrace();

			}
		}
	}

}