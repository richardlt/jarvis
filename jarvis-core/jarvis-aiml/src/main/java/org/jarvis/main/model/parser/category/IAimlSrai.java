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

/**
 * The srai element instructs the AIML interpreter to pass the result of
 * processing the contents of the srai element to the AIML matching loop, as if
 * the input had been produced by the user (this includes stepping through the
 * entire input normalization process). The srai element does not have any
 * attributes. It may contain any AIML template elements.
 * 
 * As with all AIML elements, nested forms should be parsed from inside out, so
 * embedded srais are perfectly acceptable.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:srai>
 * 
 * <!-- Contents: aiml-template-elements -->
 * 
 * </aiml:srai>
 */
public interface IAimlSrai extends IAimlElement {

}
