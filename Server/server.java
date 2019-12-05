import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.*;

public class server {
	static Vector<RequestHandler> vectorThread = new Vector<>();
	static Vector<String> vectorAddr = new Vector<>();
	public static void main(String[] args) {
		try {
			int countClient = 0;
			int _port = 8888;
			ServerSocket serverSocket = new ServerSocket(_port);
			System.out.print("\nServer is listening at port "+_port);
			while (true) {
				Socket socket = serverSocket.accept();
				InetAddress clientIp = socket.getInetAddress();
				int clientPort = socket.getPort();
				countClient++;
				RequestHandler requestHandler = new RequestHandler(socket, countClient);
				System.out.println("\nClient address: " + clientIp.toString().substring(1) + ":" + clientPort + " is connecting");
				vectorThread.add(requestHandler);
				vectorAddr.add(clientIp.toString());
				requestHandler.start();

				if(countClient == 3){
					countClient = 0;
				}
			}
		}
		catch (Exception e) {
		    	e.printStackTrace();
		}
	}

}

class RequestHandler extends Thread {

	private Socket socket;
	private static int countClient;
	private InputStream is;
	private DataInputStream dis;
	private OutputStream os;
	private DataOutputStream dos;
	private int buffer = 1024;


	public RequestHandler(Socket _socket, int _countClient){
		this.socket = _socket;
		this.countClient = _countClient;
	}

	public void run() {
	//	while(true) {
			try {

				Scanner input = new Scanner(System.in);
				String fileName;
				File file;

				System.out.println("\nAll client:"+countClient);

				dis =  new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream( socket.getOutputStream());

				if(countClient == 3) {
					// flag1: client receive direct, flag2: client receive from other client.
					int flag1 = 1;
					int flag2 = 2;

					RequestHandler client1 = server.vectorThread.get(0);
					RequestHandler client2 = server.vectorThread.get(1);
					RequestHandler client3 = server.vectorThread.get(2);
					// send flag to client
					client1.dos.writeInt(flag1);
					client1.dos.flush();

					client2.dos.writeInt(flag2);
					client2.dos.flush();

					client3.dos.writeInt(flag2);
					client3.dos.flush();
					System.out.println("Sent flag to all client!");

					// enter file until file is exists
					while(true) {
						System.out.print("\nEnter file's name to send to client: ");
						fileName = input.nextLine();
						file = new File(fileName);

						if(file.exists()){
							break;
						}
						else {
							System.out.print("\nFile is not found!");
						}
					}
					// send file's name to all client
					client1.dos.writeUTF(fileName);
					client1.dos.flush();

					client2.dos.writeUTF(fileName);
					client2.dos.flush();

					client3.dos.writeUTF(fileName);
					client3.dos.flush();
					System.out.println("\nsent file's name to all client.");
					System.out.println("\nStart sending file directly!");
					//send file size
					long fileSize = file.length();
					client1.dos.writeLong(fileSize);
					client1.dos.flush();
					//send file
					FileInputStream fileInput =  new FileInputStream(file);
					byte[] buff = new byte[buffer];
					int count;
					while ((count = fileInput.read(buff)) != -1) {
						client1.dos.write(buff, 0, count);
					}
					client1.dos.flush();
					fileInput.close();
					// receive comfirm from client 1
					int comfirm = client1.dis.readInt();
					System.out.println("\nflag confirm: "+comfirm);
					System.out.println("\nSent file successfully!");	
					// after comfirm, send ip address of client to other client.
					if(comfirm==1) {
						String addClient = server.vectorAddr.get(0);
						addClient = addClient.toString().substring(1);
						System.out.println("\naddress sent to client: "+addClient);
						client2.dos.writeUTF(addClient);
						client2.dos.flush();
						System.out.println("\nSent ip successfull.");

						client3.dos.writeUTF(addClient);
						client3.dos.flush();
						System.out.println("\nSent ip successfull.");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		//}	
	}
}
