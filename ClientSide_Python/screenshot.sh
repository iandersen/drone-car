#!/bin/bash
timestamp=$(date +%y-%m-%d_%H:%M:%S)
fswebcam -r 1920x1080 -S 2 --no-timestamp --no-banner "${timestamp}.png" &
