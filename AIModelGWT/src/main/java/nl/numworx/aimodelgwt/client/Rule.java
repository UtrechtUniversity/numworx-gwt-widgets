package nl.numworx.aimodelgwt.client;

import java.util.Map;

import nl.uu.fi.dwo.ideas.client.RuleIF;

class Rule implements RuleIF {
	
	private String expr;
	

	Rule(String expr) {
		this.expr = expr;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public String getExpr() {
		return expr;
	}

	@Override
	public boolean isException() {
		return false;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean isReady() {
		return false;
	}

	@Override
	public Map getContext() {
		return null;
	}

	@Override
	public String getPrefix() {
		return null;
	}

	@Override
	public String getArgument() {
		return null;
	}

}
