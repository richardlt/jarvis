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
 * blockResourceService
 */
angular.module('JarvisApp.services.block', []).factory('blockResourceService',
		[ '$log', 'Restangular', 'genericResourceService', 'deviceResourceService', 'filterService',
		function($log, Restangular, genericResourceService, deviceResourceService, filterService) {
  return {
	    block: genericResourceService.crud(['blocks']),
	    plugins: {
	    	"if": genericResourceService.links(['blocks'], ['plugins','scripts'],'HREF_IF'),
		    "then": genericResourceService.links(['blocks'], ['plugins','scripts'],'HREF_THEN'),
		    "else": genericResourceService.links(['blocks'], ['plugins','scripts'],'HREF_ELSE')
	    },
		blocks: {
			"then": genericResourceService.links(['blocks'], ['blocks'],'HREF_THEN'),
			"else": genericResourceService.links(['blocks'], ['blocks'],'HREF_ELSE')
		}
  }
}]);
