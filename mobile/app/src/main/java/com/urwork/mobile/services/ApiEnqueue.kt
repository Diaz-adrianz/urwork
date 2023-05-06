package com.urwork.mobile.services

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.urwork.mobile.Login
import com.urwork.mobile.models.UserModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ApiEnqueue {
    fun <T> enqueue (ctx: Context, call: Call<T>, withSuccessMsg: Boolean = false, onResult: (T?, Int?, Throwable?) -> Unit)  {
        call.enqueue(
            object: Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    Log.e("FETCH ERR", t.toString())
                    Toast.makeText(ctx, "Something wrong with app", Toast.LENGTH_SHORT).show()

                    onResult(null,400, t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        onResult(response.body(), response.code(), null)

//                        Log.e("SUCCESS", response.toString())

                    }else if (response.code() != 200) {
                        try {
                            val errorBody = response.errorBody()?.string()
                            Log.e("ERR", response.toString())
                            val jsonObject = JSONObject(errorBody)
                            val msg = jsonObject.getString("msg")

                            Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()

                        } catch (e: Exception) {
                            Toast.makeText(ctx, "Try again later", Toast.LENGTH_SHORT).show()
                        }

                        onResult(null, response.code(), null)
                    }
                }
            }
        )
    }
}