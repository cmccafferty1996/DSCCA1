package ct414;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {

    private Client() {}

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            ExamServer stub = (ExamServer) registry.lookup("ExamServer");
            
            System.out.println("Username: ");
            Scanner in = new Scanner(System.in); //add check this can be parsed to int
            String userName = in.nextLine();
            System.out.println("Password: ");
            String password = in.nextLine();
            
            int result = stub.login(Integer.parseInt(userName), password);
            if(result == 1){
            	System.out.println("Login Successful!");
            }
            else{
            	System.out.println("Login Failed");
            }
            
            // Print do you want to see your assessments info?
            // Ask to start assessment, read in choice
            // Option to submit
            
            // Print questions, read in answer
            
            
            
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
