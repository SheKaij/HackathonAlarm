package timecheck

import (
	"fmt"
	"time"

	"google.golang.org/appengine"
	"google.golang.org/appengine/datastore"
)

type Alarm struct {
	TimeH    int      `json:"timeH"`
	TimeM    int      `json:"timeM"`
	Sequence []string `json:"sequence"`
	Defused  bool     `json:"defused"`
}

func cronjob() {
	currentTime := time.Now()
	fmt.Println(currentTime)

	ctx := appengine.BackgroundContext()
	query := datastore.NewQuery("Alarm")
	var alarms []Alarm
	query.GetAll(ctx, &alarms)
	if alarms == nil {
		alarms = []Alarm{}
	}

	for _, element := range alarms {
		if element.Defused && element.TimeH == currentTime.Hour() && element.TimeM == currentTime.Minute() {
			fmt.Println("Setting defused to false")
			element.Defused = false
			updateAlarm(element)
		}

		if !element.Defused && element.TimeH == currentTime.Hour() && element.TimeM >= currentTime.Minute()+1 {
			fmt.Println("Setting defused to true")
			element.Defused = true
			updateAlarm(element)
			fmt.Println("Paying")
			// pay(0.1)
		}
	}
}

func updateAlarm(alarm Alarm) {
	// ctx := appengine.BackgroundContext()
	// var put_key *datastore.Key

	// put_key, err := datastore.Put(ctx, datastore.NewIncompleteKey(ctx, "Alarm", nil), &alarm)
	_, err := datastore.SaveStruct(&alarm)
	if err != nil {
		// http.Error(w, err.Error(), 500)
		return
	}
}
