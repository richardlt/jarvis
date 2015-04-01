/**
 * Copyright 2014 Yannick Roffin.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

var logger = require('blammo').LoggerFactory.getLogger('crontab');

'use strict';

var cron = require('cron');
var mongo = require(__dirname + '/mongodb');

var cronJobs = {}

/**
 * clear started status in current database set all them to not started
 */
exports.clear = function() {
	var jobs = mongo.syncCronList();
	for (index in jobs) {
		var job = jobs[index];
		mongo.syncCronUpdate(job.job, job.cronTime, job.plugin, job.params, job.timestamp, false);
	}
}

/**
 * start jobs
 */
exports.start = function(callback) {
	var jobs = mongo.syncCronList();
	for (index in jobs) {
		var job = jobs[index];
		if (!job.started) {
			/**
			 * set status to started
			 */
			mongo.syncCronUpdate(job.job, job.cronTime, job.plugin, job.params, job.timestamp, true);
			/**
			 * fork this jobs if not started
			 */
			cronJobs[job] = new cron.CronJob({
				cronTime : job.cronTime,
				onTick : function() {
					/**
					 * recover last version of job from database
					 */
					var newJobs = mongo.syncCronList({
						job : this.job
					});
					var updateJob = newJobs[0];
					this.started = true;
					this.timestamp = new Date();
					this.plugin = updateJob.plugin;
					this.params = updateJob.params;
					mongo.syncCronUpdate(this.job, this.cronTime, this.plugin, this.params, this.timestamp, true);
					callback(this);
				},
				start : false,
				timeZone : "Europe/Paris",
				context : job
			});
			cronJobs[job].start();
		}
	}
}