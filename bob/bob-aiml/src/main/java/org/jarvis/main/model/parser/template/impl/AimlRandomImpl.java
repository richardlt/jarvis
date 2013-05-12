package org.jarvis.main.model.parser.template.impl;

import java.util.List;
import java.util.Random;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.template.IAimlRandom;

public class AimlRandomImpl extends AimlElementContainer implements IAimlRandom {

	private static Random	randomGenerator	= new Random();

	public AimlRandomImpl() {
		super("random");
	}

	@Override
	public void add(String value) {
		/**
		 * The random element must contain one or more li elements of type
		 * defaultListItem, and cannot contain any other elements.
		 * 
		 * PCDATA is ignored
		 */
	}

	@Override
	public StringBuilder answer(IAimlCoreEngine engine, List<String> star,
			String that, StringBuilder render) throws AimlParsingError {
		/**
		 * randomize li selection and render it
		 */
		int li = randomGenerator.nextInt(1000000) % (getElements().size());
		return getElements().get(li).answer(engine, star, that, render);
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlRandom [elements=" + elements + "]";
	}
}
