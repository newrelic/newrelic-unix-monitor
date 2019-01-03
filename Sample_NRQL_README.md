# Sample NRQL Queries

The following are sample NRQL queries that can be used to configure alerts/widgets in dashboards

***The NRQL queries suggested here only serve as examples, they need to be modified as per deployments**

|Resource|AIX command|Description|Sample NRQL|
|----|-----|-----|-----|
|CPU Usage|lparstat 1 1|This situation monitors the overall CPU utilization of the server|SELECT  average(\`cpu.system\`)from \`unixMonitor:Stats\` FACET hostname |
|File Space Fragmented|df -v -k|This situation monitors for inodes High and file system used Low, then indicates that file space is fragmented for WARNING severity|SELECT average(inodesPercentUsed)  FROM \`unixMonitor:Disk\`  WHERE  mountPoint='/usr' AND hostname='b01avi11810417'|
|File System Percent - All| df -v -k|File System usage|SELECT  average(used) FROM \`unixMonitor:Disk\` AND hostname='b01avi11810417'|
|File System Percent - Specified File Systems|df -v -k|This situation monitors all OS-related filesystems on an Unix system with MINOR severity|SELECT  average(used) FROM \`unixMonitor:Disk\` WHERE mountPoint = '/opt' AND hostname='b01avi11810417'|
|Inodes Percent|df -v -k|This situation monitors ALL mounted File system's Inodes  % used  on AIX system for MINOR severity|SELECT average(inodesPercentUsed)  FROM `unixMonitor:Disk WHERE hostname='b01avi11810417'|
|Process CPU Usage|ps vewwwg|This situation monitors the CPU percent utilization at 95% by all processes except kproc and swapper for WARNING Severity|SELECT average(cpu) from \`unixMonitor:Process\` WHERE  instance NOT LIKE '%kproc%' AND instance NOT LIKE 'swapper' AND hostname='b01avi11810417'|
|Process Missing|ps vewwwg|This situation detects if a critical OS-related process is not running|SELECT uniqueCount(pid)  from \`unixMonitor:Process\` WHERE instance='cimserver' AND hostname='b01avi11810417'|
|Process User|ps vewwwg|This situation detects if the  MQ ITM agent is running as root|SELECT uniqueCount(pid) from \`unixMonitor:Process\` WHERE instance = 'cimlistener' AND command LIKE '%LOGNAME=root%' AND hostname='b01avi11810417'|
|Swap Percent|svmon -Ounit=KB|This situation monitors swap space availability|select  average(\`swap.total\`-\`swap.used\`) as 'Avg Swap.Free' from `unixMonitor:Stats' WHERE hostname='b01avi11810417'|
|Zombie Processes|ps vewwwg|This situation searches for the total amount of zombie processes running|SELECT uniquecount(state) FROM \`unixMonitor:Process\` WHERE state='Z' AND hostname='b01avi11810417'|
|Errpt Errors | errpt |Entries of CLASS Hardware with TYPE of PEND or PERM in errpt or a particular message|SELECT uniqueCount(instance) from \`unixMonitor:Errpt\` where class='S' and type='P' FACET hostname |
|Backup errors| lsmksysb -B| errors encountered during backup| SELECT uniqueCount(\`orignal.date\`) FROM \`unixMonitor:Backup\` WHERE device='/mksysbimg/mksysb.b01avi11810415.aix710.123018' AND command LIKE '%error%' AND hostname='b01avi11810417'|
|Ethernet Channel Errors|entstat 'device'|Monitors for Ethernet Errors|select uniqueCount(instance)from \`unixMonitor:NetworkIO\` WHERE \`receive.errors\` >0 OR \`transmit.errors\`>0 AND hostname='b01avi11810417'|
|File exists|ls -l| checks availability of the configured file |select uniqueCount(\`file.date\`) from \`unixMonitor:File\` WHERE  \`file.path\`='/opt/New_Relic/newrelic-unix-monitor/config/plugin.json' FACET hostname |
||||