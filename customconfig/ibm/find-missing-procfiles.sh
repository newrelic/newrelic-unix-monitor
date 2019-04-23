#!/bin/sh

echo -n "kthreadd:`pgrep kthreadd`,"; echo -n `ls -l red.txt`; echo -n "netns4:`pgrep netns4`,"; echo `ls -l blue.txt`