package uk.ac.open.kmi.uciad.helper;

import java.util.Date;

public class Trace {
	private String traceID;
	private Date time;
	private ActorAgentSetting actoAgentsetting;
	private Page pageInvolved;
	private ParameterValue parameterValues;
	private HTTPAction action;
	private String response;
	private int responseSize;
	private String followTrace;
	
	/**
	 * @param traceID the traceID to set
	 */
	public void setTraceID(String traceID) {
		this.traceID = traceID;
	}
	/**
	 * @return the traceID
	 */
	public String getTraceID() {
		return traceID;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
	}
	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}
	
	/**
	 * @param actoAgentsetting the actoAgentsetting to set
	 */
	public void setActorAgentSetting(ActorAgentSetting actoAgentsetting) {
		this.actoAgentsetting = actoAgentsetting;
	}
	/**
	 * @return the actoAgentsetting
	 */
	public ActorAgentSetting getActorAgentSetting() {
		return actoAgentsetting;
	}
	/**
	 * @param pageInvolved the pageInvolved to set
	 */
	public void setPageInvolved(Page pageInvolved) {
		this.pageInvolved = pageInvolved;
	}
	/**
	 * @return the pageInvolved
	 */
	public Page getPageInvolved() {
		return pageInvolved;
	}
	/**
	 * @param parameterValues the parameterValues to set
	 */
	public void setParameterValues(ParameterValue parameterValues) {
		this.parameterValues = parameterValues;
	}
	/**
	 * @return the parameterValues
	 */
	public ParameterValue getParameterValues() {
		return parameterValues;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(HTTPAction action) {
		this.action = action;
	}
	/**
	 * @return the action
	 */
	public HTTPAction getAction() {
		return action;
	}
	/**
	 * @param response the response to set
	 */
	public void setResponse(String response) {
		this.response = response;
	}
	/**
	 * @return the response
	 */
	public String getResponse() {
		return response;
	}
	/**
	 * @param followTrace the followTracec to set
	 */
	public void setFollowTrace(String followTrace) {
		this.followTrace = followTrace;
	}
	/**
	 * @return the followTrace
	 */
	public String getFollowTrace() {
		return followTrace;
	}
	/**
	 * @param responseSize the responseSize to set
	 */
	public void setResponseSize(int responseSize) {
		this.responseSize = responseSize;
	}
	/**
	 * @return the responseSize
	 */
	public int getResponseSize() {
		return responseSize;
	}	
	
}
