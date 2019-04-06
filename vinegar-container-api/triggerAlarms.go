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
	query := datastore.NewQuery("Alarm").Filter("Triggered = ", true).KeysOnly()
	var alarm AlarmWithKey
	keys, err := query.GetAll(ctx, &alarm)
	if err != nil {
		panic(err)
	}
	if len(keys) == 0 {
		return
	}

	err = datastore.Get(ctx, keys[0], &alarm)
	if err != nil {
		panic(err)
	}

	if !alarm.Defused && alarm.TimeH == currentTime.Hour() && alarm.TimeM >= currentTime.Minute()+1 {
		fmt.Println("Setting defused to true")
		defuse := true
		for _, device := range alarm.Devices {
			if !device.Defused {
				defuse = false
				break
			}
		}
		if defuse {
			alarm.Defused = true
			_, err := datastore.Put(ctx, alarm.Key, &alarm)
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

