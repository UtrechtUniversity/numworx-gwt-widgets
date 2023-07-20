package nl.numworx.aimodelgwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import nl.uu.fi.dwo.ideas.client.AbstractIdeas;
import nl.uu.fi.dwo.ideas.client.ExerciseArrayCallback;
import nl.uu.fi.dwo.ideas.client.IdeasIF;
import nl.uu.fi.dwo.ideas.client.RuleIF;
import nl.uu.fi.dwo.ideas.client.Usermodel;

public class FailingIdeas extends AbstractIdeas implements IdeasIF {
	
	static final Throwable NOT_IMPLEMENTED = new RuntimeException("always failing");

	@Override
	public void getDerivation(RuleIF rule, String strategie,
			AsyncCallback<RuleIF[]> callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void getAllFirsts(RuleIF rule, String strategie,
			AsyncCallback<RuleIF[]> callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void getOneFirst(RuleIF expr, String strategie, AsyncCallback<RuleIF> callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void findBuggyRules(RuleIF expr, RuleIF input, String stategie,
			AsyncCallback<RuleIF[]> callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void getExerciseList(ExerciseArrayCallback callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void diagnose(RuleIF vgl, RuleIF input, String strategie,
			AsyncCallback<RuleIF> callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void getRuleList(String strategie, AsyncCallback<RuleIF[]> callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void getRulesInfo(String strategie, AsyncCallback<RuleIF[]> callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void getExamples(String strategie, AsyncCallback<RuleIF[]> callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void interpret(String how, RuleIF[] args, AsyncCallback<RuleIF> callback) {
		NOT_IMPLEMENTED.fillInStackTrace();
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void diagnose(RuleIF[] exprs, String strategie,
			AsyncCallback<RuleIF[]> callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

	@Override
	public void adviseMe(RuleIF[] inputs, String exercise, AsyncCallback<RuleIF> callback) {
		callback.onFailure(NOT_IMPLEMENTED);
	}

  @Override
  public void getOneHint(RuleIF expr, String strategie, AsyncCallback<RuleIF> callback) {
    callback.onFailure(NOT_IMPLEMENTED);    
  }

	@Override
	public void adviseMeUsermodel(RuleIF[] inputs, String exercise, AsyncCallback<Usermodel[]> callback) {
	    callback.onFailure(NOT_IMPLEMENTED);    
	}

	@Override
	public void aiModel(RuleIF[] input, String strategy, AsyncCallback<RuleIF[]> callback) {
	    callback.onFailure(NOT_IMPLEMENTED);    	
	}

}
