# New Relic Unix Monitor
System-Level Monitoring for AIX, Linux, OSX/MacOS & Solaris/SunOS

## TL;DR - where's the download link?
**[Here.](https://github.com/newrelic/newrelic-unix-monitor/releases/latest) If you are just deploying and not compiling it, _please download the release instead of cloning the repo._**

## Table Of Contents
* [Disclaimer](#Disclaimer)
* [Contributing](#Contributing)
* [Requirements](#requirements)
	* [Supported OSes](#supported-oses)
* [Installation & Usage Overview](#installation--usage-overview)
* [`plugin.json` configuration](#pluginjson-configuration)
	* [Global settings](#global-settings)
		* [Using the EU Data Center? Click here!](#global-settings)
	* [Agent settings](#agent-settings)
	* [Proxy settings](#proxy-settings)
	* [Dashboard deployment](#dashboard-deployment)
* [Other configurations](#other-configurations)
	* [Deploying Dashboards from separate server or desktop](#deploying-dashboards-from-separate-server-or-desktop)
	* [Fix for using the WebSphere JDK](#fix-for-using-the-websphere-jdk)
	* [Enabling Debug Mode](#debug-mode)

## Disclaimer
New Relic has Open Sourced this integration to enable monitoring of this technology. This integration is provided AS-IS WITHOUT WARRANTY OR SUPPORT, although you can report issues and contribute to this integration via GitHub. Support for this integration is available with an [Expert Services subscription](https://newrelic.com/expertservices).

## Contributing
We'd love to get your contributions to improve the Unix Monitor! Keep in mind when you submit your pull request, you'll need to sign the CLA via the click-through using CLA-Assistant. If you'd like to execute our corporate CLA, or if you have any questions, please drop us an email at open-source@newrelic.com.

## Requirements
* A New Relic account. Sign up for a free account [here](http://newrelic.com)
* A Unix server that you want to monitor
* Java JRE/JDK v1.6 or later
* Network access to New Relic (proxies are supported, see details below)
* For Dashboard Installation: `curl` or `wget` installed

### Supported OSes
* AIX 7.x
* Linux - All sorts, including on ARM processors (such as Raspberry Pi) and z/Linux
* OSX / MacOS 10.9 ('Mavericks') and above
* Solaris/SunOS 10.x and 11.x

--------------------------------------------------------------------------------

## Installation & Usage Overview
1. Download the latest version of the Unix Monitor [here](https://github.com/newrelic/newrelic-unix-monitor/releases/latest).
2. Copy, gunzip & untar the latest release onto the Unix server that you want to monitor
3. Set account ID, keys and other settings in `config/plugin.json`
	* [Click here for plugin.json config details](#pluginjson-configuration)
4. OPTIONAL: Configure `pluginctl.sh` to have the correct paths to Java and your plugin location
	* Set `PLUGIN_JAVA` to the location of Java on your server (including the "java" filename)
	* Set `PLUGIN_PATH` to the location of the Unix Plugin
5. Run `./pluginctl.sh start` from command-line
6. Check logs (in `logs` directory by default) for errors
7. Login to New Relic UI and find your data in Insights
	* In the data explorer, look for custom event types that start with "unixMonitor:"
	* Possible event types (for out-of-the-box commands): unixMonitor:Disk, unixMonitor:DiskIO, unixMonitor:NetworkIO, unixMonitor:Process, unixMonitor:Stats, unixMonitor:Vmstat

--------------------------------------------------------------------------------

## `plugin.json` configuration
_Note: A full example of the possible fields in `plugin.json` can be found in `plugin-full-example.json`_

### Global settings
* `OS` (default: `auto`): Used to determine which commands to run and how to parse them. Leave set to `auto` to have the plugin figure that out (which normally works).
* `account_id`: New Relic account ID - the 6- or 7- digit number in the URL when you're logged into the account of your choosing.
* `insights_insert_key` (under `insights`): You must create an [Insights Insert key, as described here.](https://docs.newrelic.com/docs/insights/insights-data-sources/custom-data/insert-custom-events-insights-api#register)
* `insights_data_center` (under `insights`, default: `us`): If using the NR EU data center for your account, please change this to `eu` or `EU`. Otherwise, you can leave this alone or omit this setting entirely. {#eu-data-center}

### Agent settings
These settings are found in the `agents` object.

* `name`: If set to `auto`, the plugin will use that server's hostname. Otherwise, sets the hostname and agentName to whatever is set here.
* `static` (optional): An object containing static attributes (as name-value pairs) you want to appear in every event from this plugin. For example:
```json
  "agents": [
    {
      "name": "auto",
      "static": {
        "data_center": "Philadelphia",
        "customer": "Eagles",
        "rank": 1
      }
    }
  ]
```

### Proxy settings
If using a proxy, the optional `proxy` object should be added to the `global` object in `plugin.json`, if its not there already.

* The available attributes are: `proxy_host`, `proxy_port`, `proxy_username` and `proxy_password`.
* The only attribute that is required in the `proxy` object is `proxy_host`.

### Dashboard deployment

#### Enabling at plugin startup
The plugin can check for and deploy the latest dashboards to your account when it starts up. This requires the `dashboards` object in `plugin.json` to be set up properly:

* `admin_api_key`: [Admin API key, as described here.](https://docs.newrelic.com/docs/apis/getting-started/intro-apis/understand-new-relic-api-keys#admin-api)
* `integration_guid`: Default is `UNIX.Infra.Monitor`.
* `dashboard_install`: Default is `command_line`.

**Note:** *DO NOT DELETE OR CHANGE `integration_guid` AND `dashboard_install` UNLESS OTHERWISE INSTRUCTED.* Both are required, but must be left to their default values.

#### Disabling deployment
If you don't want the dashboard deployment to run at startup, leave `admin_api_key` blank or remove it entirely.

--------------------------------------------------------------------------------

## Other configurations

### Deploying Dashboards from separate server or desktop
If you want to initiate the dashboard install from a standalone machine (i.e. a tools server or your own mac, linux or cygwin laptop/desktop), you will need the following:

* `pluginctl.sh`
* `config/plugin.json` (including path) with the `dashboard` object filled in [as described above.](#enabling-at-plugin-startup)

To install, run `./pluginctl.sh dashboards`.

### Fix for using the WebSphere JDK
If you are using the JDK that is packaged with WebSphere and see an exception in the logs like below, it is due to attempting to use the WebSphere SSL Factory instead of the IBM JSSE packages.

```
ERROR com.newrelic.metrics.publish.binding.Request - An error occurred communicating with the New Relic service
java.net.SocketException: java.lang.ClassNotFoundException: Cannot find the specified class com.ibm.websphere.ssl.protocol.SSLSocketFactory
```

If so, uncomment the following line in `pluginctl.sh` and restart the plugin.

```
# USE_IBM_JSSE=true
```
### Fix for using Solaris 10
If you see the following error, it may be because the Bourne shell does not support certain syntax in the installer script.

```
pluginctl.sh: syntax error at line 240: `admin_api_key=$' unexpected
```

If so, use the Korn shell or Bash (if available).  Both were tested successfully in Solaris 10.

### Debug Mode
If you are trying to customize the commands that the Unix Monitor is running, or you are not seeing any or all of the data you expect to see, you can put the agent into 'Debug Mode', in which it outputs to the logs all of the commands being run and it's attempts at parsing them.

Note: You will need to restart the Unix Monitor to pick up these changes.

#### Enabling Debug Mode
1. Set `"debug"` to  `true` to the global section of `config/plugin.json`, like so:
```json
{
	"global": {
		"OS": "auto",
		"debug": true,
```
2. Replace `config/logback.xml` with `config/logback-debug.xml`
3. Restart Unix Monitor

#### Disabling Debug Mode
1. Remove `"debug": true` from `config/plugin.json` or change to `"debug": false`
2. Replace `config/logback.xml` with `config/logback-info.xml`
3. Restart Unix Monitor
