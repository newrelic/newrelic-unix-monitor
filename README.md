# New Relic Plugin for Unix
## System-Level Montoring for AIX, Linux, Mac OS X & Solaris/SunOS

### Requirements

- A New Relic account. Sign up for a free account [here](http://newrelic.com)
- A unix server that you want to monitor
- Java Runtime (JRE) environment Version 1.6 or later
- Network access to New Relic (proxies are supported, see details below)

----

### Installation & Usage Overview

1. Download the latest version of the agent.
2. Gunzip & untar on Unix server that you want to monitor
4. Set account ID, keys and other settings in `config/plugin.json`
    * [Click here for plugin.json config details](#plugin-json-configuration)
5. OPTIONAL: Configure `pluginctl.sh` to have the correct paths to Java and your plugin location
    * Set `PLUGIN_JAVA` to the location of Java on your server (including the "java" filename)
    * Set `PLUGIN_PATH` to the location of the Unix Plugin
6. Run `./pluginctl.sh start` from command-line
7. Check logs (in `logs` directory by default) for errors
8. Login to New Relic UI and find your data in Insights.
    * In the data explorer, look for custom event types that start with "unixMonitor:"
    * Possible event types (for out-of-the-box commands): unixMonitor:Disk, unixMonitor:DiskIO, unixMonitor:NetworkIO, unixMonitor:Process, unixMonitor:Stats, unixMonitor:Vmstat

----
### `plugin.json` configuration

_Note: A full example of the possible fields in plugin.json can be found in plugin-fullexample.json_

#### Global settings

  * `OS` (default: `auto`): Used to determine which commands to run and how to parse them. Leave set to `auto` to have the plugin figure that out (which normally works).
  * `account_id`: New Relic account ID - the 6- or 7- digit number in the URL when you're logged into the account of your choosing.
  * `insights_insert_key` (under `insights`): You must create an [Insights Insert key, as described here.](https://docs.newrelic.com/docs/insights/insights-data-sources/custom-data/insert-custom-events-insights-api#register)
  * `name` (under `agents`): Otherwise, sets the hostname and agentName to whatever is set here. If set to `auto`, the plugin will use that server's hostname.

#### Proxy settings

If using a proxy, the optional `proxy` object should be added to the `global` object in `plugin.json`, if its not there already. The possible fields are: `proxy_host`, `proxy_port`, `proxy_username` and `proxy_password`, which are self-explanatory. The only field in this object that is required is `proxy_host`.

#### Dashboard deployment

If you want the plugin to check for and deploy the latest dashboards to your account when it starts up, you can set the `admin_api_key` setting in the `dashboards` object. You will need an [Admin API key, as described here.](https://docs.newrelic.com/docs/apis/rest-api-v2/getting-started/api-keys#admin-api) If you don't want it to run, leave this blank or remove it entirely.
In the `dashboard` object, the following two settings are also required, but must be left to their default values. **DO NOT CHANGE THESE UNLESS OTHERWISE INSTRUCTED.**
  * `integration_guid`: Default is `UNIX.Infra.Monitor`
  * `dashboard_install`: Default is `command_line`

### Fix for using the WebSphere JDK

If you are using the JDK that is packaged with WebSphere and see an exception in the logs like below, it is due to attempting to use the WebSphere SSL Factory instead of the IBM JSSE packages.
```
ERROR com.newrelic.metrics.publish.binding.Request - An error occurred communicating with the New Relic service
java.net.SocketException: java.lang.ClassNotFoundException: Cannot find the specified class com.ibm.websphere.ssl.protocol.SSLSocketFactory
```

If so, uncomment the following line in `pluginctl.sh` and restart the agent.
```
# USE_IBM_JSSE=true
```
