import java.io.*;
import java.net.*;
import java.net.Socket;

class Client
{
	public static void main(String[] args)
	{
		int serverPortNumber = 0;

		BufferedReader userInput = null;
		BufferedReader bufferedResponseReader = null;
		
        PrintWriter writeRequestToServer = null;
		PrintWriter writeFileToDisk = null;
		
        Socket clientSocket = null;
		String serverHostName = "localhost";

		 try
		 {
			 //If the user did not enter any port number for server to start on. Throw Exception.
			 if(args.length == 0)
			 {
                 throw new IllegalArgumentException("Port number not entered");
			 }
			 else
			 {
                 //ASK USER IF THE WANT TO SEND A BAD REQUEST (SET /filename HTTP/1.0)
                 System.out.println("If you would like to send a bad request enter (b): ");
			     userInput = new BufferedReader(new InputStreamReader(System.in));
                 
                 String request = userInput.readLine();

                 if(request.equalsIgnoreCase("b"))
                     request = "SET /";
                 else
                     request = "GET /";

                 //IF THE USER ENTERED BOTH THE SERVER NAME AND PORT
                 if(args.length == 2)
				 {
                     serverHostName = args[0];
					 serverPortNumber = Integer.parseInt(args[1]);
				 }
                 //ELSE USE THE DEFAULT SERVER NAME ("LOCALHOST")
				 else
                 {
				  	 serverPortNumber = Integer.parseInt(args[0]);
                 }

                 System.out.println("Enter the name of the file to retrieve from the server: ");
				 String filename = userInput.readLine();

                 //OPEN A SOCKET TO CONNECT TO THE SERVER WITH GIVEN NAME AND GIVEN PORT
				 clientSocket = new Socket(serverHostName, serverPortNumber);
                 System.out.println("Connected to the server on port#" + serverPortNumber);

                 //ADD FILENAME TO CREATE A REQUEST FORMAT
				 request +=  filename + " HTTP/1.0\n";
				
				 writeRequestToServer = new PrintWriter(clientSocket.getOutputStream(), true);
				 System.out.println("Request sent to server: " + request);
                 //WRITE REQUEST ON THE SOCKET, TO THE SERVER
				 writeRequestToServer.println(request);

				 String response = null;
                 //READ RESPONSE FROM THE SERVER ON THE SAME SOCKET
				 bufferedResponseReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));		

				 String line = bufferedResponseReader.readLine();

                 //CHECK WHAT RESPONSE THE SERVER SENT
                 //IF 200 OK, SAVE THE FILE ON LOCAL DISK WITH THE FILENAME SPECIFIED BY THE USER
                 if(line.equalsIgnoreCase("HTTP/1.0 200 OK"))
                 {
                     System.out.println("Please enter a name for the received file");
                     String newFilename = userInput.readLine();
                     
                     //IF THE USER DID NOT ENTER THE FILE NAME, USE THE FILENAME IN REQUEST
                     if(newFilename.equals("\n"))
                     {
                         filename = newFilename;
                         System.out.println("User entered " + newFilename);
                     }
                     
                     System.out.println("Response from server: " + line);

                     //WRITE FILE TO THE DISK
                     FileWriter fileWriter = new FileWriter(filename);
                     writeFileToDisk = new PrintWriter(new BufferedWriter(fileWriter));

					 while( (line = bufferedResponseReader.readLine()) != null )
					 {
                         writeFileToDisk.println(line);
                         System.out.println(line);
                     }	
				 }
                 else if(line.equalsIgnoreCase("HTTP/1.0 400 Bad Request"))
                 {
                     System.out.println("Response from server: " + line);
                 }
                 else if(line.equalsIgnoreCase("HTTP/1.0 404 Not Found"))
                 {
                     System.out.println("Response from server: " + line);
                     System.out.println("The requested resource was not found on the server!!!");
                 }	

				 System.out.println("Done reading response from server. Terminating client.");

                 //CLOSE SOCKETS AND FILE READER'S
                 userInput.close();
                 bufferedResponseReader.close();
				 writeRequestToServer.close();
				 clientSocket.close();
			}
		}
		catch(IllegalArgumentException iae)
		{
			System.out.println("Please enter input correctly.");
		}
		catch(IOException ioe)
		{
			System.out.println("Could not connect to the server on port#" +  serverPortNumber);
			System.exit(1);
		}
		catch(Exception e)
		{
			System.out.println("Exception: " + e);
		}
	}
}
