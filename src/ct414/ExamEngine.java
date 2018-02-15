
package ct414;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

public class ExamEngine implements ExamServer {
		// Declare variables
		private ArrayList<Integer> enrolledStudentIds;
		private String password;
		private static int token;
		private static long tokenTimeStamp;
		private static long tokenTimeOut;
		private String[] courseCodes;
		private Map<String, AssessmentImpl> courseCodeToAssessmentMap;
		private Map<String, Integer[]> courseCodeToStudentIdsMap;
		private Map<Integer, List<Assessment>> completedAssessments;
		
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
	        
	        // Create random token with a value between 1 and 1000
	        token = ThreadLocalRandom.current().nextInt(1, 1000 + 1);
	        tokenTimeStamp = System.currentTimeMillis();
	        tokenTimeOut = 1000*60*60*2; // 2 hour timeout
	        
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
	        Calendar cal = Calendar.getInstance();
	        cal.set(Calendar.YEAR, 2018);
	    	cal.set(Calendar.MONTH, Calendar.JANUARY);
	    	cal.set(Calendar.DAY_OF_MONTH, 1);
	        AssessmentImpl a1 = new AssessmentImpl ("Math Test", cal.getTime(), mathsQuestions, 1);
	        
	        cal.set(Calendar.YEAR, 2018);
	    	cal.set(Calendar.MONTH, Calendar.FEBRUARY);
	    	cal.set(Calendar.DAY_OF_MONTH, 25);
	        AssessmentImpl a2 = new AssessmentImpl ("Chemistry Test", cal.getTime(), chemQuestions, 2);
	        
	        cal.set(Calendar.YEAR, 2018);
	    	cal.set(Calendar.MONTH, Calendar.FEBRUARY);
	    	cal.set(Calendar.DAY_OF_MONTH, 24);
	        AssessmentImpl a3 = new AssessmentImpl ("Physics Test", cal.getTime(), physicsQuestions, 3);
	        
	        // Store the current assessment available for each course code
	        courseCodeToAssessmentMap = new HashMap<String, AssessmentImpl>();
	        courseCodeToAssessmentMap.put(mathCourseCode, a1);
	        courseCodeToAssessmentMap.put(chemistryCourseCode, a2);
	        courseCodeToAssessmentMap.put(physicsCourseCode, a3);
	        
	        // Map to store all assessments completed by a student
	        completedAssessments = new HashMap<Integer, List<Assessment>>();
	    }

	    // Return an access token that allows access to the server for some time period
	    public int login(int studentid, String password) throws 
	                UnauthorizedAccess, RemoteException {
	    	
	    	if (!enrolledStudentIds.contains(studentid) || !this.password.equals(password)) {
	    		throw new UnauthorizedAccess("Invalid Id number or password");
	    	}

	    	return token;	
	    }

	    // Return a summary list of Assessments currently available for this studentid
	    public List<String> getAvailableSummary(int token, int studentid) throws
	                UnauthorizedAccess, NoMatchingAssessment, RemoteException {
	    	
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
	    	Date now = new Date();
	    	for(String i :currCodes){
		    	if (!(courseCodeToAssessmentMap.get(i) == null) && courseCodeToAssessmentMap.get(i).getClosingDate().after(now)){
		    		AssessmentImpl assessment = courseCodeToAssessmentMap.get(i);
		    		assessmentsSummary.add(i+". "+assessment.getInformation()
		    				+" closes on: "+assessment.getClosingDate().toString());
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
	    	
	    	if(this.token != token){
	    		throw new UnauthorizedAccess("Invalid Access Token");
	    	}
	    	else if(!Arrays.asList(courseCodes).contains(courseCode)){
	    		throw new NoMatchingAssessment("This course does not exist");
	    	}
	    	else if(!Arrays.asList(courseCodeToStudentIdsMap.get(courseCode)).contains(studentid)){
	    		throw new NoMatchingAssessment("Student Id not valid for this course");
	    	}
	    	else if(courseCodeToAssessmentMap.get(courseCode) == null){
	    		throw new NoMatchingAssessment("No assignment for this course");
	    	}

	        return courseCodeToAssessmentMap.get(courseCode);
	    }

	    // Submit a completed assessment
	    public void submitAssessment(int token, int studentid, Assessment completed) throws 
	                UnauthorizedAccess, NoMatchingAssessment, RemoteException {
	    	
	    	if(this.token != token){
	    		throw new UnauthorizedAccess("Invalid Access Token");
	    	}	
	    	// Check the assessment is still available
	    	// Meaning they have not missed the closing date/time
	        Date now = new Date();
	        if (completed.getClosingDate().before(now)){
	    		throw new NoMatchingAssessment("Submission closed for this assessment");
	    	} 
	        try{
	        	// Add to completed assessment arrayList
	        	ArrayList<Assessment> updated = (ArrayList<Assessment>) completedAssessments.get(studentid);
	        	
	        	int i = 0;
	        	boolean alreadySubmitted = false;
	        	for (Assessment a: updated){
	        		if(a.getAssociatedID() == completed.getAssociatedID()){
	        			updated.set(i, completed);
	        			alreadySubmitted = true;
	        		}
	        		i++;
	        	}
	        	if(alreadySubmitted == false){
	        		updated.add(completed);
	        	}
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
            
            long currentTimeStamp;
            while(true){
            	currentTimeStamp = System.currentTimeMillis();
            	// Token check
            	if((currentTimeStamp - tokenTimeStamp) > tokenTimeOut){
            		token = ThreadLocalRandom.current().nextInt(1, 100 + 1);
        	        tokenTimeStamp = System.currentTimeMillis();
            	}
            } 
        } catch (Exception e) {
            System.err.println("ExamEngine exception:");
            e.printStackTrace();
        }
    }
}