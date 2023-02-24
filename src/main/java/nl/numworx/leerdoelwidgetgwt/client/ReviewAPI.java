package nl.numworx.leerdoelwidgetgwt.client;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import fi.dwo.gwt.lib.rest.CallManagers.MethodManager;
import nl.numworx.leerdoelwidgetgwt.client.LeerdoelWidgetGWT.RoleAPI;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionService;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;

public class ReviewAPI implements RoleAPI, DescriptionService {
	
	@SuppressWarnings("serial")
	public static class ReviewException extends RuntimeException { } 
	
	private static final Promise<?> FAILED = Promises.failed(new ReviewException());
	
	@SuppressWarnings("unchecked")
	private static <T> Promise<T> getFailed() { return (Promise<T>) FAILED; }

	public ReviewAPI(DwoGlobalVars vars) {
	}

	@Override
	public DescriptionService getDescriptionService() {
		return this;
	}

	@Override
	public MethodManager getMethodManager() {
		return null;
	}

	@Override
	public Promise<DomStudentModelContext> getStudentModel(DomContext context, DomStudentModelContextId studentModelID,
			DomSchoolClass schoolclass) {
		return getFailed();
	}

	@Override
	public Promise<DomStudentModelDataScore> getScore(DomStudentModelContext4Student studentModel) {
		return getFailed();
	}

	@Override
	public Promise<String> getDescription(DomStudentModelContextId id, DomStudentModelContextInfo info) {
		return getFailed();
	}

	@Override
	public Promise<DomMethod> getMethod(DomContext context, DomMethod id, DomDwoProfileId profile) {
		return getFailed();
	}

}
