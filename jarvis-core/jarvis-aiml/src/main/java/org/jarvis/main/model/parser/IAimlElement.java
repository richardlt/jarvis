package org.jarvis.main.model.parser;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.jarvis.main.model.transform.ITransformedItem;

public interface IAimlElement extends IAimlRender {
	public void add(String value);

	public void add(IAimlElement value);

	public void add(IAimlProperty value);

	public String get(String key);

	public List<IAimlElement> getElements();

	/**
	 * get untransformed PCDATA element
	 * 
	 * @return
	 * @throws AimlParsingError
	 */
	public List<ITransformedItem> getTransforms(IAimlTopic topic)
			throws AimlParsingError;

	/**
	 * compute an answer
	 * 
	 * @param engine
	 * @param star
	 * @param that
	 * @param render
	 * @throws AimlParsingError
	 */
	public IAimlResult answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, IAimlResult render) throws AimlParsingError;

	@Override
	public StringBuilder toAiml(StringBuilder render);

	/**
	 * accept this property
	 * 
	 * @param e
	 * @return
	 */
	String accept(IAimlProperty e);
}
