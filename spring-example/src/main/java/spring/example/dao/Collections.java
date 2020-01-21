package spring.example.dao;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Collections {
	/** 注入List集合 **/
	private List<String> listNames;

	/***注入Set集合*/
	private Set<String> setNames;

	/** 注入Properties **/
	private Properties propertiesNames;

	/** 注入Map集合 **/
	private Map<String, String> mapNames;

	/** 注入数组 **/
	private String[] arrayNames;

	public List<String> getListNames() {
		return listNames;
	}

	public void setListNames(List<String> listNames) {
		this.listNames = listNames;
	}

	public Set<String> getSetNames() {
		return setNames;
	}

	public void setSetNames(Set<String> setNames) {
		this.setNames = setNames;
	}

	public Properties getPropertiesNames() {
		return propertiesNames;
	}

	public void setPropertiesNames(Properties propertiesNames) {
		this.propertiesNames = propertiesNames;
	}

	public Map<String, String> getMapNames() {
		return mapNames;
	}

	public void setMapNames(Map<String, String> mapNames) {
		this.mapNames = mapNames;
	}

	public String[] getArrayNames() {
		return arrayNames;
	}

	public void setArrayNames(String[] arrayNames) {
		this.arrayNames = arrayNames;
	}
}
