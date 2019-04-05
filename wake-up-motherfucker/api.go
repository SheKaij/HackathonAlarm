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
	"net/http"
	"encoding/json"
	"google.golang.org/appengine"
	"google.golang.org/appengine/datastore"
	"os"
)

type Device struct {
	Uid string `json:"uid"`
	Name string `json:"name"`
	Description string `json:"description"`
}

type Configuration struct {
	Devices	[]Device `json:"devices"`
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
	http.HandleFunc("/config", configHandler)
	http.HandleFunc("/defuse", defuseHandler)
	http.HandleFunc("/register", registerHandler)
	http.HandleFunc("/state", stateHandler)
	http.HandleFunc("/alarm", alarmHandler)

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
	fmt.Fprint(w, "You donnated 12,345kr to charities, congrats!")
}

// [END indexHandler]

func configHandler(w http.ResponseWriter, r *http.Request) {
	if r.URL.Path != "/config"{
		http.NotFound(w, r)
		return
	}
	// TODO: Get config from DB
	ctx := appengine.NewContext(r)
	k := datastore.NewKey(ctx, "Device", "stringID", 0, nil)
	e := new(Device)
	if err := datastore.Get(ctx, k, e); err != nil {
		http.Error(w, err.Error(), 500)
		return
	}

	config := Configuration{
		Devices: []Device{
			Device{
				Uid: "anjfenjngr",
				Name: "Button",
				Description: "Big red panic button" },
			Device{
				Uid: "felekgrk",
				Name: "Hot wire",
				Description: "Touch go boom" }} }
	json, err := json.Marshal(config)
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
		put_key, err := datastore.Put(ctx, nil, registration)
		if err != nil {
			http.Error(w, err.Error(), 500)
			return
		}
		registration.Uid = put_key.String()
		json, err := json.Marshal(registration)
		if err != nil {
			panic(err)
		}
		fmt.Fprint(w, string(json))
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
	if r.URL.Path != "/alarm" {
		http.NotFound(w, r)
		return
	}
	switch r.Method {
	case http.MethodGet:
		// TODO: Fetch current alarm in the DB
		alarm := Alarm{
			TimeH: 8,
			TimeM: 30,
			Sequence: []string{
				"ojkj",
				"kjkjk",
				"mkjkf" } }
		json, err := json.Marshal(alarm)
		if err != nil {
			panic(err)
		}
		fmt.Fprint(w, string(json))
	case http.MethodPut:
		// TODO
	}
}
// [END gae_go111_app]
