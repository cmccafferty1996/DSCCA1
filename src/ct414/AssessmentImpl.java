package ct414;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssessmentImpl implements Assessment {

	private String information;
	private Date closingDate;
	private List<Question> questions;
	Map<Integer, Integer> questionToAnswerMap;
	private int associatedID;
	
	// Constructor
	public AssessmentImpl (String info, Date cDate, List<Question> qs, int id){
		information = info;
		closingDate = cDate;
		questions = qs;
		associatedID = id;
		questionToAnswerMap = new HashMap<Integer, Integer>();
		
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
		
		if (question == null){
			throw new InvalidQuestionNumber("Invalid question number!");
		}
			
		return question;
	}

	@Override
	public void selectAnswer(int questionNumber, int optionNumber) throws InvalidQuestionNumber, InvalidOptionNumber {
		//Map Key Question Number : Option Number
		
		Question question = getQuestion(questionNumber);
		String[] answerOptions = question.getAnswerOptions();
		
		if(optionNumber < 1 || optionNumber > answerOptions.length){
			throw new InvalidOptionNumber("Invalid Option number!");
		}
		else{
			questionToAnswerMap.put(questionNumber, optionNumber);
		}
	}

	@Override
	public int getSelectedAnswer(int questionNumber) {
		return questionToAnswerMap.get(questionNumber);
	}

	@Override
	public int getAssociatedID() {
		return associatedID;
	}

}
