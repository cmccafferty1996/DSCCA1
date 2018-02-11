
package ct414;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ExamEngine implements ExamServer {
	
	// Declare variables
		private ArrayList<Integer> enrolledStudentIds;
		private String password;
		private int token;
		private String[] courseCodes;
		private Map<String, AssessmentImpl> courseCodeToAssessmentMap;
		private Map<String, Integer[]> courseCodeToStudentIdsMap;
		private Map<Integer, List<Assessment>> completedAssessments;
		
	    // Constructor is required - add test data here
	    public ExamEngine() {
	    	
	        super();
	        // Store all enrolled/valid Student IDs in a List
	        enrolledStudentIds = new ArrayList<Integer>();
	        enrolledStudentIds.add(1);
	        enrolledStudentIds.add(2);
	        enrolledStudentIds.add(3);
	        enrolledStudentIds.add(4);
	        enrolledStudentIds.add(5);
	        password = "topSecret";
	        token = 1;
	        
	        // Separate them out into the different courses 
	        Integer[] mathStudentIds = new Integer[] {1, 3, 5};
	        Integer[] chemistryStudentIds = new Integer[] {1, 2, 4};
	        Integer[] physicsStudentIds = new Integer[] {1, 2, 3};
	        
	        // Declare course codes
	        String mathCourseCode = "101";
	        String chemistryCourseCode = "102";
	        String physicsCourseCode = "103";
	        
	        // Save all valid course codes in an array
	        courseCodes = new String[] {mathCourseCode, chemistryCourseCode, physicsCourseCode};
	        
	        // Add the Student IDs to corresponding course code
	        courseCodeToStudentIdsMap = new HashMap<String, Integer[]>();
	        courseCodeToStudentIdsMap.put(mathCourseCode, mathStudentIds);
	        courseCodeToStudentIdsMap.put(chemistryCourseCode, chemistryStudentIds);
	        courseCodeToStudentIdsMap.put(physicsCourseCode, physicsStudentIds);
	        
	        // Maths questions
	        QuestionImpl m1 = new QuestionImpl(1, "2 + 2 = ?", new String[] {"0", "2", "4", "None of these!"});
	        QuestionImpl m2 = new QuestionImpl(2, "2 x 2 = ?", new String[] {"0", "2", "4", "None of these!"});
	        QuestionImpl m3 = new QuestionImpl(3, "2 - 2 = ?", new String[] {"0", "2", "4", "None of these!"});
	        List<Question> mathsQuestions = new ArrayList<Question>();
	        mathsQuestions.add(m1);
	        mathsQuestions.add(m2);
	        mathsQuestions.add(m3);
	        
	        // Chemistry questions
	        QuestionImpl c1 = new QuestionImpl(1, "Chemical symbol for water = ?", new String[] {"CO2", "HO", "H20", "None of these!"});
	        QuestionImpl c2 = new QuestionImpl(2, "Chemical symbol for Carbon dioxide = ?", new String[] {"CO2", "HO", "H20", "None of these!"});
	        QuestionImpl c3 = new QuestionImpl(3, "Noble gas = ?", new String[] {"H", "Ar", "K", "None of these!"});
	        List<Question> chemQuestions = new ArrayList<Question>();
	        chemQuestions.add(c1);
	        chemQuestions.add(c2);
	        chemQuestions.add(c3);
	        
	        // Physics questions
	        QuestionImpl p1 = new QuestionImpl(1, "Force equation F = ?", new String[] {"m x a", "m / a", "m x c^2", "None of these!"});
	        QuestionImpl p2 = new QuestionImpl(2, "Acceleration due to gravity = ?", new String[] {"0.00", "9.81", "3.14", "None of these!"});
	        QuestionImpl p3 = new QuestionImpl(3, "Voltage (V) = ?", new String[] {"I/R", "I x R", "I^R", "None of these!"});
	        List<Question> physicsQuestions = new ArrayList<Question>();
	        physicsQuestions.add(p1);
	        physicsQuestions.add(p2);
	        physicsQuestions.add(p3);
	        
	        // Define 3 new assessments - one for each course code
	        AssessmentImpl a1 = new AssessmentImpl ("Math Test", new Date(2018, 2, 14), mathsQuestions, 1);
	        AssessmentImpl a2 = new AssessmentImpl ("Chemistry Test", new Date(2017, 2, 14), chemQuestions, 1);
	        AssessmentImpl a3 = new AssessmentImpl ("Physics Test", new Date(2016, 2, 14), physicsQuestions, 1);
	        
	        // Store the current assessment available for each course code
	        courseCodeToAssessmentMap = new HashMap<String, AssessmentImpl>();
	        courseCodeToAssessmentMap.put(mathCourseCode, a1);
	        courseCodeToAssessmentMap.put(chemistryCourseCode, a2);
	        courseCodeToAssessmentMap.put(physicsCourseCode, a3);
	        
	        // Map to store all assessments completed by a student
	        completedAssessments = new HashMap<Integer, List<Assessment>>();
	    }

	    // Implement the methods defined in the ExamServer interface...
	    // Return an access token that allows access to the server for some time period
	    public int login(int studentid, String password) throws 
	                UnauthorizedAccess, RemoteException {
	    	
	    	if (!enrolledStudentIds.contains(studentid) || !this.password.equals(password)) {
	    		throw new UnauthorizedAccess("Invalid Id number or password");
	    	}
	    	
	    	//Add token time stamp so after 2 hours the token is invalid? -MD

	    	return 1;	
	    }

	    // Return a summary list of Assessments currently available for this studentid
	    public List<String> getAvailableSummary(int token, int studentid) throws
	                UnauthorizedAccess, NoMatchingAssessment, RemoteException {
	    	
	    	// Token exception
	    	if(this.token != token){
	    		throw new UnauthorizedAccess("Invalid Access Token");
	    	}
	    	
	    	// Assessment summary list
	    	List<String> assessmentsSummary = new ArrayList<String>();
	    	
	    	// Get the course code from studentId first
	    	ArrayList<String> currCodes = new ArrayList<String>();
	    	Integer[] studentIds;
	    	for (Entry<String, Integer[]> e : courseCodeToStudentIdsMap.entrySet()){
	    		studentIds = e.getValue();
	    		for (int i=0; i < studentIds.length-1; i++){
	    			if (studentid == studentIds[i]){
	    				currCodes.add(e.getKey());
	    			}
	    		}
	    	}
	    	
	    	// Check for available assessments for that course code
	    	for(String i :currCodes){
		    	if (!(courseCodeToAssessmentMap.get(i) == null)){
		    		AssessmentImpl assessment = courseCodeToAssessmentMap.get(i);
		    		assessmentsSummary.add(i+". "+assessment.getInformation()
		    				+" closes on:"+assessment.getClosingDate().toString());
		    	}
	    	}
	    	
	    	if (assessmentsSummary.isEmpty()){
	    		throw new NoMatchingAssessment("No assessments found for your Id: "+studentid);
	    	}

	        return assessmentsSummary;
	    }

	    // Return an Assessment object associated with a particular course code
	    public Assessment getAssessment(int token, int studentid, String courseCode) throws
	                UnauthorizedAccess, NoMatchingAssessment, RemoteException {
	    	
	    	// Token exception
	    	if(this.token != token){
	    		throw new UnauthorizedAccess("Invalid Access Token");
	    	}
	    	else if(!Arrays.asList(courseCodes).contains(courseCode)){
	    		throw new UnauthorizedAccess("This course does not exist");
	    	}
	    	else if(!Arrays.asList(courseCodeToStudentIdsMap.get(courseCode)).contains(studentid)){
	    		throw new UnauthorizedAccess("Student Id not valid for this course");
	    	}
	    	else if(courseCodeToAssessmentMap.get(courseCode) == null){
	    		throw new NoMatchingAssessment("No assignment for this course");
	    	}

	        return courseCodeToAssessmentMap.get(courseCode);
	    }

	    // Submit a completed assessment
	    public void submitAssessment(int token, int studentid, Assessment completed) throws 
	                UnauthorizedAccess, NoMatchingAssessment, RemoteException {
	    	
	    	// Token exception
	    	if(this.token != token){
	    		throw new UnauthorizedAccess("Invalid Access Token");
	    	}
	    	
	    	// Check the assessment is still available
	    	// Meaning they have not missed the closing date/time
	        Date now = new Date();
	        if (completed.getClosingDate().before(now)){
	    		throw new NoMatchingAssessment("Submission closed");
	    	} 
	        try{
	        	// Add to completed list
	        	ArrayList<Assessment> updated = (ArrayList<Assessment>) completedAssessments.get(studentid);
	        	updated.add(completed);
	        	completedAssessments.put(studentid, updated);
	        }
	        catch(NullPointerException e){
	        	//For first completed assessment
	        	ArrayList<Assessment> newArr = new ArrayList<Assessment>();
	        	newArr.add(completed);
	        	completedAssessments.put(studentid, newArr);
	        }
	    }

    public static void main(String[] args) {
    	
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "ExamServer";
            ExamServer engine = new ExamEngine();
            ExamServer stub =
                (ExamServer) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("ExamEngine bound");
        } catch (Exception e) {
            System.err.println("ExamEngine exception:");
            e.printStackTrace();
        }
    }
}