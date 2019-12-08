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
			System.out.println("\nServer is listening at port "+_port);
			while (true) {
				Socket socket = serverSocket.accept();
				InetAddress clientIp = socket.getInetAddress();
				int clientPort = socket.getPort();
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				countClient++;
				RequestHandler requestHandler = new RequestHandler(socket, countClient, dis, dos);
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
	private static int countComfirm = 0;
	public static long start;
	public static long end;

	public RequestHandler(Socket _socket, int _countClient, DataInputStream _dis, DataOutputStream _dos){
		this.socket = _socket;
		this.countClient = _countClient;
		this.dis = _dis;
		this.dos = _dos;
	}

	public void run() {
			try {

				Scanner input = new Scanner(System.in);
				String fileName;
				File file;

				System.out.println("All client:"+countClient);
			//	dis =  new DataInputStream(socket.getInputStream());
			//	dos = new DataOutputStream( socket.getOutputStream());
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
					System.out.println("\nSent flag to all client!");
					// Can send many file
					while(true) {
						// enter file until file is exists
						while(true) {
							System.out.print("\nEnter file's name to send to client: ");
							fileName = input.nextLine();
							start = System.currentTimeMillis();
							System.out.println("Start time: " + start);
							file = new File(fileName);

							if(file.exists() || fileName.equals("@exit")){
								break;
							}
							else {
								System.out.print("\nFile is not found!");
							}
						}

						if(fileName.equals("@exit"))
						{
							// sent @exit to client know and close connect
							client1.dos.writeUTF(fileName);
							client1.dos.flush();

							client2.dos.writeUTF(fileName);
							client2.dos.flush();

							client3.dos.writeUTF(fileName);
							client3.dos.flush();
							break;
						} else {
							// send file's name to all client
							client1.dos.writeUTF(fileName);
							client1.dos.flush();

							client2.dos.writeUTF(fileName);
							client2.dos.flush();

							client3.dos.writeUTF(fileName);
							client3.dos.flush();
							System.out.println("Sent file's name to all client.");
							System.out.println("\nStart sending file directly...");
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
							int comfirm1 = 0;
							int comfirm2 = 0;
							int comfirm3 = 0;

							comfirm1 = client1.dis.readInt();
						//	System.out.println("\nFlag confirm: "+comfirm1);
						//	System.out.println("\nSent file successfully!");
							// after comfirm, send ip address of client to other client.
							if(comfirm1==1) {
								System.out.println("Send file for 1 client successfully");
								increaseCountComfirm();
								//send ip address for other client
								String addClient = server.vectorAddr.get(0);
								addClient = addClient.toString().substring(1);
								System.out.println("\nAddress sent to client: "+addClient);
								client2.dos.writeUTF(addClient);
								client2.dos.flush();
								client3.dos.writeUTF(addClient);
								client3.dos.flush();
								System.out.println("\nSent IP address successfull.");
								// receive flag comfirm of 2 client.
								comfirm2 = client2.dis.readInt();
								comfirm3 = client3.dis.readInt();

								if(comfirm2 != 0 && comfirm3 != 0) {
									increaseCountComfirm();
									increaseCountComfirm();
									System.out.println("\nTatol client comfirm: " +countComfirm );
									end = System.currentTimeMillis();
									System.out.println("Send file,Done!");
									System.out.println("End time: " + end);
									System.out.println("Execution time is " + (end - start) +" miliseconds");
								}
							}
						}
						while(true) {
							if (countComfirm == 3) {
								countComfirm = 0;
								break;
							}
						}
					}
					server.vectorThread.clear();
                    server.vectorAddr.clear();
                    System.out.println("Finish send file!\nWaiting connect...");
                    client1.dis.close();
					client1.dos.close();
					client2.dis.close();
					client2.dos.close();
					client3.dis.close();
					client3.dos.close();
					socket.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		//}
	}
	private synchronized void increaseCountComfirm() {
        countComfirm++;
    }
}
