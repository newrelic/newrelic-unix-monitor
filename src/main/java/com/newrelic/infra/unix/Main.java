package com.newrelic.infra.unix;

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
	        System.err.println("ERROR: " + e.getMessage());
	        System.exit(-1);
	    }
	}
}

