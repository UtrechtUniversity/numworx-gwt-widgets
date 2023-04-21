package nl.numworx.geodefinergwt.client.ui;

import nl.numworx.geodefiner.common.CELL;
import nl.numworx.geodefiner.common.UIModel;

import javax.inject.Inject;

import fi.euclides.event.Tracker;
import fi.euclides.expr.List;
import fi.euclides.model.Label;
import fi.euclides.util.Observable;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Algebra;
import fi.wiskopdr.expressies.Expressie;

public class HerleidList extends List {

	@Inject public HerleidList() {
	}

	public HerleidList(Tracker viewer) {
		setTracker(viewer);
	}

	@Override
	public void update(Observable observable, Object arg) {
		super.update(observable, arg);
	}

	@Override
	protected StringBuilder createBuffer() {
		return new StringBuilder("$f");
	}
	
	protected void setString(Label l, StringBuilder sb) {
		if(normal(l)) {
			l.setString(sb.substring(2));
			l.notifyObservers();
			return;
		}
		sb.append("@");
		String s1 = sb.toString();
		Expressie e1 = FormuleParser.geefExpressie(s1);
		if(e1!=null)
		{	
			e1 = Algebra.herleidMild(e1, true);
			sb.setLength(0);
			sb.append(e1);
		} else {
			sb.delete(0, 2);
			sb.setLength(sb.length()-1);
		}
		super.setString(l, sb);
	}

	private boolean normal(Label l) {
		CELL c = l.adapt(CELL.class);
		if(c == null) return true;
		UIModel<?, ?> model = c.config;
		if(model instanceof TextModel) {
			return !((TextModel) model).isHerleid();
		}
		return true;
	}

}
