package org.jarvis.main.model.parser.template.system.impl;

import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.template.system.IAimlVersion;

public class AimlVersionImpl extends AimlElementContainer implements IAimlVersion {

	public AimlVersionImpl() {
		super("version");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlVersion [elements=" + elements + "]";
	}
}