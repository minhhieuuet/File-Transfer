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
			System.out.println ("Server is listening at port 8888.");
			while (true) {
				Socket socket = serverSocket.accept();

				//DataInputStream dis = new DataInputStream(socket.getInputStream());

				//DataOutputStream dos = new DataOutputStream(socket.getOutputStream()); 

				InetAddress clientIp = socket.getInetAddress();
				int clientPort = socket.getPort();

				// only the first client connect have flag = 1 

				countClient += 1;
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

	public RequestHandler(Socket _socket, int _countClient){//,DataInputStream _dis, DataOutputStream _dos) {			
		//this.dis = _dis;
		//this.dos = _dos;
		this.socket = _socket;
		this.countClient = _countClient;
	}

	public void run() {
		try {
			
			Scanner input = new Scanner(System.in);
			
			String fileName;

			File file;
			String msg = "You will recevied file from server!";
			
			System.out.println("\nAll client:"+countClient);
		
			dis =  new DataInputStream(socket.getInputStream());
			
 			dos = new DataOutputStream( socket.getOutputStream());
			
			if(countClient == 3) {

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
				RequestHandler client1 = server.vectorThread.get(0);
				client1.dos.writeUTF(fileName);
				client1.dos.flush();

				System.out.println("\nSend direct!");
				long fileSize = file.length();
				client1.dos.writeLong(fileSize);
				client1.dos.flush();
				
				FileInputStream fileInput =  new FileInputStream(file);
				byte[] buff = new byte[buffer];

				int count;	
				while ((count = fileInput.read(buff)) != -1) {
					client1.dos.write(buff, 0, count);
				}
				client1.os.flush();
				fileInput.close();
				System.out.println("\nSent file successfully!");


				//} else {
				//	 do somethings
				//	System.out.println("\nDon't send direct!");
				//}

			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
}
