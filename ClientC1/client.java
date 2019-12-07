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
	        System.out.print("Server's IP Address: ");
	        serverIpAddr = input.nextLine();
	        InetAddress serverAddr = InetAddress.getByName(serverIpAddr);
	        //create socket connect
			Socket socket = new Socket(serverAddr,_port);
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			System.out.println("\nConnect succesfull!");
			// receive flag
			int flag = dis.readInt();
			System.out.println("\nMy flag: "+flag);

			while(true) {
				// receive file name
				String fileName = dis.readUTF();
			
				if(fileName.equals("@exit")){
					System.out.println("Server completely sent the file!");
					break;
				} else {
					// if receive flag == 1, client start working
					if(flag==1){
						System.out.println("\nFile'name will receive: "+fileName);
						// receive file size
						long fileSize = dis.readLong();
						System.out.println("We will receive " +fileName+ " direct");
						System.out.println("\nSize of file: " +fileSize+ " bytes");
						// receive file
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
						System.out.print("\nClient is listening at port "+_portClient);
						// send comfirm: open port to listen other client
						dos.writeInt(flag);
						dos.flush();
						// connect other client
						int coutClientThread = 0;
			        	while(true) {
	        				Socket conClient = serverSocket.accept();
	        				coutClientThread++;
	       			 		Thread multiClient = new MultiClient(conClient, fileName);
	       			 		multiClient.start();
	        				System.out.println("\nClient is connecting part: "+coutClientThread);
	        				if (coutClientThread==2) {
	      				  		//	System.out.println("\nClose while");
	       			 			break;
	      			  		}
	      			  	}
	        			serverSocket.close();
	   			     // client receive from other client
					} else {
						System.out.println("\nFile'name will receive: "+fileName);
						System.out.println("\nWe will receive from other client.");
						// receive ip address
						clientAddr = dis.readUTF();
						System.out.println("\nWe will connect to clinet: "+clientAddr);
						// connect to client
						Socket socketClient = new Socket(clientAddr,_portClient);
						System.out.println("\nConnect to client succesfull");
						DataOutputStream outThr = new DataOutputStream(socketClient.getOutputStream());
    		    		DataInputStream inThr = new DataInputStream(socketClient.getInputStream());
      			  		// receive file size
      			  		long fileSizeReceive = inThr.readLong();
     			   		System.out.println("\nSize of file: " +fileSizeReceive+ " bytes");
       			 		// receive file
						FileOutputStream fiOutput = new FileOutputStream(fileName);
						byte[] buffReceive = new byte[1024];
						long sizeReceive = 0;
						while (sizeReceive < fileSizeReceive) {
							int counterReceive;
							counterReceive = inThr.read(buffReceive);
							fiOutput.write(buffReceive, 0, counterReceive);
							sizeReceive += counterReceive;
						}
						System.out.println("\nReceived file successfully!");
						// send flag comfirm
						dos.writeInt(flag);
						dos.flush();
						System.out.println("\nSent flag comfirm.");
						//
						fiOutput.close();
						inThr.close();
						outThr.close();
						socketClient.close();

					}
				}
			}
			System.out.println("Close connect!");
			dis.close();
			dos.close();
			socket.close();
		} catch(Exception e) {
		    e.printStackTrace();
		}
		finally {
			//dis.close();
			//dos.close();
			//socket.close();
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
			//send file size
			out.writeLong(fileSizeSent);
			out.flush();
			System.out.println("\nFile size will sent: "+fileSizeSent);
			// send file
			FileInputStream fInput =  new FileInputStream(fileSent);
			byte[] buffs = new byte[1024];
			int counter;
			while ((counter = fInput.read(buffs)) != -1) {
				out.write(buffs, 0, counter);
				out.flush();
			}
			fInput.close();
			System.out.println("\nSent file successfully!");
			
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