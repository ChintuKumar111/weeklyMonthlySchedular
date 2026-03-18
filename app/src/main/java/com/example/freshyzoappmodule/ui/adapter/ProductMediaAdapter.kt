package com.example.freshyzoappmodule.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshyzoappmodule.data.model.ProductMedia
import com.example.freshyzoappmodule.databinding.ItemProductMediaBinding

class ProductMediaAdapter(private val mediaList: List<ProductMedia>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_IMAGE = 0
        private const val TYPE_VIDEO = 1
    }

    private val activeHolders = mutableSetOf<VideoViewHolder>()
    private var currentPosition: Int = -1

    override fun getItemViewType(position: Int): Int {
        return if (mediaList[position].isVideo) TYPE_VIDEO else TYPE_IMAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemProductMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return if (viewType == TYPE_IMAGE) ImageViewHolder(binding) else VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val media = mediaList[position]
        if (holder is ImageViewHolder) {
            holder.bind(media.url)
        } else if (holder is VideoViewHolder) {
            holder.bindVideo(media.url)
            activeHolders.add(holder)
            
            if (position == currentPosition) {
                holder.playPlayer()
            } else {
                holder.pausePlayer()
            }
        }
    }

    override fun getItemCount() = mediaList.size

    @Suppress("DEPRECATION")
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is VideoViewHolder) {
            activeHolders.add(holder)
            holder.lastUrl?.let { holder.bindVideo(it) }
            if (holder.adapterPosition == currentPosition) {
                holder.playPlayer()
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is VideoViewHolder) {
            holder.releasePlayer()
            activeHolders.remove(holder)
        }
    }

    @Suppress("DEPRECATION")
    fun playVideoAt(position: Int) {
        currentPosition = position
        activeHolders.forEach { holder ->
            if (holder.adapterPosition == position) {
                holder.playPlayer()
            } else {
                holder.pausePlayer()
            }
        }
    }

    fun pauseAllVideos() {
        currentPosition = -1
        activeHolders.forEach { it.pausePlayer() }
    }

    class ImageViewHolder(private val binding: ItemProductMediaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(url: String) {
            binding.ivProductImage.visibility = View.VISIBLE
            binding.playerView.visibility = View.GONE
            binding.videoProgressBar.visibility = View.GONE
            
            Glide.with(binding.ivProductImage.context)
                .load(url)
                .into(binding.ivProductImage)
        }
    }

    class VideoViewHolder(private val binding: ItemProductMediaBinding) : RecyclerView.ViewHolder(binding.root) {
        private var player: ExoPlayer? = null
        var lastUrl: String? = null

        fun bindVideo(videoUrl: String) {
            this.lastUrl = videoUrl
            binding.ivProductImage.visibility = View.GONE
            binding.playerView.visibility = View.VISIBLE
            
            if (player == null) {
                player = ExoPlayer.Builder(binding.root.context).build().also {
                    binding.playerView.player = it
                    val mediaItem = MediaItem.fromUri(videoUrl)
                    it.setMediaItem(mediaItem)
                    it.repeatMode = Player.REPEAT_MODE_ONE
                    
                    it.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(state: Int) {
                            when (state) {
                                Player.STATE_BUFFERING -> {
                                    binding.videoProgressBar.visibility = View.VISIBLE
                                }
                                Player.STATE_READY -> {
                                    binding.videoProgressBar.visibility = View.GONE
                                }
                                else -> {
                                    binding.videoProgressBar.visibility = View.GONE
                                }
                            }
                        }
                    })

                    it.prepare()
                    it.playWhenReady = false 
                }
            }
        }

        fun playPlayer() {
            player?.let {
                it.playWhenReady = true
                if (it.playbackState == Player.STATE_ENDED) {
                    it.seekTo(0)
                }
            }
        }

        fun pausePlayer() {
            player?.playWhenReady = false
        }

        fun releasePlayer() {
            player?.release()
            player = null
        }
    }
}
