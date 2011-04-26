package uk.ac.open.kmi.uciad.helper;

import java.util.HashMap;
import java.util.Map;

public class ParameterValue {
	private Map<String, String> parameterValueList = new HashMap<String, String>();

	/**
	 * @param parameterValueList the parameterValueList to set
	 */
	public void setParameterValueMap(Map<String, String> parameterValueList) {
		this.parameterValueList = parameterValueList;
	}

	/**
	 * @return the parameterValueList
	 */
	public Map<String, String> getParameterValueMap() {
		return parameterValueList;
	}
	
	
}
