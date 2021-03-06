package org.jarvis.core.model.bean.scenario;

import org.jarvis.core.model.bean.GenericBean;

/**
 * block
 */
public class BlockBean extends GenericBean {
	/**
	 * name
	 */
	public String name;
	/**
	 * icon
	 */
	public String icon;
	/**
	 * testParameter
	 */
	public String testParameter;
	/**
	 * thenParameter
	 */
	public String thenParameter;
	/**
	 * elseParameter
	 */
	public String elseParameter;
	/**
	 * expression
	 */
	public String expression;

	@Override
	public String toString() {
		return "BlockBean [name=" + name + ", icon=" + icon + ", testParameter=" + testParameter + ", thenParameter="
				+ thenParameter + ", elseParameter=" + elseParameter + ", expression=" + expression + "]";
	}
}
