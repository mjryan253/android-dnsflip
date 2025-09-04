package com.mjryan253.dnsflip

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.util.Log

class VpnManager {

    companion object {
        private const val TAG = "VpnManager"
        const val VPN_REQUEST_CODE = 101
    }

    fun startVpn(context: Context, dnsHostname: String) {
        val intent = VpnService.prepare(context)
        if (intent != null) {
            Log.d(TAG, "VPN permission required. Starting activity for result.")
            // We need to ask for permission
            (context as? Activity)?.startActivityForResult(intent, VPN_REQUEST_CODE)
        } else {
            Log.d(TAG, "VPN permission already granted. Starting service.")
            // Permission already granted
            val serviceIntent = Intent(context, DnsVpnService::class.java).apply {
                action = DnsVpnService.ACTION_CONNECT
                putExtra(DnsVpnService.EXTRA_DNS_HOSTNAME, dnsHostname)
            }
            context.startService(serviceIntent)
        }
    }

    fun stopVpn(context: Context) {
        Log.d(TAG, "Stopping VPN service.")
        val serviceIntent = Intent(context, DnsVpnService::class.java).apply {
            action = DnsVpnService.ACTION_DISCONNECT
        }
        context.startService(serviceIntent)
    }

    fun isVpnActive(): Boolean {
        return DnsVpnService.isRunning
    }

    fun getCurrentDns(): String? {
        return DnsVpnService.currentDns
    }

    // This function should be called from onActivityResult in the Activity
    fun handleVpnPermissionResult(context: Context, dnsHostname: String) {
        Log.d(TAG, "Handling VPN permission result. Starting service.")
        val serviceIntent = Intent(context, DnsVpnService::class.java).apply {
            action = DnsVpnService.ACTION_CONNECT
            putExtra(DnsVpnService.EXTRA_DNS_HOSTNAME, dnsHostname)
        }
        context.startService(serviceIntent)
    }
}
