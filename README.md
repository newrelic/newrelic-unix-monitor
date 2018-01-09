# New Relic Plugin for Unix
## System-Level Montoring for AIX, Linux, Mac OS X & Solaris/SunOS

### Download the plugin here: [newrelic_unix_plugin-distribution.tar.gz](/target/newrelic_unix_plugin-distribution.tar.gz?raw=true)

### What's new in V4.0?

* **COMPLETE REWRITE** to now use config files for all commands! (TODO: Inject detail here!)
* Moved to the 21st century (Ant -> Maven)

### Previous updates

#### V3.6

* Fixed pluginctl.sh (now works on Solaris!)
* Changed metric units from '%' to 'percent' to fix dashboard issues
* Fix for AIX disk & memory reporting
* Fix for OSX disk & memory reporting
* Fix for Linux disk reporting
* Fix for IBM WebSphere JDK for IBM JSSE when the WebSphere Socket factory "classNotFound" exception. [Click here for details on how to apply fix](#ibmjsse)

#### V3.5

* New Summary Metrics (CPU, Memory & Fullest Disk) to closer match New Relic Servers and to facilitate alerting

### NOTE: If all of your Summary Metrics disappeared like this (below), UPGRADE TO THE LATEST VERSION!
![Pic of Busted Summary](/img/you_need_to_upgrade.png?raw=true "Summary Prior To Upgrade")

#### V3.4

* Automatic locating of Java & plugin dir
* Automatic copying of the plugin.json template for your OS

#### V3.3
* [Global configurations in plugin.json](#globalconf)
* Bug fixes for Linux & OSX commands
* More accurate data collection from commands that have produce a "per interval" measurement (i.e. iostat, vmstat)

#### V3.0

* MAC OS X support!
  * Tested on Yosemite (10.11), please let me know if it works for you on other OS X versions.
* Support for setting your own hostname
* Support for "netstat" and "ps" commands on all platforms
* MUCH improved parsing of commands, now using regex

* Plugin has been upgraded to V2.0.1 of the New Relic Platform Java SDK, which adds a few key features:
  * 'newrelic.properties' file is now 'newrelic.json'
  * Plugin configuration is now done through 'plugin.json'
  * Logging configuration has been simplified and consolidated to 'newrelic.json'
  * HTTP/S proxies are now supported using 'newrelic.json'. [Click here for proxy config details](#proxyconfig)

----

### Requirements

- A New Relic account. Sign up for a free account [here](http://newrelic.com)
- A unix server that you want to monitor
- Java Runtime (JRE) environment Version 1.6 or later
- Network access to New Relic (proxies are supported, see details below)

----

### Installation & Usage Overview

1. Download the latest version of the agent.
2. Gunzip & untar on Unix server that you want to monitor
3. Configure `config/newrelic.json`
  * [Click here for newrelic.json config details](#nrjson)
4. OPTIONAL: Copy `config/plugin.json` from the OS-specific templates in `config` and configure that file.
  * [Click here for plugin.json config details](#pluginjson)
5. OPTIONAL: Configure `pluginctl.sh` to have the correct paths to Java and your plugin location
  * Set `PLUGIN_JAVA` to the location of Java on your server (including the "java" filename)
  * Set `PLUGIN_PATH` to the location of the Unix Plugin
6. Run `chmod +x pluginctl.sh` to make the startup script executable (if it isn't already)
7. Run `./pluginctl.sh start` from command-line
8. Check logs (in `logs` directory by default) for errors
9. Login to New Relic UI and find your data in Insights.
  * In the data explorer, look for custom event types that start with "unixMonitor:"
  * Possible event types (for out-of-the-box commands): unixMonitor:Disk, unixMonitor:DiskIO, unixMonitor:NetworkIO, unixMonitor:Process, unixMonitor:Stats, unixMonitor:Vmstat
   
----

### <a name="nrjson"/>Configuring the `newrelic.json` file

The `newrelic.json` is a standardized file containing configuration information that applies to any plugin (e.g. license key, logging, proxy settings), so going forward you will be able to copy a single `newrelic.json` file from one plugin to another.  Below is a list of the configuration fields that can be managed through this file:

#### Configuring your New Relic License Key

* Your New Relic license key is the only required field in the `newrelic.json` file as it is used to determine what account you are reporting to.
* Your license key can be found in New Relic UI, on 'Account settings' page.

##### Example

```
{
  "license_key": "YOUR_LICENSE_KEY_HERE"
}
```

#### Logging configuration

By default, this plugins will have logging turned on; however, you can manage these settings with the following configurations:

* `log_level` - The log level. Valid values: [`debug`, `info`, `warn`, `error`, `fatal`]. Defaults to `info`.
	* `debug` will expose the metrics being collected by each command.
* `log_file_name` - The log file name. Defaults to `newrelic_plugin.log`.
* `log_file_path` - The log file path. Defaults to `logs`.
* `log_limit_in_kbytes` - The log file limit in kilobytes. Defaults to `25600` (25 MB). If limit is set to `0`, the log file size will not be limited.

##### Example

```
{
  "license_key": "YOUR_LICENSE_KEY_HERE"
  "log_level": "info",
  "log_file_path": "/var/log/newrelic",
  "log_limit_in_kbytes": "4096"
}
```

#### <a name="proxyconfig"/>Proxy configuration

If you are running your plugin from a machine that runs outbound traffic through a proxy, you can use the following optional configurations in your `newrelic.json` file:

* `proxy_host` - The proxy host (e.g. `webcache.example.com`)
* `proxy_port` - The proxy port (e.g. `8080`).  Defaults to `80` if a `proxy_host` is set
* `proxy_username` - The proxy username
* `proxy_password` - The proxy password

##### Examples

```
{
  "license_key": "YOUR_LICENSE_KEY_HERE",
  "proxy_host": "proxy.mycompany.com",
  "proxy_port": 9000
}
```

```
{
  "license_key": "YOUR_LICENSE_KEY_HERE",
  "proxy_host": "proxy.mycompany.com",
  "proxy_port": "9000",
  "proxy_username": "my_user",
  "proxy_password": "my_password"
}
```

----

### <a name="pluginjson"/>Configuring the `plugin.json` file

The `plugin.json` file contains the list of OS level commands that you want to execute as part of the plugin, and global settings to apply across all commands. All current possibilities for each OS are found in the `config/plugin.json.[OS]` template files.
To set up the agent for your OS, copy one of these templates to `plugin.json`. If you don't do this, the plugin will do it for you the first time it is run.

Each command will get its own object in the `agents` array, as seen in the Example below.
`command` is the only required configuration for each object. Commands in lowercase are ones literally defined in the plugin (i.e. `iostat`), whereas commands in Caps are specialized variations on those commands (i.e. `IostatCPU`).

#### <a name="globalconf"/>Global Configurations

Each plugin.json file now has a `global` object, which contains the optional configurations to be applied across all of the commands.

* Configurations in the `agents` array override what's in the `global` object.
  - I.e. If you want to turn on debug for one statement, you can set the `debug` object to false in the `global` object, and set it to true in that command's `agent` object.
* If you choose to use the old versions of plugin.json (without a `global` option), those will work fine.

#### Optional Configurations for `plugin.json`

For each command, the following optional configurations are available:

* `OS` - The OS you are monitoring.
  - If left out, it will use the "auto" setting, in which the plugin will detect your OS type.
  - Normally the "auto" setting works fine. If not, you can define it as any of: [aix, linux, sunos, osx].
* `debug` - This is an extra debug setting to use when a specific command isn't reporting properly.
  - Enabling it will prevent metrics from being sent to New Relic.
  - Seeing metrics in logs also requires setting `"log_level": "debug"` in `newrelic.json`.
* `hostname` - To override the hostname that appears in the UI for this instance, set this option to any string that you want.
  - If you leave this option out, the plugin will obtain your hostname from the JVM (java.net.InetAddress.getLocalHost().getHostName())

#### Examples

With the optional configurations left as the defaults, this is what your plugin.json might look like:
```
{
    "global": {
        "OS": "auto",
        "debug": false,
        "hostname": "auto"  
    },
    "agents": [
        {
            "command": "df"
        },
        {
            "command": "iostat"
        },
        {
            "command": "top"
        },
        {
            "command": "vmstat"
        },
        {
            "command": "VmstatTotals"
        }
    ]
}
```
Here is an example with optional configurations set in the `agent` object that override the `global` settings:
```
{
       "global": {
        "OS": "auto",
        "debug": false,
        "hostname": "auto"  
    },
    "agents": [
        {
            "command": "df",
            "debug": true
        },
        {
            "command": "iostat",
            "hostname": "not auto"  
        },
        {
            "command": "top",
            "OS": "linux",
            "hostname": "also not auto"  
        },
        {
            "command": "vmstat"
        },
        {
            "command": "VmstatTotals"
        }
    ]
}
```

### <a name="ibmjsse"/>Fix for using the IBM (WebSphere) JDK

If you are using the JDK that is packaged with WebSphere see an exception in the logs like below, it is due to WebSphere attempting to use the WebSphere SSL Factory instead of the IBM JSSE packages.
```
ERROR com.newrelic.metrics.publish.binding.Request - An error occurred communicating with the New Relic service
java.net.SocketException: java.lang.ClassNotFoundException: Cannot find the specified class com.ibm.websphere.ssl.protocol.SSLSocketFactory
```

If so, uncomment the following line in `pluginctl.sh` and restart the agent.
```
# USE_IBM_JSSE=true
```
