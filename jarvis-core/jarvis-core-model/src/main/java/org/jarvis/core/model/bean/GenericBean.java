/**
 *  Copyright 2015 Yannick Roffin
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

package org.jarvis.core.model.bean;

import org.joda.time.DateTime;

/**
 * generic bean
 */
public class GenericBean {
	/**
	 * generic id
	 */
	public String id;
	/**
	 * resource trigger
	 */
	public String trigger;
	/**
	 * timestamp
	 */
	public DateTime timestamp;
	/**
	 * generic href
	 */
	public String href;
	/**
	 * false when no security is used
	 * true in other way (if true no data backup can be done except field name in {id, key})
	 */
	public boolean isSecured;
}
