#!/system/bin/sh

#Create parameter to remember dt2w status across reboots
if [ ! -f /data/system/evelyn/parameters/dt2w ]; then
	mkdir -p /data/system/evelyn/parameters
	touch /data/system/evelyn/parameters/dt2w
	echo 5 > /data/system/evelyn/parameters/dt2w
fi

#Set dt2w on boot
sendevent /dev/input/event3 0 1 $(cat /data/system/evelyn/parameters/dt2w)
sendevent /dev/input/event2 0 1 $(cat /data/system/evelyn/parameters/dt2w)

# Scrape in background for change in switch state
logcat -c && logcat -b events -e "double_click_light_screen_key\,[01]" | while read line
do
	if [ $(echo ${line:${#line}-2:1}) -eq 1 ]; then
		# The below two works for Xiaomi 8xx and 7xx devices
		echo 5 > /data/system/evelyn/parameters/dt2w
		sendevent /dev/input/event3 0 1 5
		sendevent /dev/input/event2 0 1 5
		#
		# Insert you shell script cmd to TURN ON dt2w
		#
	elif [ $(echo ${line:${#line}-2:1}) -eq 0 ]; then
		# The below two works for Xiaomi 8xx and 7xx devices
		echo 4 > /data/system/evelyn/parameters/dt2w
		sendevent /dev/input/event3 0 1 4
		sendevent /dev/input/event2 0 1 4
		#
		# Insert you shell script cmd to TURN OFF dt2w
		#
	fi 	
done
