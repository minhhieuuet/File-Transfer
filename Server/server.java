import java.net.*;
import java.io.*;
import java.util.Scanner;

public class server {

	public static void main(String[] args) {
		try {
			int _port = 8888;

			int flag = 0;

			int countClient = 0;

			ServerSocket serverSocket = new ServerSocket(_port);
			System.out.print("\nServer is listening at port 8888.");
			while (true) {
				Socket socket = serverSocket.accept();

				DataInputStream dis = new DataInputStream(socket.getInputStream());

				DataOutputStream dos = new DataOutputStream(socket.getOutputStream()); 

				InetAddress clientIp = socket.getInetAddress();
				int clientPort = socket.getPort();

				// only the first client connect have flag = 1 
				flag += 1;

				countClient += 1;
				System.out.println("\nClient address: " + clientIp.toString().substring(1) + ":" + clientPort + " is connecting");

				Thread requestHandler = new RequestHandler(socket, flag, countClient, dis, dos);
	        	requestHandler.start();


		}
	} catch (Exception e) {
		    		e.printStackTrace();
		}
	}

}

class RequestHandler extends Thread {
	
	private static Socket socket;

	private static int flag;

	private static int countClient;

	private static DataInputStream dis;

	private static DataOutputStream dos;

	public RequestHandler(Socket _socket, int _flag, int _countClient,DataInputStream _dis, DataOutputStream _dos) {			this.socket = _socket;
		this.flag = _flag;
		this.dis = _dis;
		this.dos = _dos;
		this.socket = _socket;
		this.countClient = _countClient;
	}
	public void run() {
		try {
			
			Scanner input = new Scanner(System.in);
			
			String fileName;

			File file;

			System.out.println("flag of client: "+flag);

			String msg = "You will recevied file from server!";
			
			dos.writeInt(flag);
			dos.flush();
			System.out.println("\nAll client:"+countClient);
			if(countClient == 2) {

				// enter file until file is exists
				while(true) {
				System.out.print("\nEnter file's name to send to client: ");
				fileName = input.nextLine();
				file = new File(fileName);

				if(file.exists()){
					break;
				} else {
					System.out.print("\nFile is not found!");
				}

				}

				dos.writeUTF(fileName);
				dos.flush();

				if(flag == 1) {
					System.out.println("/nSend direct!");

					long fileSize = file.length();
					dos.writeLong(fileSize);
					dos.flush();
					
					FileInputStream fileInput =  new FileInputStream(file);
					byte[] buff = new byte[1024];

					int count;	
					while ((count = fileInput.read(buff)) != -1) {
						dos.write(buff, 0, count);
						dos.flush();
					}
					fileInput.close();
					System.out.println("\nSent file successfully!");


				} else {
				// do somethings
					System.out.println("\nDon't send direct!");
				}

			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
}