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

package org.jarvis.core.resources.api.plugins;

import java.io.IOException;
import java.util.Map.Entry;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.device.DeviceBean;
import org.jarvis.core.model.bean.plugin.CommandBean;
import org.jarvis.core.model.bean.plugin.ScriptPluginBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.plugin.CommandRest;
import org.jarvis.core.model.rest.plugin.ScriptPluginRest;
import org.jarvis.core.resources.api.ApiLinkedResources;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.href.ApiHrefPluginCommandResources;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * script plugin resource
 */
@Component
public class ApiScriptPluginResources extends ApiLinkedResources<ScriptPluginRest,ScriptPluginBean,CommandRest,CommandBean> {
	protected Logger logger = LoggerFactory.getLogger(ApiScriptPluginResources.class);
	
	@Autowired
	ApiCommandResources apiCommandResources;

	@Autowired
	ApiHrefPluginCommandResources apiHrefPluginCommandResources;

	/**
	 * constructor
	 */
	public ApiScriptPluginResources() {
		setRestClass(ScriptPluginRest.class);
		setBeanClass(ScriptPluginBean.class);
	}
	
	@Override
	public void mount() {
		/**
		 * scripts
		 */
		declare(SCRIPT_RESOURCE);
		/**
		 * scripts -> commands
		 */
		declare(SCRIPT_RESOURCE, COMMAND_RESOURCE, apiCommandResources, apiHrefPluginCommandResources, COMMAND, SORTKEY, HREF);
	}

	/**
	 * all script have two phase : a render (data) phase and an execute (action) phase
	 */
	@Override
	public GenericValue doRealTask(ScriptPluginBean bean, GenericMap args, TaskType taskType) throws TechnicalException {
		GenericMap result;
		switch(taskType) {
			case RENDER:
				try {
					result = renderOrExecute(null, bean, args, new GenericMap(), true);
				} catch (TechnicalNotFoundException e) {
					logger.error("Error {}", e);
					throw new TechnicalException(e);
				}
				break;
			case EXECUTE:
				try {
					result = renderOrExecute(null, bean, args, new GenericMap(), false);
				} catch (TechnicalNotFoundException e) {
					logger.error("Error {}", e);
					throw new TechnicalException(e);
				}
				break;
			default:
				result = new GenericMap();
		}
		return new GenericValue(result);
	}

	/**
	 * render all data command of this script as a pipeline
	 * @param device 
	 * @param plugin
	 * 		the script to render
	 * @param args
	 * 		arguments for this script
	 * @return GenericMap
	 * 		a result as a generic map
	 * @throws TechnicalNotFoundException
	 * 		if not found 
	 */
	public GenericMap render(DeviceBean device, ScriptPluginBean plugin, GenericMap args) throws TechnicalNotFoundException {
		return renderOrExecute(device, plugin, args, new GenericMap(), true);
	}

	/**
	 * execute all action command of this script as a pipeline
	 * @param device 
	 * @param plugin
	 * 		the script to render
	 * @param args
	 * 		arguments for this script
	 * @return GenericMap
	 * 		a result as a generic map
	 * @throws TechnicalNotFoundException
	 * 		if not found 
	 */
	public GenericMap execute(DeviceBean device, ScriptPluginBean plugin, GenericMap args) throws TechnicalNotFoundException {
		return renderOrExecute(device, plugin, args, new GenericMap(), false);
	}

	/**
	 * execute all command of this script as a pipeline
	 * @param script
	 * @param args 
	 * @param output 
	 * @return GenericMap
	 * @throws TechnicalNotFoundException 
	 */
	private GenericMap renderOrExecute(DeviceBean device, ScriptPluginBean plugin, GenericMap args, GenericMap output, boolean render) throws TechnicalNotFoundException {
		GenericMap result = args;
		int index = 0;
		for(GenericEntity entity : sort(apiHrefPluginCommandResources.findAll(plugin), "order")) {
			/**
			 * ignore data in phase action
			 */
			if(entity.get("type") != null && entity.get("type").equals("data") && !render) {
				logger.warn("Plugin {} cannot be executed, its a data");
				continue;
			}
			/**
			 * ignore action in phase data
			 */
			if(entity.get("type") != null && entity.get("type").equals("action") && render) {
				logger.warn("Plugin {} cannot be rendered, its ans action");
				continue;
			}
			
			/**
			 * extract parameter
			 */
			if(entity.get("parameter") != null) {
				try {
					GenericMap map = mapper.readValue(entity.get("parameter").toString(), GenericMap.class);
					for(Entry<String, Object> field : map.entrySet()) {
						result.put(field.getKey(), field.getValue());
					}
				} catch (IOException e) {
					logger.warn("While decoding {}", entity.get("parameter").toString());
				}
			}
			
			/**
			 * retrieve command to execute
			 */
			CommandRest command = apiCommandResources.doGetByIdRest(entity.id);
			logger.info("Before render params = {}, context = {}", command, result);
			result = apiCommandResources.execute(device, plugin, mapperFactory.getMapperFacade().map(command, CommandBean.class), result);
			logger.info("After render params = {}, context = {}", command, result);

			/**
			 * store result in output
			 */
			if(entity.get("name") != null) {
				output.put((String) entity.get("name"), result);
			} else {
				output.put("field"+(index++), result);
			}
		}
		return output;
	}
}
