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
package org.jarvis.main.model.impl.parser.template;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.template.IAimlTopicStar;

public class AimlTopicStarImpl extends AimlElementContainer implements
		IAimlTopicStar {

	public AimlTopicStarImpl() {
		super("topicstar");
	}

	private String	index;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	@Override
	public void add(IAimlProperty value) {
		if (value.getKey().compareTo("index") == 0) index = accept(value);
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlThatStar [elements=" + elements + "]";
	}
}
