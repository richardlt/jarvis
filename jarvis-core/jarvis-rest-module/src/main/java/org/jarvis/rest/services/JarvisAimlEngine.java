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

package org.jarvis.rest.services;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jarvis.client.model.JarvisDatagram;
import org.jarvis.client.model.JarvisDatagramEvent;
import org.jarvis.main.core.IJarvisCoreSystem;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.main.core.impl.JarvisCoreSystemImpl;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.jarvis.rest.services.impl.JarvisModuleException;
import org.jarvis.rest.services.impl.JarvisRestClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JarvisAimlEngine extends JarvisRestClientImpl implements IJarvisRestClient {

	protected Logger logger = LoggerFactory.getLogger(JarvisAimlEngine.class);

	IJarvisCoreSystem jarvis;

	private boolean voice;

	/**
	 * constructor
	 * 
	 * @param hostName
	 * @param portNumber
	 */
	@PostConstruct
	public void init() {
		super.init(CoreRestServices.Handler.aiml.name(), "jarvis-aiml-engine-v1.0b");

		this.voice = Boolean.parseBoolean(env.getProperty("jarvis.aiml.voice"));

		setRenderer(true);
		setSensor(true);
		setCanAnswer(true);

		/**
		 * implement jarvis system
		 */
		jarvis = new JarvisCoreSystemImpl();
	}

	/**
	 * internal init method
	 * 
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws AimlParsingError
	 * @throws IOException
	 */
	@PostConstruct
	private void initialize() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, AimlParsingError, IOException {
		System.setProperty("file.encoding", "UTF-8");
		java.lang.reflect.Field charset = Charset.class.getDeclaredField("defaultCharset");
		charset.setAccessible(true);
		charset.set(null, null);
		logger.info("Default encoding: {}", Charset.defaultCharset().displayName());
		logger.info("Initializing ...");
		jarvis.initialize("baymax", "baymax.txt");
		logger.info("Ready ...");
	}

	@Override
	public JarvisDatagram onNewMessage(JarvisDatagram message) throws JarvisModuleException {
		JarvisDatagram nextMessage = new JarvisDatagram();

		try {
			/**
			 * aiml render
			 */
			List<IAimlHistory> result = jarvis.askSilent(message.request.getData());
			for (IAimlHistory value : result) {
				/**
				 * on event per answer, for plugin mecanism
				 */
				nextMessage.setCode("event");
				nextMessage.event = new JarvisDatagramEvent();
				nextMessage.event.setData(value.getAnswer());
				nextMessage.event.setScript(value.getJavascript());
				nextMessage.event.setFrom(message.request.getTo());
				nextMessage.event.setTo(message.request.getFrom());
			}
			/**
			 * render to local default output
			 */
			logger.warn("Voice status {}", voice);
			if (voice) {
				for (IAimlHistory value : result) {
					jarvis.speak(value.getAnswer());
				}
			}

			return nextMessage;
		} catch (AimlParsingError e) {
			logger.error("Error, while accessing to jarvis with {}", message.request.getData());
			throw new JarvisModuleException(e);
		} catch (IOException e) {
			logger.error("Error, while accessing to jarvis with {}", message.request.getData());
			throw new JarvisModuleException(e);
		}
	}
}
