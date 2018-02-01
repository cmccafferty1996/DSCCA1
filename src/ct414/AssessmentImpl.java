package ct414;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssessmentImpl implements Assessment {

	private String information;
	private Date closingDate;
	private List<Question> questions;
	Map<Integer, Integer> ourMap;
	private int associatedID;
	
	// Constructor
	public AssessmentImpl (String info, Date cDate, List<Question> qs, int id){
		information = info;
		closingDate = cDate;
		questions = qs;
		associatedID = id;
		ourMap = new HashMap<Integer, Integer>();
		
	}
	
	@Override
	public String getInformation() {
		return information;
	}

	@Override
	public Date getClosingDate() {
		return closingDate;
	}

	@Override
	public List<Question> getQuestions() {
		return questions;
	}

	@Override
	public Question getQuestion(int questionNumber) throws InvalidQuestionNumber {
		Question question = null;
		for (Question q : questions){
			if (q.getQuestionNumber() == questionNumber){
				question = q;
			}
		}
		if (question == null) throw InvalidQuestionNumber
		
		return question;
	}

	@Override
	public void selectAnswer(int questionNumber, int optionNumber) throws InvalidQuestionNumber, InvalidOptionNumber {
		//Key Question Number : Option Number
		ourMap.put(questionNumber, optionNumber);
	}

	@Override
	public int getSelectedAnswer(int questionNumber) {
		return ourMap.get(questionNumber);
	}

	@Override
	public int getAssociatedID() {
		return associatedID;
	}

}
