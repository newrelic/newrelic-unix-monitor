var fs = require('fs');
var inputJSON = JSON.parse(fs.readFileSync('./UnixMetrics.json', 'utf8'));

var transTable = {
		"THIS_IS_THE_PREFIX_OF_A_METRIC_NAME" : "METRIC_PREFIX",
		"THIS_IS_A_PROCESS_NAME_TO_BE_COUNTED" : "PROCESS_NAME",
		"THIS_IS_A_DISK_NAME" : "DISK_NAME",
		"THIS_IS_THE_LAST_PART_OF_A_METRIC_NAME" : "METRIC_SUFFIX",
		"THIS_IS_THE_ACTUAL_METRIC_VALUE" : "METRIC_VALUE",
		"THIS_IS_A_MEMBER_PLACEHOLDER" : "MEMBER_PLACEHOLDER",
		"THIS_COLUMN_WILL_BE_IGNORED" : "IGNORE_COLUMN"
}

inputJSON.forEach(function(os) {
    var outfile = fs.createWriteStream('./plugin.json.new.' + os.os);
    var outJSON = {};
    outJSON['global'] = {
        'os' : os.os,
        'debug': false,
        'hostname': 'auto',
		"disksCommand": "",
  		"disksRegex": "",
  		"interfacesCommand": "",
  		"interfacesRegex": "",
  		'pageSize': os.pageSize
    };
    
    if("pageSizeCommand" in os) {
        outJSON.global.pageSizeCommand = os.pageSizeCommand.join(" ");
    }
    
    outJSON['agents'] = [];    

    var metrics = {};
    for(var metric in os.allMetrics) {
        var splitname = metric.split("/");
        if(!(splitname[0] in metrics)) {
            metrics[splitname[0]] = {};
        }
        var metricout = os.allMetrics[metric];
        metricout.type = metricout.this_type;
        delete metricout.this_type;
        metrics[splitname[0]][splitname[1]] = metricout;
    }
    

    for(var comm in os.allCommands) {
        var oldcomm = os.allCommands[comm];
        var newmap = {};
        for (var mapstr in oldcomm.lineMappings) {
        	var oldmaparr = oldcomm.lineMappings[mapstr];
        	var newmaparr = [];
        	for(var mapval in oldmaparr) {
        		if (oldmaparr[mapval] in transTable) {
        			newmaparr.push(transTable[oldmaparr[mapval]]);
        		} else {
        			newmaparr.push(oldmaparr[mapval]);
        		}
        	}
        	newmap[mapstr] = newmaparr;
        }
        
        var newcomm = {
        	"name" : comm,
        	"command" : oldcomm.commands[0].join(" "),
            "checkAllRegex" : oldcomm.checkAllRegex,
            "lineLimit" : oldcomm.lineLimit,
            "type" : oldcomm.type,
            "mappings": newmap,
            "metrics" : metrics[comm]   
        };

        outJSON.agents.push(newcomm);
    } 

    outfile.write(JSON.stringify(outJSON, null, 2));
    outfile.close;
});