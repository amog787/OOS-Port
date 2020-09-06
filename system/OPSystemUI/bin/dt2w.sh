#!/system/bin/sh

# Scrape in background for change in switch state
logcat -c && logcat -b events -e "double_click_light_screen_key\,[01]" | while read line
do
	if [ $(echo ${line:${#line}-2:1}) -eq 1 ]; then
		# The below two works for Xiaomi 8xx and 7xx devices
		sendevent /dev/input/event3 0 1 5
		sendevent /dev/input/event2 0 1 5
		#
		# Insert you shell script cmd to TURN ON dt2w
		#
	elif [ $(echo ${line:${#line}-2:1}) -eq 0 ]; then
		# The below two works for Xiaomi 8xx and 7xx devices
	 	sendevent /dev/input/event3 0 1 4
		sendevent /dev/input/event2 0 1 4
		#
		# Insert you shell script cmd to TURN OFF dt2w
		#
	fi 	
done
