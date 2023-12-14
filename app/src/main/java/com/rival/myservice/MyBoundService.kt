package com.rival.myservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyBoundService : Service() {
    companion object {
        private val TAG = MyBoundService::class.java.simpleName
    }
    private var isPaused = false

    // Variabel untuk menampung nilai terakhir yang dikirim ke ProgressBar
    private var lastNumberSent = 0
    fun pauseProgress() {
        isPaused = true
    }

    private var binder = MyBinder()


    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    val numberLiveData: MutableLiveData<Int> = MutableLiveData()

    fun startProgress() {
        serviceScope.launch {
            for (i in lastNumberSent..50) {
                delay(1000)
                if (!isPaused) {
                    Log.d(TAG, "Do Something $i")
                    numberLiveData.postValue(i)
                    lastNumberSent = i
                } else {
                    Log.d(TAG, "Paused at $i")
                    break
                }
            }
            Log.d(TAG, "Service dihentikan")
        }
    }


    fun resumeProgress() {
        isPaused = false
        startProgress()
    }
    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "onBind: ")
        startProgress()
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        lastNumberSent = 0
        Log.d(TAG, "onDestroy: ")
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "onUnbind: ")
        serviceJob.cancel()
        return super.onUnbind(intent)

    }

    override fun onRebind(intent: Intent) {
        super.onRebind(intent)
        lastNumberSent = numberLiveData.value ?: 0
        Log.d(TAG, "onRebind: ")
    }

    internal inner class MyBinder : Binder() {
        val getService: MyBoundService = this@MyBoundService
    }


}