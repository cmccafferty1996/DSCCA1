package ct414;

public class QuestionImpl implements Question {
	
	private int number;
	private String details;
	private String[] options;
	
	// Constructor
	public QuestionImpl(int no, String det, String[] answers){
		number = no;
		details = det;
		options = answers;
	}

	@Override
	public int getQuestionNumber() {
		return number;
	}

	@Override
	public String getQuestionDetail() {
		return details;
	}

	@Override
	public String[] getAnswerOptions() {
		return options;
	}

}
