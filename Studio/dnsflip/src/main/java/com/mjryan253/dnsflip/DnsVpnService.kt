package com.mjryan253.dnsflip

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.IOException

class DnsVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var vpnThread: Thread? = null

    companion object {
        const val ACTION_CONNECT = "com.mjryan253.dnsflip.ACTION_CONNECT"
        const val ACTION_DISCONNECT = "com.mjryan253.dnsflip.ACTION_DISCONNECT"
        const val EXTRA_DNS_HOSTNAME = "com.mjryan253.dnsflip.EXTRA_DNS_HOSTNAME"

        var isRunning = false
            private set
        var currentDns: String? = null
            private set
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.d("DnsVpnService", "onStartCommand with action: $action")

        when (action) {
            ACTION_CONNECT -> {
                val dnsHostname = intent.getStringExtra(EXTRA_DNS_HOSTNAME)
                if (dnsHostname != null) {
                    currentDns = dnsHostname
                    startVpn()
                } else {
                    Log.e("DnsVpnService", "DNS hostname not provided")
                    stopSelf()
                }
            }
            ACTION_DISCONNECT -> {
                stopVpn()
            }
        }
        return START_STICKY
    }

    private fun startVpn() {
        if (vpnThread != null) {
            Log.d("DnsVpnService", "VPN already running, stopping first.")
            stopVpn()
        }
        Log.d("DnsVpnService", "Starting VPN with DNS: $currentDns")

        vpnThread = Thread {
            try {
                val builder = Builder()
                builder.setSession("DNSFlip")
                builder.addAddress("192.168.50.1", 24) // Dummy address
                builder.addDnsServer(currentDns!!)
                builder.addRoute("0.0.0.0", 0) // Route all traffic to intercept DNS

                vpnInterface = builder.establish()
                if (vpnInterface != null) {
                    isRunning = true
                    Log.i("DnsVpnService", "VPN interface established.")
                    // Keep the service alive by blocking on the interface.
                    // In a real VPN, you'd read/write packets here.
                    // For a DNS-only VPN, just holding the descriptor open is enough.
                    val inputStream = vpnInterface!!.fileDescriptor
                    val stream = java.io.FileInputStream(inputStream)
                    stream.read() // This will block until the VPN is closed.
                } else {
                    Log.e("DnsVpnService", "VPN interface is null, failed to establish.")
                    stopSelf()
                }
            } catch (e: Exception) {
                Log.e("DnsVpnService", "Error in VPN thread", e)
                stopVpn()
            } finally {
                Log.d("DnsVpnService", "VPN thread finished.")
                isRunning = false
            }
        }
        vpnThread?.start()
    }

    private fun stopVpn() {
        Log.d("DnsVpnService", "Stopping VPN")
        currentDns = null
        isRunning = false
        vpnThread?.interrupt()
        try {
            vpnInterface?.close()
        } catch (e: IOException) {
            Log.e("DnsVpnService", "Error closing VPN interface", e)
        }
        vpnInterface = null
        stopSelf()
    }

    override fun onDestroy() {
        Log.d("DnsVpnService", "onDestroy called")
        stopVpn()
        super.onDestroy()
    }
}
