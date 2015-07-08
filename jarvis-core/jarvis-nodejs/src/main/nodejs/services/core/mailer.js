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

var logger = require('blammo').LoggerFactory.getLogger('kernel');

'use strict';

var nodemailer = require('nodemailer');

// create reusable transporter object using SMTP transport
var transporter = nodemailer.createTransport({
	service: 'SES',
	auth: {
		user: process.env.SMTP_CRED_USER,
		pass: process.env.SMTP_CRED_PASS
	},
	tls: {
		rejectUnauthorized: false
	}
});

// NB! No need to recreate the transporter object. You can use
// the same transporter object for all e-mails

/**
 * send email
 * @type {{sendmail: Function}}
 */
module.exports = {
	// send mail with defined transport object
	sendmail: function(subject, text, context) {
		var mailOptions = {};
		mailOptions.from = 'Automate <yannick.roffin@laposte.net>'; // sender address
		mailOptions.to = 'yroffin@gmail.com';
		mailOptions.subject = subject; // Subject line
		if(context) {
			text = text + '\n*** context ***\n' + JSON.stringify(context);
			html = '<pre>' + text + '</pre>';
		}
		mailOptions.text = text; // plaintext body
		mailOptions.html = html; // html body
		transporter.sendMail(mailOptions, function (error, info) {
				if (error) {
					return logger.error(error);
				}
				logger.info('Message sent: ', subject, info.response);
			}
		);
	}
}
