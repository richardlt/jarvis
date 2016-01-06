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

package org.jarvis.core.resources.api.iot;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.IotBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.IotRest;
import org.jarvis.core.model.rest.plugin.ScriptPluginRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.href.ApiHrefIotResources;
import org.jarvis.core.resources.api.href.ApiHrefIotScriptPluginResources;
import org.jarvis.core.resources.api.plugins.ApiScriptPluginResources;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * IOT resource
 */
@Component
public class ApiIotResources extends ApiResources<IotRest,IotBean> {

	@Autowired
	ApiHrefIotScriptPluginResources apiHrefIotScriptPluginResources;

	@Autowired
	ApiHrefIotResources apiHrefIotResources;

	@Autowired
	ApiScriptPluginResources apiScriptPluginResources;

	/**
	 * constructor
	 */
	public ApiIotResources() {
		setRestClass(IotRest.class);
		setBeanClass(IotBean.class);
	}

	@Override
	public String doRealTask(IotBean iot, GenericMap args, TaskType taskType) throws Exception {
		GenericMap result;
		switch(taskType) {
			case RENDER:
				result = render(iot, args);
				break;
			default:
				result = new GenericMap();
		}
		return mapper.writeValueAsString(result);
	}

	/**
	 * render this connected object
	 * @param iot
	 * @param args
	 * @return GenericMap
	 * @throws Exception
	 */
	public GenericMap render(IotBean iot, GenericMap args) throws Exception {
		GenericMap result = args;
		IotRest iotRest = mapperFactory.getMapperFacade().map(iot, IotRest.class);
		/**
		 * iterate on each entity and execute them as a pipeline
		 */
		for(GenericEntity entity : sort(apiHrefIotScriptPluginResources.findAll(iotRest))) {
			ScriptPluginRest script = apiScriptPluginResources.doGetById(entity.id);
			result = apiScriptPluginResources.execute(script, result);
		}
		return result;
	}

	@Override
	public void mount() {
		/**
		 * mount resources
		 */
		get("/api/iots", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doFindAll(request, response);
		    }
		});
		get("/api/iots/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doGetById(request, ID, response);
		    }
		});
		post("/api/iots", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doCreate(request, response, IotRest.class);
		    }
		});
		post("/api/iots/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doTask(request, ":id", "task", response, IotRest.class);
		    }
		});
		put("/api/iots/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doUpdate(request, ID, response, IotRest.class);
		    }
		});
		delete("/api/iots/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doDelete(request, ID, response, IotRest.class);
		    }
		});
		/**
		 * iots
		 */
		get("/api/iots/:id/iots", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		IotRest iot = doGetById(request.params(ID));
			    	return mapper.writeValueAsString(apiHrefIotResources.findAll(iot));
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    }
		});
		put("/api/iots/:id/iots/:iot", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		IotRest iot = doGetById(request.params(ID));
			    	try {
				    	IotRest child = doGetById(request.params(IOT));
				    	GenericEntity instance = apiHrefIotResources.add(iot, child, new GenericMap(), "iots");
				    	return mapper.writeValueAsString(instance);
			    	} catch(TechnicalNotFoundException e) {
			    		response.status(404);
			    		return "";
			    	}
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    }
		});
		delete("/api/iots/:id/iots/:iot", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		IotRest iot = doGetById(request.params(ID));
			    	try {
			    		IotRest param = doGetById(request.params(IOT));
			    		apiHrefIotResources.remove(iot, param, request.queryParams(INSTANCE));
			    	} catch(TechnicalNotFoundException e) {
			    		response.status(404);
			    		return "";
			    	}
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    	return doGetById(request, ID, response);
		    }
		});
		/**
		 * plugins
		 */
		get("/api/iots/:id/plugins", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		IotRest master = doGetById(request.params(ID));
		    		List<ScriptPluginRest> result = new ArrayList<ScriptPluginRest>();
		    		for(GenericEntity link : sort(apiHrefIotScriptPluginResources.findAll(master))) {
		    			result.add(scriptRest(link));
		    		}
			    	return mapper.writeValueAsString(result);
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    }
		});
		post("/api/iots/:id/plugins/:plugin", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		IotRest iot = doGetById(request.params(ID));
			    	try {
			    		ScriptPluginRest plugin = apiScriptPluginResources.doGetById(request.params(PLUGIN));
				    	GenericEntity instance = apiHrefIotScriptPluginResources.add(iot, plugin, new GenericMap(request.body()), "plugins");
				    	return mapper.writeValueAsString(scriptRest(instance));
			    	} catch(TechnicalNotFoundException e) {
			    		response.status(404);
			    		return "";
			    	}
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    }
		});
		put("/api/iots/:id/plugins/:plugin/:instance", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		doGetById(request.params(ID));
			    	try {
				    	apiScriptPluginResources.doGetById(request.params(PLUGIN));
				    	GenericMap properties = apiHrefIotScriptPluginResources.update(request.params(INSTANCE), new GenericMap(request.body()));
				    	return mapper.writeValueAsString(properties);
			    	} catch(TechnicalNotFoundException e) {
			    		response.status(404);
			    		return "";
			    	}
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    }
		});
		delete("/api/iots/:id/plugins/:plugin", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		IotRest iot = doGetById(request.params(ID));
			    	try {
			    		ScriptPluginRest plugin = apiScriptPluginResources.doGetById(request.params(":plugin"));
			    		apiHrefIotScriptPluginResources.remove(iot, plugin, request.queryParams(INSTANCE));
			    	} catch(TechnicalNotFoundException e) {
			    		response.status(404);
			    		return "";
			    	}
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    	return doGetById(request, ID, response);
		    }
		});
	}

	/**
	 * @param list
	 * @return List<CommandRest>
	 */
	public List<GenericEntity> sort(List<GenericEntity> list) {
		/**
		 * sort by order
		 */
		Collections.sort(list, new Comparator<GenericEntity>() {
	
			@Override
			public int compare(GenericEntity l, GenericEntity r) {
				String left = (String) l.get("order");
				if(left == null) {
					return -1;
				}
				String right = (String) r.get("order");
				return left.compareTo(right);
			}
			
		});
		return list;
	}

	/**
	 * build script with relationship
	 * @param link
	 * @return ScriptPluginRest
	 * @throws TechnicalNotFoundException 
	 */
	private ScriptPluginRest scriptRest(GenericEntity link) throws TechnicalNotFoundException {
		ScriptPluginRest scriptRest = apiScriptPluginResources.doGetById(link.id);
		scriptRest.instance = link.instance;
		for(Entry<String, Object> property : link.get()) {
			scriptRest.put(property.getKey(), property.getValue());
		}
		return scriptRest;
	}
}
