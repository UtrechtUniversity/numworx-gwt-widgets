package nl.numworx.aimodelgwt.client;

import java.util.Map;

import nl.uu.fi.dwo.ideas.client.AbstractRule;
import nl.uu.fi.dwo.ideas.client.RuleIF;

class Rule extends AbstractRule implements RuleIF {
	
	private String expr;
	private Map context;

	Rule(String expr) {
		this.expr = expr;
	}
	Rule(String expr, Map context) {
		this.expr = expr;
		this.context = context;
	}

	@Override
	public String getExpr() {
		return expr;
	}

	@Override
	public Map getContext() {
		return context;
	}

}
