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

package org.jarvis.core.exception;

/**
 * http exception
 */
public class TechnicalHttpException extends Exception {

	/**
	 * @param statusCode
	 * @param message
	 */
	public TechnicalHttpException(int statusCode, String message) {
		super(message + ":" + statusCode);
	}

	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 8232350623071221905L;

}
