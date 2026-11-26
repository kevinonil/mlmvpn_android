package com.mlmvpn.scanner
import android.app.Application
/** Installs crash reporting and initializes the VPN app in every app/service process. */
class MlmApp : Application() {
    private val currentProcess: String? by lazy {
        if (android.os.Build.VERSION.SDK_INT >= 28) Application.getProcessName() else {
            val am = getSystemService(ACTIVITY_SERVICE) as android.app.ActivityManager
            am.runningAppProcesses?.firstOrNull { it.pid == android.os.Process.myPid() }?.processName
        }
    }
    override fun onCreate() {
        super.onCreate()
        if (android.os.Build.VERSION.SDK_INT >= 24 &&
            getSystemService(android.os.UserManager::class.java)?.isUserUnlocked == false
        ) return
        CrashReporter.install(this)
        com.mlmvpn.scanner.backup.BackupStage.onAppStart(this)
        com.mlmvpn.scanner.store.StoreEngines.init(this)
        com.mlmvpn.core.warp.WarpIdRelay.bind(this)
        com.mlmvpn.scanner.data.CombineEngine.bind(this)
        val mainProcess = currentProcess == packageName
        if (mainProcess) Thread({ runCatching { com.mlmvpn.scanner.data.CfIdentityFiles.sealKnown(this) } }, "seal-identities").start()
        com.mlmvpn.scanner.data.CfEdge.init(this)
        com.mlmvpn.scanner.utils.NetworkSettings.tlsUnfilter(this)
        com.mlmvpn.scanner.utils.NetworkSettings.googleFix(this)
        com.mlmvpn.scanner.utils.NetworkSettings.geminiExit(this)
        if (mainProcess) Thread({
            runCatching {
                com.mlmvpn.core.tunnel.SmartIranLists.ensureSeeded(this)
                if (com.mlmvpn.core.tunnel.SmartIranRoute.enabled(this)) com.mlmvpn.core.tunnel.SmartIranLists.refresh(this)
            }
        }, "smart-iran-lists").start()
        com.mlmvpn.scanner.utils.AppLocaleManager.init(this)
        com.mlmvpn.scanner.utils.Loc.bind(this)
        com.mlmvpn.scanner.engines.cfnet.CfNetManager.bind(this)
        com.mlmvpn.scanner.engines.cloud.CfTunnel.bind(this)
        if (mainProcess) com.mlmvpn.scanner.engines.amnezia.AmzEngine.relay = com.mlmvpn.scanner.engines.proton.ProtonStealth.relay(this)
        if (mainProcess) com.mlmvpn.scanner.ui.tunnel.TunnelController.attach(this)
        if (mainProcess) com.mlmvpn.scanner.widget.WidgetUpdater.attach(this)
        if (mainProcess) Thread({ runCatching { com.mlmvpn.scanner.data.studio.central.StudioCentralJob.ensure(this) } }, "studio-central-job").start()
        com.mlmvpn.core.tunnel.ConnectionLog.bind(java.io.File(filesDir, "tunnel.log"))
        if (mainProcess) registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            private var started = 0
            override fun onActivityStarted(a: android.app.Activity) {
                started++
                com.mlmvpn.scanner.utils.BackgroundGuard.onForeground()
            }
            override fun onActivityStopped(a: android.app.Activity) {
                started = (started - 1).coerceAtLeast(0)
                if (started == 0) {
                    Thread({ runCatching { com.mlmvpn.core.geph.GephEngine.onAppBackground() } }, "app-background").start()
                    com.mlmvpn.scanner.utils.BackgroundGuard.onBackground(this@MlmApp)
                }
            }
            override fun onActivityCreated(a: android.app.Activity, b: android.os.Bundle?) {}
            override fun onActivityResumed(a: android.app.Activity) {}
            override fun onActivityPaused(a: android.app.Activity) {}
            override fun onActivitySaveInstanceState(a: android.app.Activity, b: android.os.Bundle) {}
            override fun onActivityDestroyed(a: android.app.Activity) {}
        })
        runCatching { deleteSharedPreferences("emergency_prefs") }
    }
}
