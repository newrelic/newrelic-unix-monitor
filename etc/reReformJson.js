var fs = require('fs');

osArray = ["aix","linux","osx","solaris_10","solaris_11"];

osArray.forEach(function(os) {
    var inputJSON = JSON.parse(fs.readFileSync('./config/plugin.json.'+ os, 'utf8'));
    var outfile = fs.createWriteStream('./config/plugin-commands-'+ os +'.json');
    var outputJSON = {};
    outputJSON['os_settings'] = inputJSON.global;
    outputJSON.commands = [];
   
    for(var command in inputJSON.agents) {
    	var oldcommand = inputJSON.agents[command];
    	var newcommand = JSON.parse(JSON.stringify(oldcommand));
    	delete newcommand['metrics'];
    	delete newcommand['mappings'];
    	newcommand['mappings'] = [];
    	for(var mapping in oldcommand['mappings']) {
    		console.log(JSON.stringify(oldcommand.mappings[mapping]));
    		var newmap = { 'expression': mapping, };
			newmap['metrics'] = [];
			for(var mapword in oldcommand.mappings[mapping]) {
    			var theword = oldcommand.mappings[mapping][mapword]
    			if(oldcommand['metrics'] && theword in oldcommand['metrics']) {
    				newmap.metrics.push(oldcommand['metrics'][theword]);
    			} else {
    				var outword = { 'name': theword, 'type': 'SPECIAL' };
    				newmap.metrics.push(outword);
    			}
    		}
    		newcommand.mappings.push(newmap)
    	}
    	outputJSON.commands.push(newcommand)
    }

    outfile.write(JSON.stringify(outputJSON, null, 2));
    outfile.close;
});