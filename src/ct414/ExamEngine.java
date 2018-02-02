
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

public class ExamEngine implements ExamServer {
	private int[] enrolledStudentIds;
	private String password;
	private int token;
	private String[] courseCodes;
	private Map<String, AssessmentImpl> courseCodeToAssessmentMap;
	private Map<String, Integer[]> courseCodeToStudentIdsMap;
	
    // Constructor is required
    public ExamEngine() {
        super();
        enrolledStudentIds = new int[] {1, 2, 3, 4, 5};
        password = "topSecret";
        token = 1;
        
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
        
        courseCodeToAssessmentMap = new HashMap<String, AssessmentImpl >();
        courseCodeToAssessmentMap.put(mathCourseCode, a1);
    }

    // Implement the methods defined in the ExamServer interface...
    // Return an access token that allows access to the server for some time period
    public int login(int studentid, String password) throws 
                UnauthorizedAccess, RemoteException {
    	if (!Arrays.asList(enrolledStudentIds).contains(studentid) || !this.password.equals(password)) {
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

        // TBD: You need to implement this method!
        // For the moment method just returns an empty or null value to allow it to compile

        return null;
    }

    // Return an Assessment object associated with a particular course code
    public Assessment getAssessment(int token, int studentid, String courseCode) throws
                UnauthorizedAccess, NoMatchingAssessment, RemoteException {
    	
    	if(this.token != token){
    		throw new UnauthorizedAccess("Invalid Access Token");
    	}
    	else if(!Arrays.asList(courseCodes).contains(courseCode)){
    		throw new UnauthorizedAccess("This course doe not exist");
    	}
    	else if(!Arrays.asList(courseCodeToStudentIdsMap.get(courseCode)).contains(studentid)){
    		throw new UnauthorizedAccess("Student Id not valid for this course");
    	}
    	else if(courseCodeToAssessmentMap.get(courseCode) == null){
    		throw new NoMatchingAssessment("No assignment for this course");
    	}
    	
        // TBD: You need to implement this method!
        // For the moment method just returns an empty or null value to allow it to compile

        return courseCodeToAssessmentMap.get(courseCode);
    }

    // Submit a completed assessment
    public void submitAssessment(int token, int studentid, Assessment completed) throws 
                UnauthorizedAccess, NoMatchingAssessment, RemoteException {
    	if(this.token != token){
    		throw new UnauthorizedAccess("Invalid Access Token");
    	}
    	
        // Add assessment to a particular student like a map from student id to a list of there assessment???
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
