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

package org.jarvis.main.model.impl.parser;

import java.util.ArrayList;
import java.util.List;

import org.jarvis.main.model.parser.IAimlCategory;
import org.jarvis.main.model.parser.IAimlTopic;

public class AimlTopic extends AimlElementContainer implements IAimlTopic {

	/**
	 * A topic is an optional top-level element that contains category elements.
	 * A topic element has a required name attribute that must contain a simple
	 * pattern expression. A topic element may contain one or more category
	 * elements.
	 */
	public AimlTopic() {
		super("topic");
	}

	private List<IAimlCategory> categories = new ArrayList<IAimlCategory>();

	@Override
	public void addCategory(IAimlCategory e) {
		categories.add(e);
	}

	@Override
	public String toString() {
		return "\n\t\tAimlTopic [categories=" + categories + "]";
	}
}