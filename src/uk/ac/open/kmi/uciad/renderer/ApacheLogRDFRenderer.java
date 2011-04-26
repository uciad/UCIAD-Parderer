package uk.ac.open.kmi.uciad.renderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

import uk.ac.open.kmi.uciad.helper.Actor;
import uk.ac.open.kmi.uciad.helper.ActorAgent;
import uk.ac.open.kmi.uciad.helper.ActorAgentSetting;
import uk.ac.open.kmi.uciad.helper.HTTPAction;
import uk.ac.open.kmi.uciad.helper.Page;
import uk.ac.open.kmi.uciad.helper.ParameterValue;
import uk.ac.open.kmi.uciad.helper.Trace;
import uk.ac.open.kmi.uciad.parser.ApacheLogParser;
import uk.ac.open.kmi.uciad.util.DataCompressor;
import uk.ac.open.kmi.uciad.util.MD5Generator;
import uk.ac.open.kmi.uciad.util.NameSpace;
import uk.ac.open.kmi.uciad.util.UCIADRepositoryManager;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import uk.ac.open.kmi.uciad.util.DateUtils;

public class ApacheLogRDFRenderer implements RDFRenderer {
	boolean createNewModel = false;
	int count = 1;
	String serverName = System.getProperty("serverName")+"/"; 
	Model apacheTraceModel = ModelFactory.createDefaultModel();	
	Trace traceEntry = null;
	Vector<String[]> resultVec = null;
	Date dateToParseLogFor = null;
        public static DateFormat dateFormatForParsedZipFile = new SimpleDateFormat("dd-MMM-yyyy");
        
//        public ApacheLogRDFRenderer (Date dateToParseLogFor){
//            this.dateToParseLogFor = dateToParseLogFor;
//        }
        
        @Override
	public void renderRDF(Vector<Trace> traceVector, Date dateToRenderLogFor) {
		try {
			resultVec = getRegularExpressions();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		Iterator<Trace> itrTraceVector = traceVector.iterator();		
		System.out.println("Starting RDF rendering...");		
		while (itrTraceVector.hasNext()){			
			traceEntry = itrTraceVector.next();
			if (createNewModel){
				apacheTraceModel = ModelFactory.createDefaultModel();
			}
			apacheTraceModel.createResource(NameSpace.TRACEBASE+serverName+traceEntry.getTraceID())
				.addProperty(RDF.type, apacheTraceModel.createResource(NameSpace.TRACE+"Trace"));
			//PAGE
			//System.out.println(traceEntry.getTraceID());			
			apacheTraceModel.createResource(NameSpace.TRACEBASE+serverName+traceEntry.getTraceID())				
				.addProperty(apacheTraceModel.createProperty(NameSpace.TRACE, "hasPageInvolved"), 
							 renderPage(traceEntry.getPageInvolved()));
			
			//PARAMETER VALUE
			if (traceEntry.getParameterValues() != null){
				List<Resource> resources = renderParameterValue(traceEntry.getParameterValues());
				for (Resource resource : resources){
					apacheTraceModel.createResource(NameSpace.TRACEBASE+serverName+traceEntry.getTraceID())
					.addProperty(apacheTraceModel.createProperty(NameSpace.TRACE, "hasParameterValue"), 
							resource.addProperty(RDF.type, apacheTraceModel.createResource(NameSpace.TRACE+"ParameterValue")));
				}
			}
			
			//ACTION
			apacheTraceModel.createResource(NameSpace.TRACEBASE+serverName+traceEntry.getTraceID())
			.addProperty(apacheTraceModel.createProperty(NameSpace.TRACE, "hasAction"), 
						 				renderAction(traceEntry.getAction()));
			
			
			//TIME
			apacheTraceModel.createResource(NameSpace.TRACEBASE+serverName+traceEntry.getTraceID())
			.addProperty(apacheTraceModel.createProperty(NameSpace.TRACE, "hasTime"), 
						 				renderTime(traceEntry.getTime()));
			
			
			//RESPONSE
			apacheTraceModel.createResource(NameSpace.TRACEBASE+serverName+traceEntry.getTraceID())
			.addProperty(apacheTraceModel.createProperty(NameSpace.TRACE, "hasResponse"), 
						 				renderResponse(traceEntry.getResponse(), traceEntry.getResponseSize())
//			.addProperty(apacheTraceModel.createProperty(NameSpace.TRACE, "hasSizeInBytes"), 
//	 				renderSize(traceEntry.getResponse())))
	 		.addProperty(RDF.type,apacheTraceModel.createResource(NameSpace.TRACE+"HTTPResponse")));
			
					
			//ACTOR AGENT SETTING
			apacheTraceModel.createResource(NameSpace.TRACEBASE+serverName+traceEntry.getTraceID())
			.addProperty(apacheTraceModel.createProperty(NameSpace.TRACE, "hasSetting"), 
						 				renderActorAgentSetting(traceEntry.getActorAgentSetting()));
			
			//FOLLOW TRACE
			if (traceEntry.getFollowTrace() != null)
			{
				apacheTraceModel.createResource(NameSpace.TRACEBASE+serverName+traceEntry.getTraceID())
				.addProperty(apacheTraceModel.createProperty(NameSpace.TRACE, "followTrace"), 
							 				renderFollowTrace(traceEntry.getFollowTrace()));
			}			
			count++;
			itrTraceVector.remove();
			if (count >= 300000)
			{
				writeTheModel(dateToRenderLogFor);
			}
			else if (count <= 300000 && traceVector.size() == 0)
			{
				writeTheModel(dateToRenderLogFor);
			}
		}
		
		
	}

	private void writeTheModel(Date dateToRenderLogFor) {
		//FINAL MODEL
		apacheTraceModel.setNsPrefix("trace", NameSpace.TRACE);
		apacheTraceModel.setNsPrefix("traceactor", NameSpace.TRACEACTOR);
		apacheTraceModel.setNsPrefix("sitemap", NameSpace.SITEMAP);
		apacheTraceModel.setNsPrefix("rdf", NameSpace.RDF);
		
        String fileName = ("parsedLog.rdf");
        OutputStream out = null;
        File tempFile = new File("data/" + fileName);
        try {
			out = new FileOutputStream(tempFile);				
			apacheTraceModel.write(out, "RDF/XML");	            
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally {
			try {
				out.flush();
				out.close();				
				
				//UCIADRepositoryManager.add("data/" + fileName, System.getProperty("context"));
				apacheTraceModel.close();
				//System.out.println(count+" Log entries have been rendered in RDF in the UCIAD repository.");
                                if (count > 1)
                                {
                                    System.out.println(count+" Log entries have been parsed into RDF...");
                                    DataCompressor.zip("parsedLog.rdf", System.getProperty("zippedFileOutPutDir")+
                                        "/parsedLog_"+dateFormatForParsedZipFile.format(dateToRenderLogFor)+".zip");
                                }
                                else
                                {
                                    System.out.println("No entries have been found to parse into RDF...");
                                }
				
				//DataCompressor.unZip("data/parsedLog.zip");
				createNewModel = true;
				count = 1;
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}

	@Override
	public Resource renderActor(Actor actor) {
		return null;
	}

	@Override
	public Resource renderActorAgentSetting(ActorAgentSetting actorAgentSetting) {
		Resource resource = null;
		try {
			
			resource = apacheTraceModel.createResource(NameSpace.ACTORSETTINGBASE+MD5Generator.getMD5(actorAgentSetting.getIP()+actorAgentSetting.getActorAgent().getActorAgentId()))
				.addProperty(apacheTraceModel.createProperty(NameSpace.TRACEACTOR, "hasAgent"), 
																renderActorAgent(traceEntry.getActorAgentSetting().getActorAgent()));
			
			resource.addProperty(apacheTraceModel.createProperty(NameSpace.TRACEACTOR, "fromComputer"),	renderComputer(actorAgentSetting));
			
			resource.addProperty(RDF.type, apacheTraceModel.createResource(NameSpace.TRACEACTOR+"ActorSetting"));

//			resource.addProperty(apacheTraceModel.createProperty(NameSpace.TRACEACTOR, "hasIPAddress"), 
//					apacheTraceModel.createTypedLiteral(actorAgentSetting.getIP()));
//			
//			resource.addProperty(RDF.type, NameSpace.TRACEACTOR+"ActorSetting");

//			resource.addProperty(apacheTraceModel.createProperty(NameSpace.TRACEACTOR, "fromComputer"), 
//					apacheTraceModel.createResource(NameSpace.TRACEACTOR+actorAgentSetting.getIP())
//						.addProperty(RDF.type, NameSpace.TRACEACTOR+"ActorSetting"));
			
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resource;
	}

	@Override
	public Resource renderActorAgent(ActorAgent actorAgent) {
		Resource resource = null;
		try {
			resource = apacheTraceModel.createResource(NameSpace.TRACEACTORBASE+MD5Generator.getMD5(actorAgent.getActorAgentId()))
											.addProperty(apacheTraceModel.createProperty(NameSpace.TRACEACTOR,"agentId"), 
														 apacheTraceModel.createTypedLiteral(actorAgent.getActorAgentId()));
			resource.addProperty(RDF.type, apacheTraceModel.createResource(NameSpace.TRACEACTOR+"ActorAgent"));
			//System.out.println("actorAgent: "+resource.toString());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resource;
	}
	
	@Override
	public Resource renderComputer(ActorAgentSetting actorAgentSetting) {
		Resource resource = null;
		try {
			resource = apacheTraceModel.createResource(NameSpace.COMPUTERBASE+MD5Generator.getMD5(actorAgentSetting.getIP()))
											.addProperty(apacheTraceModel.createProperty(NameSpace.TRACEACTOR, "hasIPAddress"),
														 apacheTraceModel.createTypedLiteral(actorAgentSetting.getIP()));
			
			resource.addProperty(RDF.type, apacheTraceModel.createResource(NameSpace.TRACEACTOR+"Computer"));
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	    return resource;
	}

	@Override
	public List<Resource> renderParameterValue(ParameterValue parameterValue) {
		Resource resource = null;		 
		List<Resource> resources = new ArrayList<Resource>();
		Map<String, String> paramValueMap = parameterValue.getParameterValueMap();
		Set<String> paramValueSKeyet = paramValueMap.keySet();
		Iterator<String> itrParamValue =  paramValueSKeyet.iterator();
		while (itrParamValue.hasNext()){
			String mapKeyParam = itrParamValue.next();
			String mapParamValue = (String)paramValueMap.get(mapKeyParam);
			try {
				//Value DP				
				resource = apacheTraceModel.createResource(NameSpace.PARAMETERVALUEBASE+MD5Generator.getMD5(mapKeyParam+mapParamValue))
									.addProperty(apacheTraceModel.createProperty(NameSpace.TRACE, "value"), 
												 apacheTraceModel.createTypedLiteral(mapParamValue));
				resources.add(resource);
				
//				//Parameter DP				
//				resource = apacheTraceModel.createResource(NameSpace.TRACE+MD5Generator.getMD5(mapKeyParam+mapParamValue))
//				.addProperty(apacheTraceModel.createProperty(NameSpace.TRACE, "parameter"), 
//							 apacheTraceModel.createTypedLiteral(mapKeyParam));
				
//				resources.add(resource);
				
				//Parameter OP
				Resource paramResource =  apacheTraceModel.createResource(NameSpace.PARAMETERBASE+MD5Generator.getMD5(mapKeyParam)).							
					addProperty(apacheTraceModel.createProperty(NameSpace.SITEMAP, "name"), 
							apacheTraceModel.createTypedLiteral(mapKeyParam));
				
				paramResource.addProperty(RDF.type, apacheTraceModel.createResource(NameSpace.SITEMAP+"Parameter"));
								
				resource = apacheTraceModel.createResource(NameSpace.PARAMETERVALUEBASE+MD5Generator.getMD5(mapKeyParam+mapParamValue))
						.addProperty(apacheTraceModel.createProperty(NameSpace.TRACE, "parameter"),paramResource);
				
				resources.add(resource);
				
				
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return resources;
		//return resourceVector.iterator();
	}

	@Override
	public Resource renderPage(Page page) {
		Resource resource = null;
		try {
			resource = apacheTraceModel.createResource(NameSpace.PAGEBASE+MD5Generator.getMD5(page.getPageURL()+page.getOnServer()))
											.addProperty(apacheTraceModel.createProperty(NameSpace.SITEMAP,"url"), 
														 apacheTraceModel.createTypedLiteral(page.getPageURL()));
			resource.addProperty(apacheTraceModel.createProperty(NameSpace.SITEMAP,"onServer"), 
                                            apacheTraceModel.createResource(page.getOnServer()));
                        resource.addProperty(RDF.type, apacheTraceModel.createResource(NameSpace.SITEMAP+"WebPage"));
                        
			
			Pattern pattern = null;
			Matcher matcher = null;
			Iterator<String[]> resultItr = resultVec.iterator();
			while(resultItr.hasNext())
			{
				String result[] = resultItr.next();
				String regularExpression = result[1].replace("\"/","^/");
				regularExpression = regularExpression.replace("*\"", "*");
				pattern = Pattern.compile(regularExpression, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
				matcher = pattern.matcher(page.getPageURL());
				
				if (matcher.find())
				{
//					System.out.println("match: "+matcher.group(0));
//					System.out.println("part of: "+result[0]);
					String webCollection = result[0];
					resource.addProperty(apacheTraceModel.createProperty(NameSpace.SITEMAP, "isPartOf"), 
											apacheTraceModel.createResource(webCollection));
//											.addProperty(apacheTraceModel.createProperty(NameSpace.SITEMAP, "urlPattern"), 
//													apacheTraceModel.createTypedLiteral(regularExpression))
//											.addProperty(RDF.type, apacheTraceModel.createResource(NameSpace.SITEMAP+"WebpageCollection")));
					
				}				
			}

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		return resource;	
	}

	private Vector<String[]> getRegularExpressions()
			throws RepositoryException, MalformedQueryException,
			QueryEvaluationException {
		String query = "SELECT * WHERE { ?site <http://uciad.info/ontology/sitemap/urlPattern> ?pattern . " +
										"?site <http://uciad.info/ontology/sitemap/onServer> " +
											  "<"+System.getProperty("serverURI")+"> }";
		
		TupleQueryResult tqr = UCIADRepositoryManager.evaluateSPARQLQuery(query);
		Vector<String[]> resultVec = new Vector<String[]>();
		while (tqr.hasNext()) {
			BindingSet bs = tqr.next();
			String[] elem = new String[2];
			elem[0] = bs.getValue("site").toString();
			elem[1] = bs.getValue("pattern").toString().substring(0,bs.getValue("pattern").toString().indexOf("^"));
			System.out.println(elem[1]);
			resultVec.add(elem);
		}
		return resultVec;
	}
	
	

	@Override
	public Resource renderAction(HTTPAction traceAction) {	
		Resource resource = null;
		resource = apacheTraceModel.createResource(NameSpace.TRACEACTIONBASE+traceAction.getHttpMethod())
						.addProperty(RDF.type, apacheTraceModel.createResource(NameSpace.TRACE+"TraceAction"));
		return resource;
	}

	@Override
	public Literal renderTime(Date traceTime) {
		Literal literal = null;
//		Resource resource = null;
//		try {
			DateFormat dateFormt = ApacheLogParser.getApacheLogParserDateFormat();
//			resource = apacheTraceModel.createResource(NameSpace.TRACE+MD5Generator.getMD5(dateFormt.format(traceTime.getTime())))
//											.addProperty(apacheTraceModel.createProperty(NameSpace.TRACE,"time"), 
//														 apacheTraceModel.createTypedLiteral(dateFormt.format(traceTime.getTime())));
			literal = apacheTraceModel.createTypedLiteral(dateFormt.format(traceTime.getTime()));
			//System.out.println("time: "+resource.toString());
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return literal;
	}

	@Override
	public Resource renderResponse(String traceResponse, int responseSize) {
		Resource resource = null;
		try {
			resource = apacheTraceModel.createResource(NameSpace.TRACERESPONSEBASE+MD5Generator.getMD5(traceResponse+responseSize))
											.addProperty(apacheTraceModel.createProperty(NameSpace.TRACE,"hasResponseCode"), 
														 apacheTraceModel.createResource(NameSpace.TRACE+traceResponse).addProperty(RDF.type, 
														 apacheTraceModel.createResource(NameSpace.TRACE+"HTTPResponseCode")))
											.addProperty(apacheTraceModel.createProperty(NameSpace.TRACE,"hasSizeInBytes"),
													     apacheTraceModel.createTypedLiteral(responseSize));
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return resource;
	}

	@Override
	public Literal renderFollowTrace(String followTrace) {
		Literal literal = null;
		literal = apacheTraceModel.createTypedLiteral(followTrace);	
		return literal;
	}	

}
