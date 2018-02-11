package ct414;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
	
	// Declare variables
    private static boolean validResponse = false;
    private static Scanner in;
    private static int studentId = 0;
    private static ExamServer stub;
    private static int token = 0;

    private Client() {}

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            stub = (ExamServer) registry.lookup("ExamServer");
            
            // Initialize scanner
            in = new Scanner(System.in);
            
            // Do login
            doLogin();
            
            // Get assessment Information
            printAssessments();
            
            // Handle selected assessment
            Assessment curr = selectAssessment();
            
            // If the client selects an assessment
            if (curr != null){
            	startAssessment(curr);
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
    
 // Private method that requests Student ID and password
    // Attempts to login by calling login function on server
    private static void doLogin() throws RemoteException, UnauthorizedAccess{
    	
    	while (!validResponse){
        	System.out.println("Username: ");
            String userName = in.nextLine();
            try {
            	studentId = Integer.parseInt(userName);
            	validResponse = true;
            } catch (NumberFormatException e){
            	System.out.println("Student ID must be a number!!");
            }
        }
        
        System.out.println("Password: ");
        String password = in.nextLine();
        
        token = stub.login(studentId, password);
        if(token == 1){ // need to look at this - CM
        	System.out.println("Login Successful!");
        }
        else{
        	System.out.println("Login Failed");
        }
    }
    
    // Print all available assessments for that student
    // There will only be one per course 
    private static void printAssessments() throws RemoteException{
    	
    	// Print assessments info
        System.out.println("Assessments available for "+studentId);
        try {
        	ArrayList<String> summary = 
        			(ArrayList<String>) stub.getAvailableSummary(token, studentId);
        	for (String s : summary){
        		System.out.println(s);
        	}
        	System.out.println("Enter course code of the assessment to begin assessment:");
        } catch (NoMatchingAssessment nma){
        	System.out.println(nma.getMessage());
       
		} catch (UnauthorizedAccess uaa) {
			System.out.println(uaa.getMessage());
		}
    }
    
    // Returns chosen assessment
    // Client selects an assessment by typing the course code of the assessment
    private static Assessment selectAssessment() throws RemoteException{
    	
    	validResponse = false;
    	Assessment curr = null;
        
        while(!validResponse){
        	String lookup = in.nextLine();
        	try{
        		 curr = stub.getAssessment(token, studentId, lookup);
        		validResponse = true;
        	} catch (UnauthorizedAccess uaa){
        		System.out.println(uaa.getMessage());
        	} catch (NoMatchingAssessment nma){
        		System.out.println("Please enter a valid course code from list!");
        		System.out.println(nma.getMessage());
        	}
        }
		return curr;
    }
    
    // Start selected assessment
    // Lists all questions and asks the client to select a question to answer
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
