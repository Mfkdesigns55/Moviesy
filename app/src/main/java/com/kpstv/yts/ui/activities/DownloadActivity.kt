package com.kpstv.yts.ui.activities

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.api.load
import com.danimahardhika.cafebar.CafeBar
import com.kpstv.common_moviesy.extensions.Coroutines
import com.kpstv.common_moviesy.extensions.viewBinding
import com.kpstv.yts.AppInterface.Companion.EMPTY_QUEUE
import com.kpstv.yts.AppInterface.Companion.MODEL_UPDATE
import com.kpstv.yts.AppInterface.Companion.PAUSE_JOB
import com.kpstv.yts.AppInterface.Companion.PENDING_JOB_UPDATE
import com.kpstv.yts.AppInterface.Companion.REMOVE_CURRENT_JOB
import com.kpstv.yts.AppInterface.Companion.UNPAUSE_JOB
import com.kpstv.yts.AppInterface.Companion.formatDownloadSpeed
import com.kpstv.yts.AppInterface.Companion.setAppThemeNoAction
import com.kpstv.yts.R
import com.kpstv.yts.adapters.JobQueueAdapter
import com.kpstv.yts.adapters.PauseAdapter
import com.kpstv.yts.data.db.repository.PauseRepository
import com.kpstv.yts.data.models.Torrent
import com.kpstv.yts.data.models.TorrentJob
import com.kpstv.yts.data.models.response.Model
import com.kpstv.yts.databinding.ActivityDownloadBinding
import com.kpstv.yts.extensions.hide
import com.kpstv.yts.extensions.show
import com.kpstv.yts.extensions.utils.AppUtils
import com.kpstv.yts.extensions.utils.AppUtils.Companion.getMagnetUrl
import com.kpstv.yts.receivers.CommonBroadCast
import com.kpstv.yts.ui.dialogs.AlertNoIconDialog
import com.kpstv.yts.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class DownloadActivity : AppCompatActivity() {

    companion object {
        fun calculateCurrentSize(torrentJob: TorrentJob): String? {
            val currentSize =
                (torrentJob.progress.toLong() * (torrentJob.totalSize as Long)) / (100).toLong()
            return AppUtils.getSizePretty(currentSize)
        }

    }

    val viewModel by viewModels<MainViewModel>()
    private val binding by viewBinding(ActivityDownloadBinding::inflate)

    @Inject
    lateinit var pauseRepository: PauseRepository

    private val TAG = "DownloadActivity"
    private lateinit var adapter: JobQueueAdapter
    private lateinit var currentModel: TorrentJob

    private lateinit var pauseAdapter: PauseAdapter
    private var PAUSE_BUTTON_CLICKED = false

    private val SHOW_LOG_FROM_THIS_CLASS = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppThemeNoAction(this)

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        title = "Download Queue"

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.layoutPauseQueue.hide()

        emptyQueue()

        binding.recyclerViewDownload.layoutManager = LinearLayoutManager(this)

        handleReceiver(intent?.action, intent)

        bindUI()

        registerLocalBroadcast()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun bindUI() = Coroutines.main {
        binding.recyclerViewPause.layoutManager = LinearLayoutManager(this)
        pauseAdapter = PauseAdapter(applicationContext, ArrayList())

        pauseAdapter.setOnMoreListener = { v, model, i ->
            val popupMenu = PopupMenu(this, v)
            popupMenu.inflate(R.menu.activity_download_pause_menu)
            popupMenu.setOnMenuItemClickListener {
                handlePauseMenu(it, model, i)
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }

        binding.recyclerViewPause.adapter = pauseAdapter
        viewModel.pauseMovieJob.await().observe(this, Observer {
            pauseAdapter.updateModels(it)
            if (pauseAdapter.itemCount > 0) {
                binding.layoutPauseQueue.show()
            } else
                binding.layoutPauseQueue.hide()
            DA_LOG("Updating models")
        })
    }

    private fun registerLocalBroadcast() {
        val filter = IntentFilter(MODEL_UPDATE)
        filter.addAction(PENDING_JOB_UPDATE)
        filter.addAction(EMPTY_QUEUE)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            handleReceiver(intent?.action, intent)
        }
    }

    private fun handleReceiver(action: String?, intent: Intent?) {
        when (action) {
            MODEL_UPDATE -> {
                try {
                    DA_LOG("--- MODAL_UPDATE ---")

                    val parcelableObject = intent?.getSerializableExtra("model")
                    Log.e(TAG, "Class: ${parcelableObject?.javaClass ?: "null"}")
                    if (parcelableObject is TorrentJob) {
                        val model: TorrentJob = intent.getSerializableExtra("model") as TorrentJob
                        if (!::currentModel.isInitialized) {
                            updateCurrentModel(intent, model)
                        } else if (currentModel.title == model.title) {
                            updateCurrentModel(intent, model, true)
                        } else updateCurrentModel(intent, model)
                        binding.layoutJobCurrentQueue.visibility = View.VISIBLE
                    }

                } catch (e: Exception) {

                    /** For some reasons it is throwing
                     * 'java.lang.RuntimeException: Parcelable encountered IOException reading a Serializable object (name = com.kpstv.yts.data.models.TorrentJob'
                     *
                     *  Well i've to figure it out // TODO: Fix this
                     *  Edit: Seems like its fixed, but still needs some testing
                     *  */
                    DA_LOG(e.message ?: "null")
                    e.printStackTrace()

                }
            }
            PENDING_JOB_UPDATE -> {

                DA_LOG("--- PENDING_JOB_UPDATE ---")

                pendingJobUpdate(intent)
            }
            EMPTY_QUEUE -> {

                DA_LOG("--- EMPTY_QUEUE ---")

                emptyQueue()
            }
        }
    }

    private fun updateCurrentModel(
        intent: Intent?,
        torrentJob: TorrentJob,
        justUpdateStatus: Boolean = false
    ) {
        binding.layoutJobEmptyQueue.hide()

        binding.itemTorrentDownload.itemStatus.text = torrentJob.status
        binding.itemTorrentDownload.itemProgress.text = "${torrentJob.progress}%"
        binding.itemTorrentDownload.itemSeedsPeers.text = "${torrentJob.seeds}/${torrentJob.peers}"

        /** Calculate total size based on progress */

        var totalSize = torrentJob.totalSize;
        totalSize ?: kotlin.run { totalSize = 0 }

        binding.itemTorrentDownload.itemCurrentSize.text =
            calculateCurrentSize(
                torrentJob
            )

        binding.itemTorrentDownload.itemDownloadSpeed.text =
            formatDownloadSpeed(torrentJob.downloadSpeed)
        binding.itemTorrentDownload.itemProgressBar.progress = torrentJob.progress
        binding.itemTorrentDownload.itemTotalSize.text =
            AppUtils.getSizePretty(torrentJob.totalSize)

        pendingJobUpdate(intent)

        if (!justUpdateStatus) {
            currentModel = torrentJob

            binding.itemTorrentDownload.itemImage.load(currentModel.bannerUrl)

            binding.itemTorrentDownload.itemTitle.text = currentModel.title

            binding.itemTorrentDownload.itemMoreImageView.setOnClickListener {
                val menu = PopupMenu(this, binding.itemTorrentDownload.itemMoreImageView)
                menu.inflate(R.menu.item_torrrent_menu)
                menu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.action_pause -> {

                            /** Since live data fetching takes time our noJobLayout doesn't wait for it.
                             *  Hence, we are use a boolean to identify a pause click.
                             */
                            PAUSE_BUTTON_CLICKED = true

                            val i = Intent(PAUSE_JOB)
                            i.putExtra("model", currentModel)
                            LocalBroadcastManager.getInstance(this@DownloadActivity)
                                .sendBroadcast(i)
                        }
                        R.id.action_copy_magnet -> {
                            val service =
                                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            service.setPrimaryClip(
                                ClipData.newPlainText(
                                    "Magnet Url",
                                    getMagnetUrl(
                                        currentModel.magnetHash,
                                        currentModel.title.toLowerCase().replace(" ", "-")
                                    )
                                )
                            )
                            CafeBar.Builder(this@DownloadActivity)
                                .floating(true)
                                .content("Copied to clipboard")
                                .duration(CafeBar.Duration.SHORT)
                                .show()
                        }
                        R.id.action_cancel -> {
                            val i = Intent(REMOVE_CURRENT_JOB)
                            i.putExtra("model", currentModel)

                            AlertNoIconDialog.Companion.Builder(this).apply {
                                setTitle(getString(R.string.ask_title))
                                setMessage(getString(R.string.ask_delete))
                                setPositiveButton(getString(R.string.yes)) {
                                    i.putExtra("deleteFile", true)
                                    actionCancel(i)
                                }
                                setNegativeButton(getString(R.string.no)) {
                                    i.putExtra("deleteFile", false)
                                    actionCancel(i)
                                }
                            }.show()
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
                menu.show()
            }
        }
    }

    private fun pendingJobUpdate(intent: Intent?) {

        val jobs = intent?.getSerializableExtra("models") ?: return

        val models: ArrayList<Torrent> = jobs as ArrayList<Torrent>

        DA_LOG("### JOB QUEUE: ${models.size}")

        if (models.size > 0) {

            if (::adapter.isInitialized && adapter.itemCount == models.size) return

            adapter = JobQueueAdapter(this@DownloadActivity, models)

            adapter.setCloseClickListener(object : JobQueueAdapter.CloseClickListener {
                override fun onClick(model: Torrent, pos: Int) {
                    models.removeAt(pos)
                    adapter.notifyItemRemoved(pos)

                }
            })

            binding.recyclerViewDownload.adapter = adapter
            binding.layoutJobPendingQueue.visibility = View.VISIBLE
        } else binding.layoutJobPendingQueue.visibility = View.GONE
    }

    private fun handlePauseMenu(it: MenuItem, model: Model.response_pause, i: Int) {
        when (it.itemId) {
            R.id.action_unpause -> {
                val intent = Intent(this, CommonBroadCast::class.java)
                intent.action = UNPAUSE_JOB
                intent.putExtra("model", model.torrent!!)
                sendBroadcast(intent)
            }
            R.id.action_deleteJob -> {
                AlertNoIconDialog.Companion.Builder(this).apply {
                    setTitle(getString(R.string.ask_title))
                    setMessage(getString(R.string.ask_delete))
                    setPositiveButton(getString(R.string.yes)) {
                        model.saveLocation?.let {
                            AppUtils.deleteRecursive(File(it))
                        }
                        pauseRepository.deletePause(model.hash)
                        decideToShowNoJobLayout()
                    }
                    setNegativeButton(getString(R.string.no)) {
                        pauseRepository.deletePause(model.hash)
                        decideToShowNoJobLayout()
                    }
                }.show()
            }
        }
    }

    private fun decideToShowNoJobLayout() {
        if (pauseAdapter.itemCount <= 0 && ::adapter.isInitialized && adapter.itemCount <= 0 && binding.layoutJobCurrentQueue.visibility == View.GONE) {
            binding.layoutJobEmptyQueue.show()
            return
        }
    }

    private fun actionCancel(i: Intent) {
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(i)
        binding.layoutJobCurrentQueue.visibility = View.GONE
        showJobEmptyQueue()
    }

    private fun emptyQueue() {

        DA_LOG("## EMPTYING QUEUE")

        showJobEmptyQueue()
        binding.layoutJobPendingQueue.visibility = View.GONE
        binding.layoutJobCurrentQueue.visibility = View.GONE
    }

    private fun showJobEmptyQueue() {

        if (PAUSE_BUTTON_CLICKED) {
            PAUSE_BUTTON_CLICKED = false
            return
        }

        if (::pauseAdapter.isInitialized && pauseAdapter.itemCount <= 0) {
            DA_LOG("pauseAdapter: ${pauseAdapter.itemCount}")
            binding.layoutJobEmptyQueue.show()
        } else binding.layoutJobEmptyQueue.show()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy()
    }

    private fun DA_LOG(message: String) {
        if (SHOW_LOG_FROM_THIS_CLASS)
            Log.e(TAG, message)
    }
}
