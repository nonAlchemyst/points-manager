package com.example.points_manager.presentation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.points_manager.R
import com.example.points_manager.presentation.services.TcpServerServiceCoroutine
import com.example.points_manager.presentation.viewmodels.MainActivityViewModel


class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startTcpReceiver()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.savePoint -> {
                viewModel.onContextMenuItemSelected(MainActivityViewModel.ContextMenuItem.SAVE_POINT)
                true
            }
            R.id.pointsList -> {
                viewModel.onContextMenuItemSelected(MainActivityViewModel.ContextMenuItem.POINTS_LIST)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startTcpReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(applicationContext, TcpServerServiceCoroutine::class.java))
        } else {
            startService(Intent(applicationContext, TcpServerServiceCoroutine::class.java))
        }
    }
}