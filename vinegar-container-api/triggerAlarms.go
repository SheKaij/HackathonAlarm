package main

import (
	"time"
	"net/http"
	"google.golang.org/appengine"
	"google.golang.org/appengine/datastore"
)

func triggerAlarms(w http.ResponseWriter, r *http.Request) {
	currentTime := time.Now()

	ctx := appengine.NewContext(r)
	query := datastore.NewQuery("Alarm").Filter("Defused = ", false).Filter("Processed = ", false).KeysOnly()
	keys, err := query.GetAll(ctx, nil)
	if err != nil {
		panic(err)
	}
	if len(keys) == 0 {
		return
	}

	for _, alarmKey := range keys {
		var alarm Alarm
		err = datastore.Get(ctx, alarmKey, &alarm)
		if err != nil {
			panic(err)
		}
		
		alarm_timestamp := alarm.TimeH*60 + alarm.TimeM
		current_timestamp := (currentTime.Hour()+2)*60 + currentTime.Minute() 
		if !alarm.Triggered && current_timestamp >=  alarm_timestamp {
			alarm.Triggered = true
			_, err := datastore.Put(ctx, alarmKey, &alarm)
			if err != nil {
				http.Error(w, err.Error(), 500)
				panic(err)
			}
		} else if alarm.Triggered && current_timestamp >= alarm_timestamp && current_timestamp <= (alarm_timestamp + alarm.Limit) {
			defuse := true
			for _, deviceID := range alarm.DeviceIDs {
				var triggeredDevice TriggeredDevice
				err := datastore.Get(ctx, deviceID, &triggeredDevice)
				if err != nil {
					panic(err)
				}
				if !triggeredDevice.Defused {
					defuse = false
					break
				}
			}
			if defuse {
				alarm.Defused = true
				_, err := datastore.Put(ctx, alarmKey, &alarm)
				if err != nil {
					http.Error(w, err.Error(), 500)
					panic(err)
				}
			}
		} else if alarm.Triggered && !alarm.Defused {
			pay(float32(alarm.Amount))
			alarm.Processed = true
			_, err := datastore.Put(ctx, alarmKey, &alarm)
			if err != nil {
				http.Error(w, err.Error(), 500)
				panic(err)
			}
		} else if alarm.Triggered {
			err := datastore.Delete(ctx, alarmKey)
			if err != nil {
				panic(err)
			}
		}
	}
}

