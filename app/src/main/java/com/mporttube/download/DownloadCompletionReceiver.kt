package com.mporttube.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mporttube.data.repository.DownloadRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DownloadCompletionReceiver : BroadcastReceiver() {

    @Inject lateinit var repository: DownloadRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != DownloadManager.ACTION_DOWNLOAD_COMPLETE) return

        val systemId = intent.getLongExtra(
            DownloadManager.EXTRA_DOWNLOAD_ID,
            -1L
        )
        if (systemId < 0L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                repository.markCompleted(systemId)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
