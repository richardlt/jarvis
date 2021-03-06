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

package org.jarvis.core.resources.api.scenario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.scenario.BlockBean;
import org.jarvis.core.model.bean.scenario.ScenarioBean;
import org.jarvis.core.model.bean.scenario.TriggerBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.scenario.BlockRest;
import org.jarvis.core.model.rest.scenario.ScenarioRest;
import org.jarvis.core.model.rest.scenario.TriggerRest;
import org.jarvis.core.profiler.DefaultProcessService;
import org.jarvis.core.profiler.model.DefaultProcess;
import org.jarvis.core.resources.api.ApiLinkedTwiceResources;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.device.ApiTriggerResources;
import org.jarvis.core.resources.api.href.ApiHrefScenarioBlockResources;
import org.jarvis.core.resources.api.href.ApiHrefScenarioTriggerResources;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Scenario resource
 *
 */
@Component
public class ApiScenarioResources extends ApiLinkedTwiceResources<ScenarioRest,ScenarioBean,BlockRest,BlockBean,TriggerRest,TriggerBean> {

	@Autowired
	ApiBlockResources apiBlockResources;

	@Autowired
	ApiHrefScenarioBlockResources apiHrefScenarioBlockResources;

	@Autowired
	ApiTriggerResources apiTriggerResources;

	@Autowired
	ApiHrefScenarioTriggerResources apiHrefScenarioTriggerResources;

	/**
	 * constructor
	 */
	public ApiScenarioResources() {
		setRestClass(ScenarioRest.class);
		setBeanClass(ScenarioBean.class);
	}

	/**
	 * mount resources
	 */
	@Override
	public void mount() {
		/**
		 * scenarios
		 */
		declare(SCENARIO_RESOURCE);
		declareSecond(SCENARIO_RESOURCE, TRIGGER_RESOURCE, apiTriggerResources, apiHrefScenarioTriggerResources, TRIGGER, SORTKEY, HREF);
		spark.Spark.get("/api/scenarios/:id/graph", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doExecute(
		    			response,
	    				request.params(ID),
	    				(GenericMap) mapper.readValue("{}",GenericMap.class),
	    				TaskType.RENDER);
		    }
		});
		/**
		 * scenarios -> blocks
		 */
		declare(SCENARIO_RESOURCE, BLOCK_RESOURCE, apiBlockResources, apiHrefScenarioBlockResources, BLOCK, SORTKEY, HREF);
	}

	@Override
	public GenericValue doRealTask(ScenarioBean bean, GenericMap args, TaskType taskType) throws TechnicalException {
		switch(taskType) {
			case EXECUTE:
				try {
					return new GenericValue(execute(bean, args, new GenericMap()));
				} catch (TechnicalNotFoundException e) {
					logger.error("Error {}", e);
					throw new TechnicalException(e);
				}
			case RENDER:
				try {
					return new GenericValue(render(bean, args, new GenericMap()));
				} catch (TechnicalNotFoundException e) {
					logger.error("Error {}", e);
					throw new TechnicalException(e);
				}
			default:
				return new GenericValue(new GenericMap());
		}
	}

	/**
	 * execute this scenario
	 * @param bean
	 * @param args
	 * @param genericMap
	 * @return
	 * @throws TechnicalNotFoundException
	 */
	private String execute(ScenarioBean bean, GenericMap args, GenericMap genericMap) throws TechnicalNotFoundException {
		List<String> console = new ArrayList<String>();
		for(GenericEntity entity : sort(apiHrefScenarioBlockResources.findAll(bean), "order")) {
			BlockBean block = apiBlockResources.doGetByIdBean(entity.id);
			GenericMap testParameter = null;
			try {
				if(block.testParameter != null) {
					testParameter = mapper.readValue(block.testParameter, GenericMap.class);
				} else {
					testParameter = new GenericMap();
				}
			} catch (IOException e) {
				throw new TechnicalException(e);
			}
			apiBlockResources.execute(console, 0, block, testParameter);
		}
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(console);
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
	}

	/**
	 * render this level
	 * @param stage
	 * @param entities
	 * @param processes
	 * @return
	 * @throws TechnicalNotFoundException
	 */
	private HashMap<String, GenericEntity> renderLevel(String stage, Collection<GenericEntity> entities, List<DefaultProcess> processes) throws TechnicalNotFoundException {
		HashMap<String, GenericEntity> calls = new HashMap<String, GenericEntity>();
		/**
		 * iterate on block entities
		 */
		for(GenericEntity blockEntity : entities) {
			DefaultProcess process = new DefaultProcess(stage);
			processes.add(process);
			BlockBean block = apiBlockResources.doGetByIdBean(blockEntity.id);
			apiBlockResources.render(process, block, calls);
		}
		return calls;
	}

	/**
	 * render processes
	 * @param bean
	 * @param args
	 * @param genericMap
	 * @return
	 * @throws TechnicalNotFoundException
	 */
	private String render(ScenarioBean bean, GenericMap args, GenericMap genericMap) throws TechnicalNotFoundException {
		/**
		 * compute first level, return is the sub block of n-1 level
		 */
		List<DefaultProcess> processes = new ArrayList<DefaultProcess>();
		Map<String, GenericEntity> calls = renderLevel("main", sort(apiHrefScenarioBlockResources.findAll(bean), "order"), processes);
		int level = 1;
		Set<String> index = new HashSet<String>();
		while(calls.size()>0) {
			Map<String, GenericEntity> toRun = new HashMap<String, GenericEntity>();
			for(Entry<String, GenericEntity> entity : calls.entrySet()) {
				if(!index.contains(entity.getKey())) {
					index.add(entity.getKey());
					toRun.put(entity.getKey(), entity.getValue());
				}
			}
			/**
			 * render not known block
			 */
			calls = renderLevel("level#" + level, toRun.values(), processes);
			level ++;
			if(level > 10) {
				logger.warn("To many levels {}", bean.id);
				break;
			}
		}
		return DefaultProcessService.toJson(processes);
	}
}
