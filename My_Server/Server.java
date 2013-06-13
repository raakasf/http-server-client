import java.io.*;
import java.net.*;
import java.net.ServerSocket;

class Server
{
	public static void main(String[] args)
	{
		int clientCount = 0;
        
        ServerSocket serverSocket = null;
        Socket socketForClient = null;

        try
        {
            //If the user did not enter any port number for server to start on. Throw Exception
            if(args.length == 0)
            {
                throw new IllegalArgumentException("Port number not entered");
            }
            else
			{	
				int serverPortNumber = Integer.parseInt(args[0]);		
				serverSocket = new ServerSocket(serverPortNumber);
                System.out.println("Server running successfully on port#" + serverPortNumber);
				System.out.println("Waiting for clients to connect…..\n");	

				while(true)
				{
					socketForClient = serverSocket.accept();
					clientCount++;
					System.out.println("Accepted connection from client#" + clientCount);

					BufferedReader in = new BufferedReader(new InputStreamReader(socketForClient.getInputStream()));
                    String request = in.readLine();
                    String[] parts = request.split(" ");
						
					System.out.println("\tClient Request: " + request);
					String response = null;
					PrintWriter out = new PrintWriter(socketForClient.getOutputStream(), true);

					if( parts.length == 3 && parts[0].equalsIgnoreCase("GET") )
					{

						System.out.println("\tUser request is: " + request);

						try 
						{
                            System.out.println("\tTrying to read file");
						    StringBuffer sb = new StringBuffer(parts[1]); 
						    String filename = parts[1].substring(1, parts[1].length());
						    long length = new File(filename).length();
					     		
						    try
                            {
                                //Read file from the local disk
                                FileReader fileReader = new FileReader(filename);
    							BufferedReader reader = new BufferedReader(fileReader);
                                System.out.println("\tFile read successfully. Sending response to client: HTTP/1.0 200 OK");
                                out.println("HTTP/1.0 200 OK");
                                out.println("Content-Length: " + length);

    							String line = null;
    							while ((line = reader.readLine()) != null) 
                                {
                                    out.println(line);
    							}
                                reader.close();
						    } 
						    catch (FileNotFoundException ioex) 
						    {
    							System.err.println("\tFile does not exists. Exception: " + ioex);
						    	response = "HTTP/1.0 404 Not Found";														    	out.println(response);
						    }
					     }
					     catch (IOException fnfx) 
					     {
                             System.err.println("\tException while reading file: " + fnfx);
					     }	
					}
					else
					{
						response = "HTTP/1.0 400 Bad Request";
                        System.out.println("Bad request from user. Sending response: " + response);
						out.println(response);
					}

				out.close();
				in.close();
				socketForClient.close();
				}
			}

		}
		catch(IllegalArgumentException iae)
		{
			System.out.println("Please input <port number>");
		}
		catch(IOException ioe)
		{
			System.out.println("Could not start the server on the given port: " + args[0]);
		}
		catch(Exception e)
		{
			System.out.println("");
		}
	}
}
