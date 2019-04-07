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
	DeviceID *datastore.Key 
	Defused bool
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
	DeviceIDs []*datastore.Key `json:"deviceIDs"`
	Triggered bool `json:"triggered"`
	Processed bool `json:"processed"`
}

type AlarmRequest struct {
	TimeH int `json:"timeH"`
	TimeM int `json:"timeM"`
	Limit int `json:"limit"`
	Amount int `json:"amount"`
	Devices []string `json:"devices"`
	Uid string `json:"uid"`
}

type AlarmWithID struct {
	Uid string `json:"uid"`
	TimeH int `json:"timeH"`
	TimeM int `json:"timeM"`
	DeviceIDs []*datastore.Key `json:"deviceIDs"`
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
	http.HandleFunc("/triggerAlarms", triggerAlarms)
	http.HandleFunc("/clear", clear)

	appengine.Main()
	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
		log.Printf("Defaulting to port %s", port)
	}

	log.Printf("Listening on port %s", port)
	log.Fatal(http.ListenAndServe(fmt.Sprintf(":%s", port), nil))
}


func clear(w http.ResponseWriter, r *http.Request) {
	ctx := appengine.NewContext(r)
	query := datastore.NewQuery("Alarm").KeysOnly()
	keys, err := query.GetAll(ctx, nil)
	if err != nil {
		panic(err)
	}
	datastore.DeleteMulti(ctx, keys)

	query = datastore.NewQuery("Device").KeysOnly()
	keys, err = query.GetAll(ctx, nil)
	if err != nil {
		panic(err)
	}
	datastore.DeleteMulti(ctx, keys)

	query = datastore.NewQuery("TriggeredDevice").KeysOnly()
	keys, err = query.GetAll(ctx, nil)
	if err != nil {
		panic(err)
	}
	datastore.DeleteMulti(ctx, keys)
	fmt.Fprint(w, "yo                       we cool bro")
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
	/* BUG BUG BUG BUG BUG BUG BUG PUG LOL HAHA
	 * Devices don't seem to be fetched with their key oh nooo 
	 */
	if r.Method == http.MethodPost {
		var device DeviceWithID
		decoder := json.NewDecoder(r.Body)
		err := decoder.Decode(&device)
		if err != nil {
			panic(err)
		}
		ctx := appengine.NewContext(r)
		id64, err := strconv.ParseInt(device.Uid, 10, 64)
		deviceKey := datastore.NewKey(ctx, "Device", device.Uid, id64, nil)
		fmt.Println("Fetching device's key: " + device.Uid)
		fmt.Println(deviceKey)
		if err != nil {
			panic(err)
		}
		query := datastore.NewQuery("TriggeredDevice").KeysOnly()
		triggeredDevicesKeys, err := query.GetAll(ctx, nil)
		if err != nil {
			panic(err)
		}
		fmt.Println("Devices:")
		fmt.Println(triggeredDevicesKeys)
		matchingDeviceIDs := []*datastore.Key {}
		fmt.Println("Triggered devices:")
		for _, triggeredDeviceKey := range triggeredDevicesKeys {
			var triggeredDevice TriggeredDevice
			err := datastore.Get(ctx, triggeredDeviceKey, &triggeredDevice)
			if err != nil {
				panic(err)
			}
			fmt.Println("triggeredDevice's key:")
			fmt.Println(triggeredDevice.DeviceID)
			if triggeredDevice.DeviceID.IntID() == deviceKey.IntID() {
				fmt.Println("yoss")
				matchingDeviceIDs = append(matchingDeviceIDs, triggeredDeviceKey)
				fmt.Println("Found triggeredDevice's match: " + triggeredDeviceKey.StringID())
			}
		}
		var alarms []Alarm
		query = datastore.NewQuery("Alarm")
		alarmsKeys, err := query.GetAll(ctx, &alarms)
		if err != nil {
			panic(err)
		}
		var matchingAlarm Alarm
		found := false
		for i, alarm := range alarms {
			for _, alarmDeviceID := range alarm.DeviceIDs {
				for _, matchingDeviceID := range matchingDeviceIDs {
					if alarmDeviceID.IntID() == matchingDeviceID.IntID() {
						fmt.Println("Found triggeredDevice's alarm: " + alarmsKeys[i].StringID())
						matchingAlarm = alarm
						found = true
						break
					}
				}
				if found {
					break}
				}
		}

		if matchingAlarm.Triggered {
			for _, tDeviceID := range matchingDeviceIDs {
				var tDevice TriggeredDevice
				err := datastore.Get(ctx, tDeviceID, &tDevice)
				if err != nil {
					panic(err)
				}
				fmt.Print("Defusing triggeredDevice: ")
				tDevice.Defused = true
				fmt.Print(tDevice)
				_, err = datastore.Put(ctx, tDeviceID, &tDevice)
				if err != nil {
					panic(err)
				}
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
		var registration Device
		decoder := json.NewDecoder(r.Body)
		err := decoder.Decode(&registration)
		if err != nil {
			panic(err)
		}

		ctx := appengine.NewContext(r)
		query := datastore.NewQuery("Device").Filter("Name =", registration.Name).Filter("Description =", registration.Description).Limit(1).KeysOnly()
		existingKey, err := query.GetAll(ctx, nil)
		if len(existingKey) != 0 {
			fmt.Fprint(w, strconv.FormatInt(existingKey[0].IntID(), 10))
		} else {
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
		// TODO: Fail if there is one alarm schedule for that time window
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
			DeviceIDs: []*datastore.Key {},
			Triggered: false}
		for _, deviceId := range alarmRequest.Devices {
			id64, err := strconv.ParseInt(deviceId, 10, 64)
			key := datastore.NewKey(ctx, "Device", "", id64, nil)
			tDevice := TriggeredDevice{
				DeviceID: key,
				Defused: false }
			tDeviceKey, err := datastore.Put(ctx, datastore.NewIncompleteKey(ctx, "TriggeredDevice", nil), &tDevice)
			if err != nil {
				panic(err)
			}
			alarm.DeviceIDs = append(alarm.DeviceIDs, tDeviceKey)
		}
		put_key, err := datastore.Put(ctx, datastore.NewIncompleteKey(ctx, "Alarm", nil), &alarm)
		if err != nil {
			http.Error(w, err.Error(), 500)
			return
		}

		alarmWithID := AlarmWithID{
			Uid: strconv.FormatInt(put_key.IntID(), 10),
			TimeH: alarm.TimeH,
			TimeM: alarm.TimeM,
			DeviceIDs: alarm.DeviceIDs,
			Defused: false,
			Triggered: false}

		json, err := json.Marshal(alarmWithID)
		if err != nil {
			panic(err)
		}

		fmt.Fprint(w, string(json))
	//case http.MethodPut:

	case http.MethodDelete:
		var alarmRequest AlarmRequest
		decoder := json.NewDecoder(r.Body)
		err := decoder.Decode(&alarmRequest)
		if err != nil {
			panic(err)
		}

		ctx := appengine.NewContext(r)
		uid64, err := strconv.ParseInt(alarmRequest.Uid, 10, 64)
		if err != nil {
			panic(err)
		}
		key := datastore.NewKey(ctx, "Alarm", alarmRequest.Uid, uid64, nil)
		err = datastore.Delete(ctx, key)
		if err != nil {
			panic(err)
		}
	}
}
