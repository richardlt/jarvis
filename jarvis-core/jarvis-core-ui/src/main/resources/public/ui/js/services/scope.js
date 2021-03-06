/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Services */

/**
 * genericScopeService
 */
angular.module('JarvisApp.services.scope', ['JarvisApp.services.generic']).factory('genericScopeService', 
		[ '$log', '$filter', 'genericResourceService', 'genericPickerService', 'toastService', function($log, $filter, genericResourceService, genericPickerService, toastService) {
  var scope = {
	  resources : function(setEntities, getEntities, scope, resource, service, init) {
	    	$log.debug("inject default resources scope", resource, init);
		    /**
		     * Cf. genericResourceService
		     */
		    scope.create = function(create, callback) {
		    	genericResourceService.scope.collections.new(
		    			resource,
		        		getEntities(),
		        		create,
		        		service,
		        		callback
		        );
		    }
		    /**
		     * Cf. genericResourceService
		     */
		    scope.new = function(callback) {
		    	genericResourceService.scope.collections.new(
		    			resource,
		        		getEntities(),
		        		init,
		        		service,
		        		callback
		        );
		    }
			scope.crud = genericResourceService.crud(resource.split('/'));
		    /**
		     * Cf. genericResourceService
		     */
		    scope.remove = function(entity,callback) {
		    	genericPickerService.pickers.confirm(
		    			$filter('translate')('action.drop', {name:entity.name, id:entity.id}),
		    			'',
		    			$filter('translate')('yes'),
		    			$filter('translate')('no')
		    		)
		    		.then(function() {
			    	$log.debug('remove', entity);
					var toremove = entity.id;
					_.remove(getEntities(), function(element) {
						return element.id == toremove;
					});
			    	scope.crud.delete(entity.id);
			    	if(callback) {
			    		callback(entity);
			    	}
		    	}, function() {
			    	$log.debug('abort');
		    	});
		    }
		    /**
		     * loading
		     */
		    scope.load = function(callback) {
		    	setEntities([]);
		    	service.findAll(function(data) {
		    		setEntities(data);
					$log.debug(resource+'-ctrl', data, callback);
					if(callback) {
						callback();
					}
			    }, toastService.failure);
		    }
	  },
	  resource : function(scope, resource, back, service, link, dataLink, stateParamsId) {
	    	$log.debug("inject default resource scope", resource);
		    /**
		     * Cf. genericResourceService
		     */
		    scope.remove = function(data) {
		    	genericPickerService.pickers.confirm(
		    			$filter('translate')('action.drop', {name:data.name, id:data.id}),
		    			'',
		    			$filter('translate')('yes'),
		    			$filter('translate')('no')
		    		)
		    		.then(function() {
		    			genericResourceService.scope.entity.remove(function() {scope.go(back)}, resource, data, service);
			    	}, function() {
				    	$log.debug('abort');
			   	});
		    }
		    /**
		     * Cf. genericResourceService
		     */
		    scope.save = function(data) {
		    	genericResourceService.scope.entity.save(resource, data, service);
		    }
		    /**
		     * Cf. genericResourceService
		     */
		    scope.duplicate = function(data) {
		    	genericResourceService.scope.entity.duplicate(function() {scope.go(back)}, resource, data, service);
		    }
	  },
	  resourceLink : function(getLink, scope, resource, back, service, link, dataLink, stateParamsId, callbacks) {
	    	$log.debug('inject link',link);
		    /**
		     * Cf. genericResourceService
		     */
		    scope.add = function(data) {
		    	$log.debug('link::add',data);
		    	genericResourceService.scope.link.add(stateParamsId,data,dataLink,link,getLink());
			}
		    /**
		     * Cf. genericResourceService
		     */
		    scope.update = function(data) {
		    	$log.debug('link::update',data);
		    	genericResourceService.scope.link.save(stateParamsId,data,link);
			}
		    /**
		     * Cf. genericResourceService
		     */
		    scope.drop = function(data) {
		    	genericPickerService.pickers.confirm(
		    			$filter('translate')('action.remove.link', {name:data.name, id:data.id}),
		    			'',
		    			$filter('translate')('yes'),
		    			$filter('translate')('no')
		    		)
		    		.then(function() {
				    	$log.debug('link::drop',data);
				    	if(callbacks && callbacks.remove) {
				    		/**
				    		 * alternate remove function
				    		 */
				    		callbacks.remove(data);
				    	} else {
				    		/**
				    		 * default remove function
				    		 */
					    	genericResourceService.scope.link.remove(stateParamsId,data,link,getLink());
				    	}
				    	/**
				    	 * post drop callback
				    	 */
				    	if(callbacks && callbacks.afterRemove) {
				    		callbacks.afterRemove(data);
				    	}
			    	}, function() {
				    	$log.debug('abort');
			    	}
			    );
			}
	  }
  };
  return {
	  scope:scope
  }
}]);
