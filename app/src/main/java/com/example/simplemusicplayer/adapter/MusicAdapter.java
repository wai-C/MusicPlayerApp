package com.example.simplemusicplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplemusicplayer.MusicActivity;
import com.example.simplemusicplayer.databinding.CardSongBinding;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder>{

    Context context;
    ArrayList<String> list;

    public MusicAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //parent is our Activity
        return new MusicViewHolder(CardSongBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String filePath = list.get(position);
        Log.e("File path: ",filePath);
        String title = filePath.substring(filePath.lastIndexOf("/")+1); // /+1 => first letter after /
        holder.songBinding.textViewCardSongName.setText(title);
        holder.songBinding.cardCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iMusic = new Intent(context, MusicActivity.class);
                iMusic.putExtra("file_path",filePath);
                iMusic.putExtra("title",title);
                iMusic.putExtra("position",position);
                iMusic.putExtra("list",list);

                context.startActivity(iMusic);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder {
        private CardSongBinding songBinding;
        public MusicViewHolder(CardSongBinding songBinding) {
            super(songBinding.getRoot());
            this.songBinding = songBinding;
        }
    }
}
