package com.newrelic.infra.unix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newrelic.infra.publish.api.Agent;
import com.newrelic.infra.publish.api.InventoryReporter;
import com.newrelic.infra.publish.api.MetricReporter;
import com.newrelic.infra.unix.config.AgentSettings;
import com.newrelic.infra.unix.config.Command;

public class UnixAgent extends Agent {

	private static final Logger logger = LoggerFactory.getLogger(UnixAgent.class);
	private AgentSettings agentSettings;
	private HashSet<String> disks;
	private HashSet<String> networkInterfaces;
	private int pageSize;
	private HashMap<String, Long> commandTimestamp;
	
	public UnixAgent(AgentSettings asettings) {
		agentSettings = asettings;
		disks = getMembers(agentSettings.getOsSettings().getDisksCommand(), agentSettings.getOsSettings().getDisksRegex());
		networkInterfaces = getMembers(agentSettings.getOsSettings().getInterfacesCommand(), agentSettings.getOsSettings().getInterfacesRegex());
		setPageSize(agentSettings);
		addStaticAttribute(UnixAgentConstants.KAOSMetricName, agentSettings.getOs().split("_")[0]);
		commandTimestamp = new HashMap<String, Long>();
	}

	// Maintain timestamp for when the command was run last. 
	// If the command has an interval established ("'interval': nnn" in plugin-commands-xxx.json),
	// only run command where the interval has been exceeded since the last run.
	// Returns 'true' if the command should be run, 'false' if it should be skipped.
	private boolean doRunCommand(Command command) {
			// Check if the command interval is non-zero (default is 0).
			// If 0, don't bother doing anything else and return true.
		if(command.getInterval() == 0) {
			logger.debug("doRunCommand: Interval not configured or set to '0' for command " 
				+ command.getCommand() + ", will always execute.");
			return true;
		}
		
		String commandExec = command.getCommand();	
		long interval = command.getInterval() * 60000;
		long currentTime = System.currentTimeMillis();
		if (commandTimestamp.containsKey(commandExec)) {
			// Find the last run timestamp
			long lastrun = commandTimestamp.get(commandExec);
			// Check if it is time to execute the command
			if ((currentTime - lastrun) > interval) {
				// Update the timestamp
				logger.debug("doRunCommand: Time to run command " + commandExec + ", Interval @ " + interval);
				commandTimestamp.put(commandExec, currentTime);
				return true;
			} else {
				// Skip running command since interval has not passed
				logger.debug("doRunCommand: Skipping command " + commandExec + 
					", Timeleft @ " + (currentTime - lastrun) + ", Interval @ " + interval);
				return false;
			}
		} else {
			// First run, since there is interval we need to maintain map
			logger.debug("doRunCommand: First run for command " + commandExec + " @ " + currentTime);
			commandTimestamp.put(commandExec, currentTime);
			return true;
		}
	}
	
	@Override
	public void populateMetrics(MetricReporter metricReporter) throws Exception {
		for(Command command : agentSettings.getCommands()) {
			try {
				HashSet<String> members = new HashSet<String>();
				if (command.getCommand().contains(UnixAgentConstants.kMemberPlaceholder)) {
					if (command.getEventType().toUpperCase().contains("DISK")) {
						members = disks;
					} else if (command.getEventType().toUpperCase().contains("NETWORK")) {
						members = networkInterfaces;
					} else {
						members.add("");
						throw new Exception("Command with member placeholder not of eventType 'Disk' or 'Network' (case insensitive).");
					}
				} else {
					members.add("");
				}
				
				// Checks whether to run this commands in this harvest cycle.
				if(doRunCommand(command)) {
					for(String thisMember : members) {
						CommandMetricUtils.parseCommandOutput(command, thisMember, metricReporter, getStaticAttributes(), pageSize);
					}
				} else {
					logger.debug("Info: Skipping " + command.getEventType() + " : " + command.getCommand());
				}
			} catch (Exception e) {
				logger.error("Error: Parsing of " + command.getEventType() + " : " + command.getCommand() + " could not be completed.");
				logger.error(e.toString());
			}
		}
	}
	
	public HashSet<String> getMembers(String command, String strMemberPattern) {
		HashSet<String> members = new HashSet<String>();
		if(command == null || strMemberPattern == null || command.isEmpty() || strMemberPattern.isEmpty()) {
			members.add("");
		} else {
			Pattern memberPattern = Pattern.compile(strMemberPattern);
			ArrayList<String> membersReader = CommandMetricUtils.executeCommand(command);
			try {
				for (String line : membersReader) {
					Matcher lineMatch = memberPattern.matcher(line);
					if (lineMatch.matches()) {
						members.add(lineMatch.group(1));
					}
				}
			} catch (Exception e) {
				logger.error("Error: Parsing of " + command + "could not be completed.");
				e.printStackTrace();
			}
			logger.debug("Members found: " + members);
		}
		return members;
	}
	
	public void setPageSize(AgentSettings agentSettings) {
		int pageSize = 1;
		for(String line : CommandMetricUtils.executeCommand(agentSettings.getOsSettings().getPageSizeCommand())) {
			try {
				pageSize = Integer.parseInt(line.trim());
				break;
			} catch (NumberFormatException e) { 
				pageSize = 1;
			}
		}
		this.pageSize = pageSize;
	}

	@Override
	public void dispose() throws Exception { }

	@Override
	public void populateInventory(InventoryReporter inventoryReporter) throws Exception { }	
}
