package main

import (
	"fmt"
	"time"
	"net/http"
	"google.golang.org/appengine"
	"google.golang.org/appengine/datastore"
)

func triggerAlarms(w http.ResponseWriter, r *http.Request) {
	currentTime := time.Now()
	fmt.Println(currentTime)

	ctx := appengine.BackgroundContext()
	query := datastore.NewQuery("Alarm").Filter("Defused = ", false).KeysOnly()
	keys, err := query.GetAll(ctx, nil)
	if err != nil {
		panic(err)
	}
	if len(keys) == 0 {
		return
	}

	for _, alarmKey := range keys {
		fmt.Println(alarmKey.String())
		var alarm Alarm
		fmt.Println("Fetching alarm")
		err = datastore.Get(ctx, alarmKey, &alarm)
		if err != nil {
			panic(err)
		}

		//if !alarm.Defused && alarm.TimeH == currentTime.Hour() && alarm.TimeM >= currentTime.Minute()+1 {
		if true {
			fmt.Println("Setting defused to true")
			defuse := true
			for _, deviceID := range alarm.DeviceIDs {
				fmt.Println("DeviceID: " + deviceID.String())
				var triggeredDevice TriggeredDevice
				err := datastore.Get(ctx, deviceID, &triggeredDevice)
				if err != nil {
					panic(err)
				}
				if !triggeredDevice.Defused {
					fmt.Println(triggeredDevice.DeviceID.String() + " not defused!")
					fmt.Println(triggeredDevice)
					defuse = false
					fmt.Println("Breaking")
					break
				}
			}
			if defuse {
				fmt.Println("Defusing alarm")
				fmt.Println(alarm)
				alarm.Defused = true
				_, err := datastore.Put(ctx, alarmKey, &alarm)
				if err != nil {
					//http.Error(w, err.Error(), 500)
					panic(err)
				}
			} else {
				fmt.Println("Not defusing, triggering")
				alarm.Triggered = true
				_, err := datastore.Put(ctx, alarmKey, &alarm)
				if err != nil {
					//http.Error(w, err.Error(), 500)
					panic(err)
				}
			}
		} else {
			//pay()
			fmt.Println("Paying")
		}
	}
}

