package nl.numworx.leerdoelwidgetgwt.client;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import fi.dwo.gwt.lib.rest.CallManagers.MethodManager;
import nl.numworx.leerdoelwidgetgwt.client.LeerdoelWidgetGWT.RoleAPI;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionService;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;

public class ReviewAPI implements RoleAPI, DescriptionService {

	public ReviewAPI(DwoGlobalVars vars) {
	}

	@Override
	public DescriptionService getDescriptionService() {
		return this;
	}

	@Override
	public MethodManager getMethodManager() {
		return MethodManager.teacher();
	}

	@Override
	public Promise<DomStudentModelContext> getStudentModel(DomContext context, DomStudentModelContextId studentModelID,
			DomSchoolClass schoolclass) {
		return Promises.failed(new IllegalArgumentException());
	}

	@Override
	public Promise<DomStudentModelDataScore> getScore(DomStudentModelContext4Student studentModel) {
		// TODO Auto-generated method stub
		return Promises.failed(new IllegalArgumentException());
	}

	@Override
	public Promise<String> getDescription(DomStudentModelContextId id, DomStudentModelContextInfo info) {
		// TODO Auto-generated method stub
		return Promises.failed(new IllegalArgumentException());
	}

}
