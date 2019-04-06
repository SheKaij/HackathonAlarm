/*
 * WAKE UP MOTHERFUCKER
 * AUHack 2019
 * Authors: Vinegar container left on the beach by a sandy grandma (Do not abbreviate)
*/

// [START gae_go111_app]
package main

// [START import]
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

type DeviceWithID struct {
	Uid string `json:"uid"`
	Name string `json:"name"`
	Description string `json:"description"`
}

type Alarm struct {
	TimeH int `json:"timeH"`
	TimeM int `json:"timeM"`
	Sequence []string `json:"sequence"`
}

// [END import]
// [START main_func]

func main() {
	http.HandleFunc("/", indexHandler)
	http.HandleFunc("/devices", devicesHandler)
	http.HandleFunc("/defuse", defuseHandler)
	http.HandleFunc("/register", registerHandler)
	http.HandleFunc("/state", stateHandler)
	http.HandleFunc("/alarms", alarmHandler)

	appengine.Main()
	// [START setting_port]
	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
		log.Printf("Defaulting to port %s", port)
	}

	log.Printf("Listening on port %s", port)
	log.Fatal(http.ListenAndServe(fmt.Sprintf(":%s", port), nil))
	// [END setting_port]
}

// [END main_func]

// [START indexHandler]

// indexHandler responds to requests with our greeting.
func indexHandler(w http.ResponseWriter, r *http.Request) {
	if r.URL.Path != "/" {
		http.NotFound(w, r)
		return
	}
	fmt.Fprint(w, "{\"message\":\"You donnated 12,345kr to charities, congrats!\"}")
}

// [END indexHandler]

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
		// TODO: update the alarm in the DB
		fmt.Fprint(w, "YE BOI")
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

func stateHandler(w http.ResponseWriter, r *http.Request) {
	if r.URL.Path != "/state" {
		http.NotFound(w, r)
		return
	}
	// TODO: Fetch current alarm in the DB
	alarm := Alarm{
		TimeH: 8,
		TimeM: 30,
		Sequence: []string{
			"ojkj",
			"kjkjk",
			"mkjkf" } }
	alarmState := string(alarm.TimeH) + "lol I don't know man"
	json, err := json.Marshal(alarmState)
	if err != nil {
		panic(err)
	}
	fmt.Fprint(w, string(json))
}

func alarmHandler(w http.ResponseWriter, r *http.Request) {
	if r.URL.Path != "/alarms" {
		http.NotFound(w, r)
		return
	}
	switch r.Method {
	case http.MethodGet:
		// TODO: Fetch current alarm in the DB
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
	case http.MethodPut:
		// TODO
	}
}
// [END gae_go111_app]
