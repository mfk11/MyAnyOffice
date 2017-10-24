
var exec = require('cordova/exec');

function AnyOffice(){}
AnyOffice.prototype.init = function(successCallback, errorCallback, options) {
    options = options || {};

    exec(successCallback, errorCallback, "MyAnyOffice", "init", []);
};
AnyOffice.prototype.login = function(successCallback, errorCallback, options) {
		options = options || {};
		var username = options.username || "";
		var password = options.password || "";
		var gateway = options.gateway || "";
		var port = options.port || 443;

  	exec(successCallback, errorCallback, "MyAnyOffice", "login", [username, password, gateway, port]);
};
AnyOffice.prototype.getNetStatus = function(successCallback, errorCallback) {

  	exec(successCallback, errorCallback, "MyAnyOffice", "getNetStatus", []);
};
module.exports = new AnyOffice();