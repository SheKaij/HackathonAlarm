package hr.olfo.alarmclock.network

import org.json.JSONArray
import org.json.JSONObject

class ApiController constructor(serviceInjection: ServiceInterface): ServiceInterface {
    private val service: ServiceInterface = serviceInjection

    override fun get(path: String, completionHandler: (response: JSONObject?) -> Unit) {
        service.get(path, completionHandler)
    }

    override fun post(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit) {
        service.post(path, params, completionHandler)
    }

    override fun put(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit) {
        service.put(path, params, completionHandler)
    }

    override fun delete(path: String, completionHandler: (response: JSONObject?) -> Unit) {
        service.delete(path, completionHandler)
    }

    override fun getString(path: String, completionHandler: (response: String?) -> Unit) {
        service.getString(path, completionHandler)
    }

    override fun getArray(path: String, completionHandler: (response: JSONArray?) -> Unit) {
        service.getArray(path, completionHandler)
    }
}