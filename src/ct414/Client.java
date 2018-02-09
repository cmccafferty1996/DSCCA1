package ct414;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class Client {

    private Client() {}
    
    private static String userName;
	private static int studentId;
	private static int token;
	private static ExamServer stub;

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            stub = (ExamServer) registry.lookup("ExamServer");
            
            System.out.println("Username: ");
            Scanner in = new Scanner(System.in); //add check this can be parsed to int
            userName = in.nextLine();
            System.out.println("Password: ");
            String password = in.nextLine();
            
            studentId = Integer.parseInt(userName);
            
            token = stub.login(studentId, password);
            if(token == 1){
            	System.out.println("Login Successful!");
            }
            else{
            	System.out.println("Login Failed");
            }
            
            // Print do you want to see your assessments info?
            // Ask to start assessment, read in choice
            // Option to submit
            
            // Print questions, read in answer
            
            Assessment a1 = stub.getAssessment(token, studentId, "101"); //replace this
            
            startAssessment(a1);
            
            
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
    
    public static void startAssessment(Assessment a) throws RemoteException, UnauthorizedAccess, NoMatchingAssessment{
		Scanner in = new Scanner(System.in);
		
		System.out.println("\n" + a.getInformation());
        List<Question> questions = a.getQuestions();
        for(Question q : questions){
        	System.out.println(q.getQuestionNumber() + ") " + q.getQuestionDetail());
        }
        
        
        boolean continueFlag = true;
        boolean validResponse = false;
        String response;
        int qNumber = 0;
        Question quest = null;
        int ansNumber = 0;
        
        while(continueFlag){
	        System.out.println("\nSelect a Question Number: ");
	        
	        while(!validResponse){
	        	response = in.nextLine();
				response = response.replaceAll("\\s+","");
				
				try{
				    int n = Integer.parseInt(response);
				    qNumber = n;
				    
			       	quest = a.getQuestion(qNumber);
			        validResponse = true;
				} 
				catch (NumberFormatException e) {
				    System.out.println("Invalid input! Please enter an integer!");
				}
		        catch(InvalidQuestionNumber e){
		        	System.out.println("Invalid input! There is no question number " + qNumber);
//		        	e.getMessage();
		        }
				
		        

			}
	                
	        validResponse = false;
	        
	        
	        System.out.println("\n" + quest.getQuestionDetail());
	        
	        String[] answers = quest.getAnswerOptions();
	        
	        for(int i = 0; i < answers.length; i++){
	        	int num = i + 1;
	        	System.out.println(num + ") " + answers[i]);
	        }
	        
	        System.out.println("\nSelect an Answer Number:");
	        
	        while(!validResponse){
	        	response = in.nextLine();
				response = response.replaceAll("\\s+","");
				
				try {
				    int n = Integer.parseInt(response);
				   	
				   	ansNumber = n; //May be problem/confusion here down the line as Answer 1) = answer[0]
				   	a.selectAnswer(quest.getQuestionNumber(), ansNumber);
				   	
				   	validResponse = true;  
				} 
				catch (NumberFormatException e) {
				    System.out.println("Invalid input! Please enter an integer!");
				}
				catch (InvalidOptionNumber e) {
					System.out.println("Invalid input! There is no answer number " + ansNumber);
				} 
				catch (InvalidQuestionNumber e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
	        }
			validResponse = false;
	        
	        System.out.println("\nContinue? [Y/n]: ");
	        
	        while(!validResponse){
				response = in.nextLine();
				response = response.replaceAll("\\s+","");
				
				if(!response.equals("Y") && !response.equals("n")){
					System.out.println("Invalid input! Please enter \"Y\" or \"n\"\n");
				}
				else{
					validResponse = true;
					
					if(response.equals("Y")){
						System.out.println("\n" + a.getInformation());
						for(Question q : questions){
				        	System.out.println(q.getQuestionNumber() + ") " + q.getQuestionDetail());
				        }
					}
					else if(response.equals("n")){
						continueFlag = false;
					}
				}
			}
	        validResponse = false;
        }
	        
        System.out.println("\nSubmit Assessment? [Y/n]");
        
        while(!validResponse){
			response = in.nextLine();
			response = response.replaceAll("\\s+","");
			
			if(!response.equals("Y") && !response.equals("n")){
				System.out.println("Invalid input! Please enter \"Y\" or \"n\"\n");
			}
			else{
				validResponse = true;
				
				if(response.equals("Y")){
					stub.submitAssessment(token, studentId, a);
		        	System.out.println("Assessment submitted!");
		        	
		        	try{
		        		stub.submitAssessment(token, studentId, a);
		        	}
		        	catch(NoMatchingAssessment e){
		        		System.out.println(e.getMessage());
		        	}
				}
				else if(response.equals("n")){
					System.out.println("Assessment not submitted");
				}
			}
		}
        validResponse = false;
        in.close();
	}
}
