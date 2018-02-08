package ct414;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    private Client() {}

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            ExamServer stub = (ExamServer) registry.lookup("ExamServer");
            
            int result = stub.login(1, "topSecret");
            System.out.println("The token is: " + result);
            
            
//            String response = stub.sayHello();
//            System.out.println("response: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
