package uk.ac.open.kmi.uciad.renderer;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import uk.ac.open.kmi.uciad.helper.Actor;
import uk.ac.open.kmi.uciad.helper.ActorAgent;
import uk.ac.open.kmi.uciad.helper.ActorAgentSetting;
import uk.ac.open.kmi.uciad.helper.HTTPAction;
import uk.ac.open.kmi.uciad.helper.Page;
import uk.ac.open.kmi.uciad.helper.ParameterValue;
import uk.ac.open.kmi.uciad.helper.Trace;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

@SuppressWarnings("unused")
public interface RDFRenderer {	
	public void renderRDF(Vector<Trace> traceVector, Date dateToRenderLogFor);
	public Resource renderActor(Actor actor);
	public Resource renderActorAgentSetting(ActorAgentSetting actorAgentSetting);
	public Resource renderActorAgent(ActorAgent actorAgent);
	public List<Resource> renderParameterValue(ParameterValue parameterValue);
	public Resource renderPage(Page page);
	public Resource renderAction(HTTPAction traceAction);
	public Literal renderTime(Date traceTime);
	public Resource renderResponse(String traceResponse, int responseSize);
	public Literal renderFollowTrace(String followTrace);
	public Resource renderComputer(ActorAgentSetting actorAgentComputer);
}
