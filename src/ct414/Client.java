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
    private static String response;
    private static Scanner in;
    private static int studentId = 0;
    private static ExamServer stub;
    private static int token = 0;
    private static boolean firstLogin = false;

    private Client() {}

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            stub = (ExamServer) registry.lookup("ExamServer");
            boolean continueFlag = false;
            
            // Initialize scanner
            in = new Scanner(System.in);
            // Do login
            doLogin();
            
            while(!continueFlag){
	            
	            // Get assessment Information
	            printAssessments();
	            
	            // Handle selected assessment
	            Assessment curr = selectAssessment();
	            
	            // If the user selects an assessment
	            if (curr != null){
	            	startAssessment(curr);
	            }
	            
	            
	            System.out.println("\nDo you want to try another assessment?[Y/n] ");
	            while(!validResponse){
	    			response = in.nextLine();
	    			response = response.replaceAll("\\s+","");
	    			
	    			if(!response.equals("Y") && !response.equals("n")){
	    				System.out.println("Invalid input! Please enter \"Y\" or \"n\"\n");
	    			}
	    			else{
	    				validResponse = true;
	    				
	    				if(response.equals("n")){
	    		        	continueFlag = true;
	    				}
	    			}
	    		}
	            validResponse = false;
            }
            System.out.println("\nUser " + studentId + " logged out.");
            in.close();
        } catch(Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
    
    // Private method that requests Student ID and password
    // Attempts to login by calling login function on server
    private static void doLogin() throws RemoteException{
    	
    	boolean login = false;
    	validResponse = false;
    	while(!login){
    		//Stops user logging in as different user if they timeout
    		if(!firstLogin){
		    	while (!validResponse){
		        	System.out.println("\nStudent ID: ");
		            String userName = in.nextLine();
		            try{
		            	studentId = Integer.parseInt(userName);
		            	validResponse = true;
		            } 
		            catch(NumberFormatException e){
		            	System.out.println("Student ID must be a number!");
		            }
		        }
		    	firstLogin = true;
		    	validResponse = false;
    		}
	        
	        System.out.println("Password: ");
	        String password = in.nextLine();
	        
	        try{
				token = stub.login(studentId, password);
				System.out.println("\nLogin Successful!");
				login = true;
			} 
	        catch(UnauthorizedAccess e) {
				System.out.println(e.getMessage());
				System.out.println("\nLogin Failed");
			}
    	}
    }
    
    // Print all available assessments for that student
    // There will only be one per course 
    private static void printAssessments() throws RemoteException{
    	
    	// Print assessments info
        System.out.println("\nAssessments available for "+studentId + ": ");
        try{
        	ArrayList<String> summary = 
        			(ArrayList<String>) stub.getAvailableSummary(token, studentId);
        	for (String s : summary){
        		System.out.println(s);
        	}
        	
        } 
        catch(NoMatchingAssessment nma){
        	System.out.println(nma.getMessage());
       
		} 
        catch(UnauthorizedAccess uaa) {
			System.out.println(uaa.getMessage());
			System.out.println("\nYour session timed out!\nPlease login again to continue.");
			doLogin();
			printAssessments();
		}
    }
    
    // Returns chosen assessment
    // Client selects an assessment by typing the course code of the assessment
    private static Assessment selectAssessment() throws RemoteException{
    	
    	validResponse = false;
    	Assessment curr = null;
        
    	System.out.println("\nEnter the course code of an assessment to begin the assessment:");
        while(!validResponse){
        	String lookup = in.nextLine();
        	lookup = lookup.replaceAll("\\s+","");
        	try{
        		 curr = stub.getAssessment(token, studentId, lookup);
        		 validResponse = true;
        	} 
        	catch(UnauthorizedAccess uaa){
        		System.out.println(uaa.getMessage());
        		System.out.println("\nYour session timed out!\nPlease login again to continue.");
    			doLogin();
    			System.out.println("\nEnter the course code of an assessment to begin the assessment:");
        	} 
        	catch(NoMatchingAssessment nma){
        		System.out.println(nma.getMessage());
        	}
        }
		return curr;
    }
    
    // Start selected assessment
    // Lists all questions and asks the client to select a question to answer
    public static void startAssessment(Assessment assess) throws RemoteException{
		
		System.out.println("\n" + assess.getInformation());
        List<Question> questions = assess.getQuestions();
        for(Question q : questions){
        	System.out.println(q.getQuestionNumber() + ") " + q.getQuestionDetail());
        }
        
        
        boolean continueFlag = true;
        int qNumber = 0;
        Question quest = null;
        int ansNumber = 0;
        validResponse = false;
        
        while(continueFlag){
	        System.out.println("\nSelect a Question Number: ");
	        
	        while(!validResponse){
	        	response = in.nextLine();
				response = response.replaceAll("\\s+","");
				
				try{
				    int n = Integer.parseInt(response);
				    qNumber = n;
			       	quest = assess.getQuestion(qNumber);
			        validResponse = true;
				} 
				catch(NumberFormatException e) {
				    System.out.println("Invalid input! Please enter an integer!");
				}
		        catch(InvalidQuestionNumber e){
		        	System.out.println("Invalid input! There is no question number " + qNumber);
		        	System.out.println(e.getMessage());
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
				
				try{
				    int n = Integer.parseInt(response);
				   	
				   	ansNumber = n;
				   	assess.selectAnswer(quest.getQuestionNumber(), ansNumber);
				   	
				   	validResponse = true;  
				} 
				catch(NumberFormatException e) {
				    System.out.println("Invalid input! Please enter an integer!");
				}
				catch(InvalidOptionNumber e) {
					System.out.println("Invalid input! There is no answer number " + ansNumber);
				} 
				catch(InvalidQuestionNumber e) {
					System.out.println(e.getMessage());
				}
				
	        }
			validResponse = false;
	        
	        System.out.println("\nContinue answering questions? [Y/n]: ");
	        
	        while(!validResponse){
				response = in.nextLine();
				response = response.replaceAll("\\s+","");
				
				if(!response.equals("Y") && !response.equals("n")){
					System.out.println("Invalid input! Please enter \"Y\" or \"n\"\n");
				}
				else{
					validResponse = true;
					
					if(response.equals("Y")){
						System.out.println("\n" + assess.getInformation());
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
				
				
				if(response.equals("Y")){
		        	try{
		        		stub.submitAssessment(token, studentId, assess);
		        		System.out.println("Assessment submitted!");
		        		validResponse = true;
		        	}
		        	catch(NoMatchingAssessment e){
		        		System.out.println(e.getMessage());
		        	} 
		        	catch(UnauthorizedAccess e) {	
		        		System.out.println(e.getMessage());
		        		System.out.println("\nYour session timed out!\nPlease login again to continue.");
		    			doLogin();
		    			System.out.println("\nSubmit Assessment? [Y/n]");
					}
				}
				else if(response.equals("n")){
					System.out.println("Assessment not submitted");
					validResponse = true;
				}
			}
		}
        validResponse = false;
	}
}
