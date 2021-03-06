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
package org.jarvis.main.model.parser.category;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.IAimlTopic;

/**
 * This element appear in different context
 * 
 * The template-side that element indicates that an AIML interpreter should
 * substitute the contents of a previous bot output.
 * 
 * The template-side that has an optional index attribute that may contain
 * either a single integer or a comma-separated pair of integers. The minimum
 * value for either of the integers in the index is "1". The index tells the
 * AIML interpreter which previous bot output should be returned (first
 * dimension), and optionally which "sentence" (see [8.3.2.]) of the previous
 * bot output (second dimension).
 * 
 * The AIML interpreter should raise an error if either of the specified index
 * dimensions is invalid at run-time.
 * 
 * An unspecified index is the equivalent of "1,1". An unspecified second
 * dimension of the index is the equivalent of specifying a "1" for the second
 * dimension.
 * 
 * The template-side that element does not have any content.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:that index = (single-integer-index | comma-separated-integer-pair) />
 * 
 * The pattern-side that element is a special type of pattern element used for
 * context matching. The pattern-side that is optional in a category, but if it
 * occurs it must occur no more than once, and must immediately follow the
 * pattern and immediately precede the template. A pattern-side that element
 * contains a simple pattern expression.
 * 
 * The contents of the pattern-side that are appended to the full match path
 * that is constructed by the AIML interpreter at load time, as described in
 * [8.2].
 * 
 * If a category does not contain a pattern-side that, the AIML interpreter must
 * assume an "implied" pattern-side that containing the pattern expression *
 * (single asterisk wildcard).
 * 
 * A pattern-side that element has no attributes.
 * 
 * <!-- Category: aiml-category-elements -->
 * 
 * <aiml:that>
 * 
 * <!-- Content: aiml-pattern-expression -->
 * 
 * </aiml:that>
 */
public interface IAimlThat extends IAimlElement {
	public IAimlTopic getTopic();
}
