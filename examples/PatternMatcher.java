import java.util.regex.*;


/**
 * The PatternMatcher program help you to understand how to parse a line.
 * There are multiple samples with regular expressions are given below and you may uncomment and run the same.
 * Once, the parsing is done correctly then same working regular expression may be used in newrelic-unix-monitor's json file.
 * @author  Gulab Sidhwani
 * @version 1.0
 * @since   2023-01-05 
 */
public class PatternMatcher {

	public static void main(String[] args) {
	
        // Sample 1
		//Pattern patternToMatch = Pattern.compile("()([a-zA-Z_]+)\\s+([a-zA-Z-]+)\\s+(.*)\\s+([0-9_-]+)\\s+(.*)");
		//Matcher lineMatch = patternToMatch.matcher("online         -             12DEC        33 svc:/system/sysobj:default".trim());

		// Sample 2
		//Pattern patternToMatch = Pattern.compile("(\\d+)\\s+([A-Z]{1})\\s+([0-9\\.]+)\\s+([0-9\\.]+)\\s+(\\d+)\\s+(\\d+)\\s+([A-Za-z0-9]+)\\s+([0-9:]+)\\s+(\\d+)\\s+(.+)");
		//Matcher lineMatch = patternToMatch.matcher("1123 S  0.0  0.0      132 3932     root       00:00    1 /usr/sbin/ttymon -g -d /dev/vt/4 -l console -m ldterm,ttcompat -h -p gulab.host.com vt4 login:".trim());


		// Sample 3
		 Pattern patternToMatch = Pattern.compile("status=([\\d]+)\\scommand=([a-zA-Z.0-9]+)\\stime=([,a-zA-Z.0-9:\\s]+)");
		 Matcher lineMatch = patternToMatch.matcher("status=0 command=getMemStats.sh time=Fri Dec 23 13:51:04 GMT 2022".trim());
		 //Matcher lineMatch = patternToMatch.matcher("status=0 command=getLdmMemStat.sh time=Tuesday, January 10, 2023 at 8:16:52 AM GMT".trim());
		

		// Sample 4
		//Pattern patternToMatch = Pattern.compile("\\s*(\\d+)\\s+(\\S{1})(\\S{3})(\\S{3})(\\S{3})\\s+(\\d+)\\s+(\\w+)\\s+(\\w+)\\s+(\\d+)\\s+(\\w{3}\\s+\\d{2}\\s+\\d{4})\\s+(.*)");
		//Matcher lineMatch = patternToMatch.matcher("3 drwxr-xr-x   2 root     sys            2 Aug 20  2018 zones".trim());

		// Sample 5
		//Pattern patternToMatch = Pattern.compile("CPU states:\\s+([0-9\\.]+)%\\s+idle,\\s+([0-9\\.]+)%\\s+user,\\s+([0-9\\.]+)%\\s+kernel,\\s+([0-9\\.]+)%\\s+stolen,\\s+([0-9\\.]+)%\\s+swap");
		//Matcher lineMatch = patternToMatch.matcher("CPU states: 96.0% idle,  0.0% user,  4.0% kernel,  0.0% stolen,  0.0% swap".trim());

		// Sample 6
		//Pattern patternToMatch = Pattern.compile("(\\S+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+(\\S+\\s+\\d+\\s+\\d+:\\d+)\\s+(\\S+)");
		//Matcher lineMatch = patternToMatch.matcher("-rw-r--r-- 1 user staff 355 Jan 05 07:22 /opt/New_Relic/newrelic-unix-monitor/config/plugin.json".trim());

		// Sample 7
		//Pattern patternToMatch = Pattern.compile("\\s*([A-Za-z0-9]+)\\s+([0-9\\.]+)\\s+([0-9\\.]+)\\s+([0-9\\.]+)\\s+([0-9\\.]+)\\s+([0-9\\.]+)\\s+([0-9\\.]+)\\s+([0-9\\.]+)\\s+([0-9\\.]+)\\s+([0-9\\.]+)\\s+([0-9\\.]+)");
	//	Matcher lineMatch = patternToMatch.matcher("cmdk0  11999.0 1204392.0 564635.0 5280431.5  0.0  0.0    0.0    0.5   0   1".trim());
		
		

		int iCount = lineMatch.groupCount(); // Total Matching Groups
		System.out.println("Total Matching Groups " + iCount);
		int i =1;

		if (lineMatch.matches())
		{
			while (iCount >= i ) {
				System.out.println("Matching Column " + "[" + i + "]  " + lineMatch.group(i++) );
			}


		}

	}

}
