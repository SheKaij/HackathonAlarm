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
	Key *datastore.Key `datastore:"__key__"`
}


func main() {
	currentTime := time.Now()
	fmt.Println(currentTime)

	ctx := appengine.BackgroundContext()
	query := datastore.NewQuery("Alarm").Filter("Triggered = ", true).KeysOnly()
	var alarm Alarm
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
		alarm.Defused = true
		_, err := datastore.Put(ctx, alarm.Key, &alarm)
		if err != nil {
			//http.Error(w, err.Error(), 500)
			panic(err)
		}
	} else {
		//pay()
		fmt.Println("Paying")
	}
}

