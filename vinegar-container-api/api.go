/*
 * WAKE UP MOTHERFUCKER
 * AUHack 2019
 * Authors: Vinegar container left on the beach by a sandy grandma (Do not abbreviate)
*/

package main

import (
	"fmt"
	"log"
	"strconv"
	"net/http"
	"encoding/json"
	"google.golang.org/appengine"
	"google.golang.org/appengine/datastore"
	"os"
)

type Device struct {
	Name string `json:"name"`
	Description string `json:"description"`
}

type TriggeredDevice struct {
	Device Device 
	Defused bool
	Key *datastore.Key `datastore:"__key__"`
}

type DeviceWithID struct {
	Uid string `json:"uid"`
	Name string `json:"name"`
	Description string `json:"description"`
}

type Alarm struct {
	TimeH int `json:"timeH"`
	TimeM int `json:"timeM"`
	Limit int `json:"limit"`
	Amount int `json:"amount"`
	Defused bool `json:"defused"`
	Devices []TriggeredDevice `json:"devices"`
	Triggered bool `json:"triggered"`
	Key *datastore.Key `datastore:"__key__"`
}

type AlarmRequest struct {
	TimeH int `json:"timeH"`
	TimeM int `json:"timeM"`
	Limit int `json:"limit"`
	Amount int `json:"amount"`
	Devices []string `json:"devices"`
}

type AlarmWithID struct {
	Uid string `json:"uid"`
	TimeH int `json:"timeH"`
	TimeM int `json:"timeM"`
	Devices []TriggeredDevice `json:"devices"`
	Defused bool `json:"defused"`
	Triggered bool `json:"triggered"`
}

func main() {
	http.HandleFunc("/", indexHandler)
	http.HandleFunc("/devices", devicesHandler)
	http.HandleFunc("/defuse", defuseHandler)
	http.HandleFunc("/register", registerHandler)
	http.HandleFunc("/alarms", alarmHandler)
	http.HandleFunc("/state", alarmStateHandler)
	http.HandleFunc("/test", run)

	appengine.Main()
	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
		log.Printf("Defaulting to port %s", port)
	}

	log.Printf("Listening on port %s", port)
	log.Fatal(http.ListenAndServe(fmt.Sprintf(":%s", port), nil))
}


func indexHandler(w http.ResponseWriter, r *http.Request) {
	if r.URL.Path != "/" {
		http.NotFound(w, r)
		return
	}
	fmt.Fprint(w, "{\"message\":\"You donnated 12,345kr to charities, congrats!\"}")
}

func devicesHandler(w http.ResponseWriter, r *http.Request) {
	if r.URL.Path != "/devices"{
		http.NotFound(w, r)
		return
	}
	ctx := appengine.NewContext(r)

	query := datastore.NewQuery("Device")
	var devices []Device
	keys, err := query.GetAll(ctx, &devices)

	devicesWithID := []DeviceWithID {}
	for i, device := range devices {
		devicesWithID = append(devicesWithID, DeviceWithID{
			Uid: strconv.FormatInt(keys[i].IntID(), 10),
			Name: device.Name,
			Description: device.Description}) }
	json, err := json.Marshal(devicesWithID)
	if err != nil {
		panic(err)
	}
	fmt.Fprint(w, string(json))
}

func defuseHandler(w http.ResponseWriter, r *http.Request) {
	if r.URL.Path != "/defuse" {
		http.NotFound(w, r)
		return
	}
	if r.Method == http.MethodPost {
		var device DeviceWithID
		decoder := json.NewDecoder(r.Body)
		err := decoder.Decode(&device)
		if err != nil {
			panic(err)
		}
		ctx := appengine.NewContext(r)
		var tDevices []TriggeredDevice
		id64, err := strconv.ParseInt(device.Uid, 10, 64)
		deviceKey := datastore.NewKey(ctx, "Device", "", id64, nil)
		if err != nil {
			panic(err)
		}
		query := datastore.NewQuery("TriggeredDevice")
		_, err = query.GetAll(ctx, &tDevices)
		if err != nil {
			panic(err)
		}
		matchingDevices := []TriggeredDevice{}
		for _, triggeredDevice := range tDevices {
			if triggeredDevice.Key == deviceKey {
				matchingDevices = append(matchingDevices, triggeredDevice)
			}
		}
		var alarms []Alarm
		query = datastore.NewQuery("Alarm")
		_, err = query.GetAll(ctx, &alarms)
		if err != nil {
			panic(err)
		}
		var matchingAlarm Alarm
		found := false
		for _, alarm := range alarms {
			for _, device := range alarm.Devices {
				if device.Key == deviceKey {
					matchingAlarm = alarm
					found = true
					break
				}
			}
			if found {
				break
			}
		}
		if matchingAlarm.Triggered {
			for _, tDevice := range matchingDevices {
				tDevice.Defused = true
				datastore.Put(ctx, tDevice.Key, tDevice)
			}
		}
	}
}

func registerHandler(w http.ResponseWriter, r *http.Request) {
	if r.URL.Path != "/register" {
		http.NotFound(w, r)
		return
	}
	if r.Method == http.MethodPost {
		// TODO: Return existing id if same name/desc
		var registration Device
		decoder := json.NewDecoder(r.Body)
		err := decoder.Decode(&registration)
		if err != nil {
			panic(err)
		}

		ctx := appengine.NewContext(r)
		put_key, err := datastore.Put(ctx, datastore.NewIncompleteKey(ctx, "Device", nil), &registration)
		if err != nil {
			http.Error(w, err.Error(), 500)
			return
		}
		if err != nil {
			panic(err)
		}
		fmt.Fprint(w, strconv.FormatInt(put_key.IntID(), 10))
	}
}

func alarmStateHandler(w http.ResponseWriter, r *http.Request) {
	if r.URL.Path != "/state" {
		http.NotFound(w, r)
		return
	}
	ctx := appengine.NewContext(r)
	query := datastore.NewQuery("Alarm").Filter("Triggered =", true).Limit(1).KeysOnly()
	var activeAlarm Alarm
	keys, err := query.GetAll(ctx, &activeAlarm)
	if err != nil {
		panic(err)
	}
	if len(keys) == 0 {
		fmt.Fprint(w, "{}")
	} else {
		err = datastore.Get(ctx, keys[0], &activeAlarm)
		json, err := json.Marshal(activeAlarm)
		if err != nil {
			panic(err)
		}
		fmt.Fprint(w, string(json))
	}
}

func alarmHandler(w http.ResponseWriter, r *http.Request) {
	if r.URL.Path != "/alarms" {
		http.NotFound(w, r)
		return
	}
	switch r.Method {
	case http.MethodGet:
		ctx := appengine.NewContext(r)
		query := datastore.NewQuery("Alarm")
		var alarms []Alarm
		_, err := query.GetAll(ctx, &alarms)
		if alarms == nil {
			alarms = []Alarm {}
		}
		
		json, err := json.Marshal(alarms)
		if err != nil {
			panic(err)
		}
		fmt.Fprint(w, string(json))
	case http.MethodPost:
		var alarmRequest AlarmRequest
		decoder := json.NewDecoder(r.Body)
		err := decoder.Decode(&alarmRequest)
		if err != nil {
			panic(err)
		}

		ctx := appengine.NewContext(r)
		alarm := Alarm{
			TimeH: alarmRequest.TimeH,
			TimeM: alarmRequest.TimeM,
			Limit: alarmRequest.Limit,
			Amount: alarmRequest.Amount,
			Devices: []TriggeredDevice {},
			Triggered: false}
		for _, deviceId := range alarmRequest.Devices {
			id64, err := strconv.ParseInt(deviceId, 10, 64)
			key := datastore.NewKey(ctx, "Device", "", id64, nil)
			var device Device
			err = datastore.Get(ctx, key, &device)
			if err != nil {
				panic(err)
			}
			tDevice := TriggeredDevice{
				Device: device,
				Defused: false }
			_, err = datastore.Put(ctx, datastore.NewIncompleteKey(ctx, "TriggeredDevice", nil), &tDevice)
			if err != nil {
				panic(err)
			}
			alarm.Devices = append(alarm.Devices, tDevice)
		}
		var put_key *datastore.Key
		put_key, err = datastore.Put(ctx, datastore.NewIncompleteKey(ctx, "Alarm", nil), &alarm)
		if err != nil {
			http.Error(w, err.Error(), 500)
			return
		}

		alarmWithID := AlarmWithID{
			Uid: strconv.FormatInt(put_key.IntID(), 10),
			TimeH: alarm.TimeH,
			TimeM: alarm.TimeM,
			Devices: alarm.Devices,
			Defused: false,
			Triggered: false}

		json, err := json.Marshal(alarmWithID)
		if err != nil {
			panic(err)
		}

		fmt.Fprint(w, string(json))
	case http.MethodPut:

	}
}
