package hr.olfo.alarmclock.network

import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONObject

class ApiService : ServiceInterface {
    val TAG = ApiService::class.java.simpleName
    val basePath = "https://vinegar-container.appspot.com/"

    override fun get(path: String, completionHandler: (response: JSONObject?) -> Unit) {
        val jsonObjReq = object : JsonObjectRequest(Method.GET, basePath + path, null,
                Response.Listener<JSONObject> { response ->
                    Log.d(TAG, "/get request OK! Response: $response")
                    completionHandler(response)
                },
                Response.ErrorListener { error ->
                    VolleyLog.e(TAG, "/get request fail! Error: ${error.message}")
                    completionHandler(null)
                }){}
        ServerGateway.instance?.addToRequestQueue(jsonObjReq, TAG)
    }

    override fun post(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit) {
        val jsonObjReq = object : JsonObjectRequest(Method.POST, basePath + path, params,
                Response.Listener<JSONObject> { response ->
                    Log.d(TAG, "/post request OK! Response: $response")
                    completionHandler(response)
                },
                Response.ErrorListener { error ->
                    VolleyLog.e(TAG, "/post request fail! Error: ${error.message}")
                    completionHandler(null)
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Content-Type", "application/json")
                return headers
            }
        }
        ServerGateway.instance?.addToRequestQueue(jsonObjReq, TAG)
    }

    override fun put(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit) {
        val jsonObjReq = object : JsonObjectRequest(Method.PUT, basePath + path, params,
                Response.Listener<JSONObject> { response ->
                    Log.d(TAG, "/put request OK! Response: $response")
                    completionHandler(response)
                },
                Response.ErrorListener { error ->
                    VolleyLog.e(TAG, "/put request fail! Error: ${error.message}")
                    completionHandler(null)
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Content-Type", "application/json")
                return headers
            }
        }
        ServerGateway.instance?.addToRequestQueue(jsonObjReq, TAG)
    }

    override fun delete(path: String, completionHandler: (response: JSONObject?) -> Unit) {
        val jsonObjReq = object : JsonObjectRequest(Method.DELETE, basePath + path, null,
                Response.Listener<JSONObject> { response ->
                    Log.d(TAG, "/delete request OK! Response: $response")
                    completionHandler(response)
                },
                Response.ErrorListener { error ->
                    VolleyLog.e(TAG, "/delete request fail! Error: ${error.message}")
                    completionHandler(null)
                }) {}
        ServerGateway.instance?.addToRequestQueue(jsonObjReq, TAG)
    }

    override fun getString(path: String, completionHandler: (response: String?) -> Unit) {
        val jsonStringReq = object : StringRequest(Method.GET, basePath + path,
                Response.Listener<String> { response ->
                    Log.d(TAG, "/get request OK! Response: $response")
                    completionHandler(response)
                },
                Response.ErrorListener { error ->
                    VolleyLog.e(TAG, "/get request fail! Error: ${error.message}")
                    completionHandler(null)
                }){}
        ServerGateway.instance?.addToRequestQueue(jsonStringReq, TAG)
    }

    override fun getArray(path: String, completionHandler: (response: JSONArray?) -> Unit) {
        val jsonArrayReq = object : JsonArrayRequest(Method.GET, basePath + path, null,
                Response.Listener<JSONArray> { response ->
                    Log.d(TAG, "/get request OK! Response: $response")
                    completionHandler(response)
                },
                Response.ErrorListener { error ->
                    VolleyLog.e(TAG, "/get request fail! Error: ${error.message}")
                    completionHandler(null)
                }){}
        ServerGateway.instance?.addToRequestQueue(jsonArrayReq, TAG)
    }
}