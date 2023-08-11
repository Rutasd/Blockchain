/**
 This class implements a TCP client that communicates with a blockchain server
 It provides a menu-driven interface for performing various operations on the blockchain.
 The client sends messages to the server in JSON format and receives responses from the server in JSON format.
 The class uses the Gson library for JSON serialization and deserialization.
 Upon receiving a response from the server, the client processes the response and displays the result to the user.
 * @author: Ruta Deshpande
 * @andrew id: rutasurd
 * @email id: rutasurd@andrew.cmu.edu
 * @date: 16th March 2023
 * Project 3
 */


package ds;
//imports
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Scanner;

public class TCPClient {
    //member variables
    static Scanner scanner;
    static BufferedReader in;
    static PrintWriter out;
    static Socket clientSocket = null;
    public static void main(String args[]) {
        //initializing server port
        int serverPort = 7777;
        try {
            //creating new socket
            clientSocket = new Socket("localhost", serverPort);
            //creating new scanner
            scanner = new Scanner(System.in);
            RequestMessage requestMessage;
            while(true)
            {
                //Menu for blockchain operations
                System.out.println("0. View basic blockchain status.");
                System.out.println("1. Add a transaction to the blockchain.");
                System.out.println("2. Verify the blockchain.");
                System.out.println("3. View the blockchain.");
                System.out.println("4. Corrupt the chain.");
                System.out.println("5. Hide the Corruption by recomputing hashes.");
                System.out.println("6. Exit");
                //taking user input for choice
                int choice = scanner.nextInt();
                switch(choice)
                {
                    case 0, 2, 3, 5:
                        //creating a new RequestMessage with only choice as a parameter
                        requestMessage = new RequestMessage(choice);
                        //sending the requestMessage to server after converting toString
                        sendToServer(requestMessage.toString());
                        //receiving from server
                        receiveFromServer();
                        break;

                    case 1:
                        System.out.println("Enter difficulty > 0");
                        //taking user input for difficulty
                        int difficulty = scanner.nextInt();
                        System.out.println("Enter transaction");
                        scanner.nextLine();
                        //taking user input as transaction data
                        String data = scanner.nextLine();
                        //creating requestMessage object using 3 parameters choice, difficulty and data
                        requestMessage = new RequestMessage(choice,difficulty,data);
                        //calling send to server method and sending the json obj
                        sendToServer(requestMessage.toString());
                        //calling the receive from server
                        receiveFromServer();
                        break;


                    case 4:
                        System.out.println("Corrupt the blockchain");
                        System.out.print("Enter block ID of block to Corrupt: ");
                        //taking user input for id of block to be corrupted
                        int id = scanner.nextInt();
                        System.out.print("Enter new data for block " + id + ":\n");
                        scanner.nextLine();
                        //taking input of corruptdata
                        String corruptData = scanner.nextLine();
                        //creating requestMessage object using 3 parameters choice, corruptdata  and id
                        requestMessage = new RequestMessage(choice,corruptData,id);
                        //calling send to server method and sending the json obj
                        sendToServer(requestMessage.toString());
                        //calling the receive from server
                        receiveFromServer();
                        break;

                    case 6:
                        System.exit(0);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // This method sends a message to the server using the client socket connection.
    public static void sendToServer(String s)
    {
        try {
            // Create a new BufferedReader to read input from the client socket.
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // Create a new PrintWriter to write output to the client socket.
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            // Send the message to the server by writing it to the PrintWriter.
            out.println(s);
            // Flush the PrintWriter to ensure the message is sent immediately.
            out.flush();
        } catch (IOException e) {
            // If an error occurs while sending the message, throw a RuntimeException.
            throw new RuntimeException(e);
        }
    }

    // This method receives data from the server and processes it based on its contents.
    public static void receiveFromServer()
    {
        // Initialize a variable to store the data read from the stream.
        String data = null;
        try {
            // Read a line of data from the BufferedReader connected to the client socket.
            data = in.readLine();
        } catch (IOException e) {
            // If an error occurs while reading the data, throw a RuntimeException.
            throw new RuntimeException(e);
        }
        // Create a new Gson object to parse the received data into a JsonObject.
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(data, JsonObject.class);

        // Retrieve the operation code from the JsonObject.
        int sel = jsonObject.get("operation").getAsInt();

        // Process the data based on the received operation code.
        switch (sel)
        {
            case 0:
                // Display information about the current state of the blockchain.
                System.out.println("Current Size of chain: " + jsonObject.get("size").getAsInt());
                System.out.println("Difficulty of most recent block: " + jsonObject.get("difficulty").getAsInt());
                System.out.println("Total difficulty for all blocks: " + jsonObject.get("totalDifficulty").getAsInt());
                System.out.println("Approximate hashes per second on this machine: " + jsonObject.get("hashesPerSecond").getAsString());
                System.out.println("Expected total hashes required for the whole chain: " + jsonObject.get("totalExpectedHashes").getAsString());
                System.out.println("Nonce for the most recent block: " + jsonObject.get("nonce").getAsInt());
                System.out.println("Chain hash: " + jsonObject.get("hash").getAsString() + "\n");
                break;

            case 1:
                // Display information about the execution time for adding a block to the blockchain.
                System.out.println("Total execution time to add this block was "+jsonObject.get("elapsedTime")+ " milliseconds");
                break;

            case 2:
                // Verify the entire blockchain and display the result.
                System.out.println(jsonObject.get("response").getAsString());
                System.out.println("Total execution time to verify the chain was "+jsonObject.get("elapsedTime")+ " milliseconds");
                break;

            case 3:
                // Display the current state of the blockchain.
                System.out.println("View the blockchain");
                System.out.println(jsonObject.get("response").getAsString());
                break;

            case 4,5:
                // Display any response received from the server for the specified operation codes.
                System.out.println(jsonObject.get("response").getAsString());
                break;

            case 6:
                System.out.println("Quitting!");
        }
    }

}
