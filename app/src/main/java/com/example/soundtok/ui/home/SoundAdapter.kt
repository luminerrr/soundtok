package com.example.soundtok.ui.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.soundtok.DetailActivity
import com.example.soundtok.R

class SoundAdapter(private val soundList: List<SoundList>, private val context: Context): RecyclerView.Adapter<SoundAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sound_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val soundViewModel = soundList[position]
        holder.titleView.text = soundViewModel.title
        holder.durationView.text = soundViewModel.duration
        holder.descriptionView.text = soundViewModel.description
        holder.restrictionView.text = soundViewModel.rating

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("soundId", soundViewModel.id)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return soundList.size
    }

    class ViewHolder(soundView: View): RecyclerView.ViewHolder(soundView) {
        val titleView: TextView = soundView.findViewById(R.id.soundItemTitleTv)
        val durationView: TextView = soundView.findViewById(R.id.durationSoundItemTv)
        val descriptionView: TextView = soundView.findViewById(R.id.descriptionSoundItemTv)
        val restrictionView: TextView = soundView.findViewById(R.id.restrictionSoundItemTv)
    }

}