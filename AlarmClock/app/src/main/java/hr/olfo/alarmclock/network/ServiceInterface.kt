package hr.olfo.alarmclock.network

import org.json.JSONArray
import org.json.JSONObject

interface ServiceInterface {
    fun get(path: String, completionHandler: (response: JSONObject?) -> Unit)
    fun post(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit)
    fun put(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit)
    fun delete(path: String, completionHandler: (response: JSONObject?) -> Unit)
    fun getString(path: String, completionHandler: (response: String?) -> Unit)
    fun getArray(path: String, completionHandler: (response: JSONArray?) -> Unit)
}