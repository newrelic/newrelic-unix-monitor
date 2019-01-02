# Sample NRQL Queries

The following are sample NRQL queries that can be used to configure alerts/widgets in dashboards

|Resource|Description|Sample NRQL|
|----|-----|------|
|CPU Usage|This situation monitors the overall CPU utilization of the server|SELECT  average(`cpu.system`)from `unixMonitor:Stats`|
|File Space Fragmented|This situation monitors for inodes High and file system used Low, then indicates that file space is fragmented for WARNING severity|SELECT average(inodesPercentUsed)  FROM `unixMonitor:Disk`  WHERE  mountPoint='/usr'|
|File System Percent - All| File System usage|SELECT  average(used) FROM `unixMonitor:Disk`|
|File System Percent - Specified File Systems|This situation monitors all OS-related filesystems on an Unix system with MINOR severity|SELECT  average(used) FROM `unixMonitor:Disk` WHERE mountPoint = '/opt'|
|Inodes Percent|This situation monitors ALL mounted File system's Inodes  % used  on AIX system for MINOR severity|SELECT average(inodesPercentUsed)  FROM `unixMonitor:Disk|
|Process CPU Usage|This situation monitors the CPU percent utilization at 95% by all processes except kproc and swapper for WARNING Severity|SELECT average(cpu) from `unixMonitor:Process` WHERE  instance NOT LIKE '%kproc%' AND instance NOT LIKE 'swapper'|
|Process Missing|This situation detects if a critical OS-related process is not running|SELECT uniqueCount(pid)  from `unixMonitor:Process` WHERE instance='cimserver'|
|Process User|This situation detects if the  MQ ITM agent is running as root|SELECT uniqueCount(pid) from `unixMonitor:Process` WHERE instance = 'cimlistener' AND command LIKE '%LOGNAME=root%'|
|Swap Percent|This situation monitors swap space availability|select  average(`swap.total`-`swap.used`) as 'Avg Swap.Free' from `unixMonitor:Stats'|
|Zombie Processes|This situation searches for the total amount of zombie processes running|SELECT uniquecount(state) FROM `unixMonitor:Process` WHERE state='Z'|
