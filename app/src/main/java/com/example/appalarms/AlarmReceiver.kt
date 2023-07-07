package com.example.appalarms


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

// Klasa ta jest odpowiedzialna za odbieranie alarmów.
// Jest to klasa dziedzicząca po BroadcastReceiver i implementująca metodę
// onReceive(context: Context, intent: Intent), która jest wywoływana po odebraniu alarmu.
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Tutaj moge dodać kod ktory wykona się po odebraniu alarmu.
        Toast.makeText(context, "Alarm!", Toast.LENGTH_SHORT).show()
    }
}
