package com.newrelic.infra.unix;

import java.io.PrintWriter;
import java.io.StringWriter;
import com.newrelic.infra.publish.api.Runner;
import com.newrelic.infra.publish.RunnerFactory;
import com.newrelic.infra.unix.UnixAgentFactory;

public class Main {
	public static void main(String[] args) {
		try {
			Runner runner = RunnerFactory.getRunner();
	        runner.add(new UnixAgentFactory());
	        runner.setupAndRun(); // Never returns
	    } catch (Exception e) {
	        System.err.println("ERROR starting runner: " + e.getMessage());
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					System.err.println("Stack trace: " + sw.toString());
	        System.exit(-1);
	    }
	}
}
