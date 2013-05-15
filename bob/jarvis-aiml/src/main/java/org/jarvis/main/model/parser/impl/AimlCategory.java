/**
 *  Copyright 2012 Yannick Roffin
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.jarvis.main.model.parser.impl;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.IAimlCategory;
import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.category.IAimlPattern;
import org.jarvis.main.model.parser.category.IAimlTemplate;
import org.jarvis.main.model.parser.category.IAimlThat;
import org.jarvis.main.model.transform.ITransformedItem;

public class AimlCategory extends AimlElementContainer implements IAimlCategory {

	/**
	 * A category is a top-level (or second-level, if contained within a topic)
	 * element that contains exactly one pattern and exactly one template. A
	 * category does not have any attributes.
	 */
	public AimlCategory() {
		super("category");
	}

	private IAimlTemplate		template;
	private IAimlPattern		pattern;
	private IAimlThat			that;

	/**
	 * optimisation
	 */
	private ITransformedItem	transPattern;
	private ITransformedItem	transThat;

	@Override
	public IAimlPattern getPattern() {
		return pattern;
	}

	@Override
	public void setTemplate(IAimlTemplate e) {
		template = e;
	}

	@Override
	public void setPattern(IAimlPattern e) {
		pattern = e;
	}

	@Override
	public void setThat(IAimlThat e) {
		that = e;
	}

	@Override
	public IAimlThat getThat() {
		return that;
	}

	@Override
	public boolean hasThat() {
		return that != null;
	}

	@Override
	public void setTransformedPattern(ITransformedItem transform) {
		transPattern = transform;
	}

	@Override
	public ITransformedItem getTransformedPattern() {
		return transPattern;
	}

	@Override
	public void setTransformedThat(ITransformedItem transform) {
		transThat = transform;
	}

	@Override
	public ITransformedItem getTransformedThat() {
		return transThat;
	}

	@Override
	public StringBuilder answer(IAimlCoreEngine engine, List<String> star,
			String that, StringBuilder render) throws AimlParsingError {
		for (IAimlElement element : template.getElements()) {
			element.answer(engine, star, that, render);
		}
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\tAimlCategory [template=" + template + ", pattern="
				+ pattern + ", elements=" + elements + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if (elements.size() == 0) {
			render.append("<" + tag);
			properties(render);
			render.append("/>\n");
		} else {
			render.append("<" + tag);
			properties(render);
			render.append(">");
			for (IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</" + tag + ">\n");
		}
		return render;
	}
}