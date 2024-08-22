<a href="https://opensource.newrelic.com/oss-category/#community-project"><picture><source media="(prefers-color-scheme: dark)" srcset="https://github.com/newrelic/opensource-website/raw/main/src/images/categories/dark/Community_Project.png"><source media="(prefers-color-scheme: light)" srcset="https://github.com/newrelic/opensource-website/raw/main/src/images/categories/Community_Project.png"><img alt="New Relic Open Source community project banner." src="https://github.com/newrelic/opensource-website/raw/main/src/images/categories/Community_Project.png"></picture></a>


![GitHub forks](https://img.shields.io/github/forks/newrelic/newrelic-unix-monitor?style=social)
![GitHub stars](https://img.shields.io/github/stars/newrelic/newrelic-unix-monitor?style=social)
![GitHub watchers](https://img.shields.io/github/watchers/newrelic/newrelic-unix-monitor?style=social)

![GitHub all releases](https://img.shields.io/github/downloads/newrelic/newrelic-unix-monitor/total)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/newrelic/newrelic-unix-monitor)
![GitHub last commit](https://img.shields.io/github/last-commit/newrelic/newrelic-unix-monitor)
![GitHub Release Date](https://img.shields.io/github/release-date/newrelic/newrelic-unix-monitor)


![GitHub issues](https://img.shields.io/github/issues/newrelic/newrelic-unix-monitor)
![GitHub issues closed](https://img.shields.io/github/issues-closed/newrelic/newrelic-unix-monitor)
![GitHub pull requests](https://img.shields.io/github/issues-pr/newrelic/newrelic-unix-monitor)
![GitHub pull requests closed](https://img.shields.io/github/issues-pr-closed/newrelic/newrelic-unix-monitor)

# New Relic Unix Monitor
System-Level Monitoring for AIX, HP-UX, Linux, OSX/MacOS & Solaris/SunOS

## TL;DR - where's the download link?
**[Here.](https://github.com/newrelic/newrelic-unix-monitor/releases/latest) If you are just deploying and not compiling it, _please download the release instead of cloning the repo._**

## Table Of Contents
* [Disclaimer](#Disclaimer)
* [Contributing](#Contributing)
* [Requirements](#requirements)
	* [Operating Systems with available configurations](#oses-with-available-configurations)
* [Installation](#installation)
	* [Overview](#overview)
	* [`plugin.json` configuration](#pluginjson-configuration)
		* [Global settings](#global-settings)
			* [Using the EU Data Center? Click here!](#global-settings)
		* [Agent settings](#agent-settings)
		* [Proxy settings](#proxy-settings)
			* [Credential Obfuscation](#credential-obfuscation)
	* [Dashboards](#dashboards)
* [Other configurations](#other-configurations)
	* [Fix for using the WebSphere JDK](#fix-for-using-the-websphere-jdk)
	* [Enabling Debug Mode](#debug-mode)

## Disclaimer
New Relic has Open Sourced this integration to enable monitoring of this technology. This integration is provided AS-IS WITHOUT WARRANTY OR SUPPORT, although you can report issues and contribute to this integration via GitHub. 

## Contributing
We'd love to get your contributions to improve the Unix Monitor! Keep in mind when you submit your pull request, you'll need to sign the CLA via the click-through using CLA-Assistant. If you'd like to execute our corporate CLA, or if you have any questions, please drop us an email at open-source@newrelic.com.

## Requirements
* A New Relic account.
* A Unix server that you want to monitor
* Java JRE/JDK v1.8 or later
* Network access to New Relic (proxies are supported, see details below)

### OSes with Available Configurations
* AIX 7.x
* HP-UX 11.x
* Linux - All sorts, including on ARM processors (such as Raspberry Pi) and z/Linux
* OSX / MacOS 10.9 ('Mavericks') and above
* Solaris/SunOS 10.x and 11.x

## Installation

### Overview
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
8. If you don't yet have the Unix Monitor dashboards in your account, [use the Quickstarts NR1app to deploy them](#dashboards)

### `plugin.json` configuration
_Note: A full example of the possible fields in `plugin.json` can be found in `plugin-full-example.json`_

#### Global settings
* `OS` (default: `auto`): Used to determine which commands to run and how to parse them. Leave set to `auto` to have the plugin figure that out (which normally works).
* `account_id`: New Relic account ID - the 6- or 7- digit number in the URL when you're logged into the account of your choosing.
* `fedramp`: A true or false string to indicate that the target is the New Relic Fedramp-authorized endpoint.
* `insights_insert_key` (under `insights`): You must create an [Insights Insert key, as described here.](https://docs.newrelic.com/docs/insights/insights-data-sources/custom-data/insert-custom-events-insights-api#register)
* `insights_data_center` (under `insights`, default: `us`): If using the NR EU data center for your account, please change this to `eu` or `EU`. Otherwise, you can leave this alone or omit this setting entirely. {#eu-data-center}

#### Agent settings
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

#### Proxy settings
If using a proxy, the optional `proxy` object should be added to the `global` object in `plugin.json`, if its not there already.

* The available attributes are: `proxy_host`, `proxy_port`, `proxy_username` and `proxy_password`.
* The only attribute that is required in the `proxy` object is `proxy_host`.

##### Credential Obfuscation
For additional security, this integration supports the using obfuscated values for the attributes like insights_insert_key, proxy_username, proxy_password and any other attributes under the parent attribute 'agents' by appending `_obfuscated` to the attribute name and providing an obfuscated value that was produced by the [New Relic CLI](https://github.com/newrelic/newrelic-cli).

1. Prerequesite: [New Relic CLI is installed](https://github.com/newrelic/newrelic-cli#installation) on any supported platform.
    * **NOTE**: It does NOT need to be installed on the same host as the Unix Monitor. It is only used to generate the obfuscated keys, this integration handles deobfuscation independently.

2. Generate your obfuscated credentials using the following CLI command:
```
newrelic agent config obfuscate --key "OBSCURING_KEY" --value "CLEAR_TEXT_PROXY_PASSWORD"
```
In this command, `OBSCURING_KEY` can be any value you want. You can even point it at an existing environment variable. Examples:
```
newrelic agent config obfuscate --key "IUsedS0methingRand0m!" --value "proxyPassword2020!"
newrelic agent config obfuscate --key ${NEW_RELIC_CONFIG_OBSCURING_KEY} --value ${OUR_PROXY_PASSWORD}
```

3. In the `proxy` object in `plugin.json`, populate the `proxy_username_obfuscated` and `proxy_password_obfuscated` attributes with the values returned by the CLI.

4. In `pluginctl.sh`, uncomment the `NEW_RELIC_CONFIG_OBSCURING_KEY` variable, and set it to the same value or envrionment variable as you used in step 2 for `OBSCURING_KEY`.

### Dashboards

Unix Monitor Dashboards are now installed using the Quickstarts app. 

1. If you don't already have the Quickstarts app installed, [visit this page](https://newrelic.github.io/quickstarts-dashboard-library/#/installation) to learn how.
1. Once it is installed, open it up in the NR1 UI ("Quickstarts" under the "Apps" menu in the top bar)
1. In Quickstarts, find the "Unix Monitor" dashboards and open them.
1. Click "Import", which will bring up a dialog to guide you through deployment to a specific account.

--------------------------------------------------------------------------------

## Other configurations

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
This can be enabled EITHER by:
* Running `pluginctl.sh` with the `debug` at the end to start, like so:
  `./pluginctl.sh start debug` or `./pluginctl.sh restart debug`
OR
* Replacing `config/logback.xml` with `config/logback-debug.xml` and restarting the Unix monitor
(if using this method, remember to swap the files back when finished)

#### Release Notes

Date: 09-May-2023
Version: 1.0.2

The release 1.0.2 is a minor release with updated library for httpclient. This update addresses the [CVE-2020-13956](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2020-13956).

Date: 13-Jan-2023
Version: 1.0.1

The release 1.0.1 is a major release with multiple fixes and enhancements for Solaris 11.x platform, and this release has been certified on Oracle Solaris 11.4.
Please refer the [user guide](https://github.com/newrelic/newrelic-unix-monitor/blob/master/docs/NewRelic_UnixMonitor_Readme.pdf) for further details.


