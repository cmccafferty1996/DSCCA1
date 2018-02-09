
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
	private ArrayList<Integer> enrolledStudentIds;
	private String password;
	private int token;
	private String[] courseCodes;
	// Might need to change this to Map<String, List<AssessmentImpl>> below -CM
	private Map<String, AssessmentImpl> courseCodeToAssessmentMap;
	private Map<String, Integer[]> courseCodeToStudentIdsMap;
	private Map<Integer, List<Assessment>> completedAssessments;
	
    // Constructor is required
    public ExamEngine() {
        super();
        enrolledStudentIds = new ArrayList<Integer>();
        enrolledStudentIds.add(1);
        enrolledStudentIds.add(2);
        enrolledStudentIds.add(3);
        enrolledStudentIds.add(4);
        enrolledStudentIds.add(5);
        password = "topSecret";
        token = 1;
        
        completedAssessments = new HashMap<Integer, List<Assessment>>();
        
        courseCodes = new String[] {"101", "102", "103", "104"};
        
        Integer[] mathStudentIds = new Integer[] {1, 3, 5};
        String mathCourseCode = "101";
        courseCodeToStudentIdsMap = new HashMap<String, Integer[]>();
        courseCodeToStudentIdsMap.put(mathCourseCode, mathStudentIds);
        
        QuestionImpl q1 = new QuestionImpl(1, "2 + 2 = ?", new String[] {"0", "2", "4", "None of these!"});
        QuestionImpl q2 = new QuestionImpl(2, "2 x 2 = ?", new String[] {"0", "2", "4", "None of these!"});
        QuestionImpl q3 = new QuestionImpl(3, "2 - 2 = ?", new String[] {"0", "2", "4", "None of these!"});
        List<Question> questionList = new ArrayList<Question>();;
        questionList.add(q1);
        questionList.add(q2);
        questionList.add(q3);
        AssessmentImpl a1 = new AssessmentImpl ("Math Test", new Date(2018, 2, 14), questionList, 1);
        
        courseCodeToAssessmentMap = new HashMap<String, AssessmentImpl>();
        courseCodeToAssessmentMap.put(mathCourseCode, a1);
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
	    		assessmentsSummary.add(assessment.getInformation()
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
//        if (System.getSecurityManager() == null) {
//            System.setSecurityManager(new SecurityManager());
//        }
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