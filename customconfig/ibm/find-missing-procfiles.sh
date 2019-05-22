#!/bin/sh

# This script outputs info on process/file pairs.  It's associated with the "MissingProcFiles" eventType in the zlinux
# config file.  It's a little cryptic looking but the pairs shown in this example below are:
# kthreadd & red.txt
# netns4 & blue.txt
# The file names could be any path.
# The unix agent will recognize the pattern of a missing file output from ls or missing pid from pgrep.  If one of the
# pair is present but not both, then an event will be logged for the string name shown before pgrep below.
# So, kthreadd or netns4.
# This allows us to monitor when a process is not running, but only if the process should actually be running, which we
# know because the file is there from when it was running.  Or, only flagging a missing process file if the process is
# actually running.

echo -n "kthreadd:`pgrep kthreadd`,"; echo -n `ls -l red.txt`; echo -n "netns4:`pgrep netns4`,"; echo `ls -l blue.txt`