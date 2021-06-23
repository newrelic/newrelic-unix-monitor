package com.newrelic.infra.unix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newrelic.infra.publish.api.MetricReporter;
import com.newrelic.infra.publish.api.metrics.*;
import com.newrelic.infra.unix.config.Command;
import com.newrelic.infra.unix.config.CommandMapping;
import com.newrelic.infra.unix.config.MappingMetric;
import com.newrelic.infra.unix.config.MappingTranslation;

public class CommandMetricUtils {

	public static final String kPluginJarName = "newrelic_unix_plugin";
	private static final Logger logger = LoggerFactory.getLogger(CommandMetricUtils.class);

	public static ArrayList<String> executeCommand(String command) {
		return executeCommand(command, false);
	}

	public static ArrayList<String> executeCommand(String command, Boolean useFile) {
		BufferedReader br = null;
		ArrayList<String> al = new ArrayList<String>();
		String line = null;

		if (useFile) {
			File commandFile = new File(command + ".out");
			CommandMetricUtils.logger.debug("Opening file: "
					+ commandFile.getAbsolutePath());
			if (!commandFile.exists()) {
				CommandMetricUtils.logger.error("Error: "
						+ commandFile.getAbsolutePath() + " does not exist.");
			} else if (!commandFile.isFile()) {
				CommandMetricUtils.logger.error("Error: "
						+ commandFile.getAbsolutePath() + " is not a file.");
			} else {
				try {
					br = new BufferedReader(new FileReader(commandFile));
					while((line = br.readLine()) != null) {
						al.add(line);
					}
				} catch (Exception e) {
					CommandMetricUtils.logger.error("Error: "
							+ commandFile.getAbsolutePath()
							+ " does not exist.");
					e.printStackTrace();
				} finally {
					try {
						br.close();
					} catch (IOException e) {
						// If we can't close, then it's probably closed.
					}
				}
			}
		} else {
			Process proc = null;
			try {
				if (command != null) {
					CommandMetricUtils.logger.debug("Begin execution of " + command);
					ProcessBuilder pb = new ProcessBuilder(command.split(" "))
							.redirectErrorStream(true);
					proc = pb.start();
					br = new BufferedReader(new InputStreamReader(
							proc.getInputStream()));
					while((line = br.readLine()) != null) {
						al.add(line);
						logger.debug(" Line: " + line);
					}
					proc.waitFor();
					if(br != null) {
						br.close();
					}
					if(proc != null) {
						proc.getOutputStream().close();
						proc.getErrorStream().close();
						proc.getInputStream().close();
					}
				} else {
					CommandMetricUtils.logger.error("Error: command was null.");
				}
			} catch (Exception e) {
				CommandMetricUtils.logger.error("Error: Execution of " + command + " failed.");
				e.printStackTrace();
			} finally {
				try {
					if(br != null) {
						br.close();
					}
					if(proc != null) {
						proc.getOutputStream().close();
						proc.getErrorStream().close();
						proc.getInputStream().close();
					}
				} catch (Exception e) {
					// If we can't close, then it's probably closed.
				}
			}
		}
		return al;
	}
	
	public static String extractCommandName(String fullCommand, Boolean hasSpaces) {
		String outCommand;
		if(hasSpaces) {
			outCommand = fullCommand;
		} else {
			outCommand = fullCommand.split("\\s+")[0];
		}
		if (outCommand.startsWith("[") && outCommand.endsWith("]")) {
			outCommand = outCommand.replace("]","").replace("[", "").split("/")[0];
		} else {
			outCommand = outCommand.substring(outCommand.lastIndexOf('/') + 1);
		}
		if(outCommand.endsWith(":")) {
			outCommand = outCommand.substring(0, outCommand.length()-1);
		}
		return outCommand;
	}

	private static void insertMetric(List<Metric> metricSet,
		MappingMetric metricDeets, String metricValueString) {
		Metric newMetric = null;
		String metricName = toCamelCase(metricDeets.getName());
		
		// Assume that it's a string attribute if we don't know the type.
		if((metricDeets.getType() == null) || metricDeets.getType().equals("ATTRIBUTE")) {
			newMetric = new AttributeMetric(metricName, truncateForInsights(metricValueString));
		} else if((metricDeets.getType().equals("DATE"))) {
			SimpleDateFormat dateFormat;
			if(metricDeets.getFormat() == null) {
				dateFormat = new SimpleDateFormat();
			} else {
				dateFormat = new SimpleDateFormat(metricDeets.getFormat());
			}
			try {
				Date date = dateFormat.parse(metricValueString);
				newMetric = new GaugeMetric(metricName, date.getTime());
				logger.debug("Date parsing succeeded.");
			} catch(ParseException pe) {
				logger.debug("Date parsing failed, reporting attribute value as-is.");
				try {
						double metricValue = Double.parseDouble(metricValueString);
						newMetric = new GaugeMetric(metricName, metricValue);
				} catch (NumberFormatException e) {
					newMetric = new AttributeMetric(metricName, truncateForInsights(metricValueString));
				}
			}
		} else {
			double metricValue;
			try {
				if(metricDeets.getActualRatio() != null) {
					metricValue = Double.parseDouble(metricValueString) * metricDeets.getActualRatio();
				} else {
					metricValue = Double.parseDouble(metricValueString);
				}

				if(metricDeets.getType().equals("DELTA")) {
					newMetric = new DeltaMetric(metricName, metricValue);
				} else if(metricDeets.getType().equals("NORMAL")) {
					newMetric = new GaugeMetric(metricName, metricValue);
				} else if(metricDeets.getType().equals("INCREMENT")) {
					newMetric = new GaugeMetric(metricName, metricValue);
				} else if(metricDeets.getType().equals("RATE")) {
					newMetric = new RateMetric(metricName, metricValue);
				}
	  	} catch (NumberFormatException e) {
				newMetric = new AttributeMetric(metricName, truncateForInsights(metricValueString));
	  	}
		}
		
		if(newMetric != null) {
			logger.debug("Inserting Metric: " + newMetric.getName() + " : " + newMetric.getValue());
			metricSet.add(newMetric);
		} else {
			logger.error("Error parsing metric name: " + metricDeets.getName() + ", value: " + metricValueString);
		}
	}

	public static String mungeString(String str1, String str2, char divider) {
		if (str1 == null || str1.isEmpty()) {
			return str2;
		} else if (str2 == null || str2.isEmpty()) {
			return str1;
		} else {
			return str1 + divider + str2;
		}
	}

	public static void parseCommandOutput(Command command, String metricPrefix,
			MetricReporter metricReporter, List<Metric> staticAttributes, int pageSize) throws Exception {
		String thisCommand = command.getCommand().replace(UnixAgentConstants.kMemberPlaceholder, metricPrefix);
		ArrayList<String> commandReader = CommandMetricUtils.executeCommand(thisCommand);
		
		List<CommandMapping> lineMappings = command.getMappings();
		Boolean checkAllRegex = command.getCheckAllRegex();
		int lineLimit = command.getLineLimit();
		int lineCount = 0;
		List<Metric> commandMetricSet = new LinkedList<Metric>();
		
		// LineLoop loops through all of the lines, man!
		// Unless there's a line limit set for this command, in which case it only loops through a maximum of [limit].
		LineLoop: for(String line : commandReader) {
			lineCount++;
			String thisMetricInstance = metricPrefix;
			
			List<Metric> localMetricSet = new LinkedList<Metric>();
			
			// RegexLoop matches the line against all regex patterns for this command
			// If it finds a match, it parses it and matches against the "mappings" definition.
			RegexLoop: for(int lmap = 0; lmap < lineMappings.size(); lmap++) {
				CommandMapping thisLineMapping = lineMappings.get(lmap);
				List<MappingMetric> thisLineMappingMetrics = thisLineMapping.getMetrics();
				Pattern patternToMatch = Pattern.compile(thisLineMapping.getExpression());
				Matcher lineMatch = patternToMatch.matcher(line.trim());
				if (lineMatch.matches()) {
					logger.debug("Line MATCHED: " + line);
					logger.debug("Regex: " + patternToMatch.toString());
				
					String thisMetricName = "";
					String thisMetricValue = "";
					String thisMetricType = "";
					String thisMetricDateFormat = "";

					// Loop through columns of regexed line.
					// If the matching field in the mappings is one of the special keywords, act accordingly.
					for (int l = 0; l < thisLineMappingMetrics.size(); l++) {
						String columnMetricName = thisLineMappingMetrics.get(l).getName();
						String thisColumn = lineMatch.group(l + 1);
						if(columnMetricName.equals(UnixAgentConstants.kColumnIgnore))
							continue;	
						
						// Disk names get processed into the short name for the desk,
						// and appended to the "Instance" attribute.
						else if(columnMetricName.equals(UnixAgentConstants.kColumnMetricDiskName)) {
							thisMetricInstance = mungeString(
								thisMetricInstance, thisColumn.substring(thisColumn.lastIndexOf('/') + 1),
								UnixAgentConstants.kMetricTreeDivider);
						} 
						
						// Metric name columns get stored as the metric name to be used later.
						else if (columnMetricName.equals(UnixAgentConstants.kColumnMetricName)) {
							thisMetricName = thisColumn;
						}	
						
						// Metric prefixes get appended to the "Instance" attribute.
						// Can be "METRIC_PREFIX" (old syntax) or "METRIC_INSTANCE" (new syntax) - yay backwards compatibility!
						else if (columnMetricName.equals(UnixAgentConstants.kColumnMetricPrefix) || 
							columnMetricName.equals(UnixAgentConstants.kColumnMetricInstance)) {
							thisMetricInstance = mungeString(thisMetricInstance, thisColumn,
									UnixAgentConstants.kMetricTreeDivider);
						}
						
						// For processes, set the "Instance" attribute to a friendly process name,
						// AND set "Command" attribute to the full command.
						else if (columnMetricName.startsWith(UnixAgentConstants.kColumnMetricProcessName)) {
							String outCommand = extractCommandName(thisColumn,
								columnMetricName.equals(UnixAgentConstants.kColumnMetricProcessNameAndSpaces));
							thisMetricInstance = mungeString(thisMetricInstance, outCommand, UnixAgentConstants.kMetricTreeDivider);
							if (outCommand.toLowerCase().equals("java")) {
								String[] allColumns = thisColumn.split("\\s+");
						        CommandMetricUtils.insertMetric(localMetricSet, 
						        		new MappingMetric(UnixAgentConstants.kJavaClassMetricName), allColumns[allColumns.length-1]);
							}
					        CommandMetricUtils.insertMetric(localMetricSet, 
					        		new MappingMetric(UnixAgentConstants.kCommandMetricName), thisColumn);
						}
					    
						// If column is metric value (matched to metric name from that line),
					    // get its value, and get its type (and date format if exists) from the mapping.
						else if (columnMetricName.equals(UnixAgentConstants.kColumnMetricValue)) {
							thisMetricValue = thisColumn;
							thisMetricType = thisLineMappingMetrics.get(l).getType();
							thisMetricDateFormat = thisLineMappingMetrics.get(l).getFormat();
						}
						
						// Normal case - column is a metric, mapped to a mapping without any special keywords.
						else {
							// If it's the first time this mapping is encountered, 
							// convert the ratio into something useful and store in the mapping.
							if(thisLineMappingMetrics.get(l).getActualRatio() == null)
								thisLineMappingMetrics.get(l).setActualRatio(pageSize);
							CommandMetricUtils.insertMetric(localMetricSet, thisLineMappingMetrics.get(l), thisColumn);
						}
					}
					
					// Insert instance (if exists) into event as an attribute called "Instance"
					if(!thisMetricInstance.isEmpty()) {
				        CommandMetricUtils.insertMetric(localMetricSet, 
				        		new MappingMetric(UnixAgentConstants.kInstanceMetricName), thisMetricInstance);
					}
					
					// If the kColumnMetricName & kColumnMetricValue keywords were used to get metric name and value, 
					// and the metric name and value were subsequently defined, insert this metric.
					if(!thisMetricName.isEmpty() && !thisMetricValue.isEmpty()) {
						MappingMetric thisMappingMetric = null;
					
						// Check for a translation table - if one exists, attempt to match metric name to a tranlsation.
						if(thisLineMapping.getTranslations() != null) { 
							for(MappingTranslation thisTrans : thisLineMapping.getTranslations()) {
								if(thisTrans.getInput().equals(thisMetricName)) {
									thisMappingMetric = new MappingMetric(thisTrans.getOutput(), thisTrans.getType(), thisTrans.getRatio(), thisTrans.getFormat());
									thisMappingMetric.setActualRatio(pageSize);
									break;
								}
							}
						}
			    			
						// If mapping is still null, nothing was matched in translation table (or table doesn't exist)
						if(thisMappingMetric == null) {
							thisMappingMetric = new MappingMetric(thisMetricName);
							thisMappingMetric.setType(thisMetricType);
							thisMappingMetric.setFormat(thisMetricDateFormat);
						}
			    			
						CommandMetricUtils.insertMetric(localMetricSet, thisMappingMetric, thisMetricValue);
					}
					
					// Metric lines with "Instances" require their own metric event.
					// Otherwise, add to the set of metrics that will be reported at the end of the command looping.
					if(!thisMetricInstance.isEmpty() && !thisMetricInstance.equals(metricPrefix)) {
						if(staticAttributes != null) {
							localMetricSet.addAll(staticAttributes);
						}
						metricReporter.report(command.getEventType(), localMetricSet, thisCommand + thisMetricInstance);						
					} else {
						commandMetricSet.addAll(localMetricSet);
					}
					
					// Once we find a valid mapping for this line, stop looking for matches for this line,
					// unless we explicitly want to check all regex mappings (part of command config).
					if(!checkAllRegex) {
						break RegexLoop;
					}
				} else {
					logger.debug("Skipped: " + line);
				}
			}
			
			// For commands like 'top', we probably only need the first few lines.
			if (lineLimit > 0 && lineCount >= lineLimit) {
				break LineLoop;
			}
		}
		
		// For commands that used the pooled metric set (non-Instance-centric), finally report them.
		if(!commandMetricSet.isEmpty()) {
			if(staticAttributes != null) {
				commandMetricSet.addAll(staticAttributes);
			}
			metricReporter.report(command.getEventType(), commandMetricSet, thisCommand);
		}
	}
	
	public static double roundNumber(double theNumber, int places) {
		double placesDouble = Math.pow(10, places);
		return Math.round(theNumber * placesDouble) / placesDouble;
	}
	
	private static String toCamelCase(String input) {
	    String output = "";
	    String[] words = input.split("\\s+");
	    for (int i = 0; i < words.length; i++) {
	        String word = words[i];
	        if (i == 0) {
	        		output = word.toLowerCase();
	        } else {
	            output += word.substring(0, 1).toUpperCase() + word.substring(1, word.length()).toLowerCase();
	        }
	    }
	    return output;
	}
	
	private static String truncateForInsights(String input) {
		if (input.length() >= UnixAgentConstants.kInsightsAttributeSize) {
			return input.substring(0, UnixAgentConstants.kInsightsAttributeSize);
		} else {
			return input;
		}
	}
}
