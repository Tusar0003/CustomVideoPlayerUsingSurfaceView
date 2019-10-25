package com.example.anagramchecker.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.anagramchecker.R;
import com.example.anagramchecker.fragment.VideoFragment;
import com.example.anagramchecker.fragment.VideoListFragment;
import com.example.anagramchecker.model.Video;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private Context context;
    private List<Video> videoList;

    private OnVideoCallBack mVideoCallBack;
    public interface OnVideoCallBack {
        void onVideoClick(String path);
    }

    public VideoAdapter(Context context, List<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.single_video_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Glide.with(context)
                .load("file://" + videoList.get(i).getThumb())
                .placeholder(R.drawable.placeholder_image)
                .skipMemoryCache(false)
                .into(viewHolder.mThumbImageView);
        viewHolder.mNameTextView.setText(videoList.get(i).getName());

        viewHolder.mVideoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoCallBack = VideoListFragment.getInstance();
                mVideoCallBack.onVideoClick(videoList.get(i).getPath());
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mVideoLayout;
        private ImageView mThumbImageView;
        private TextView mNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mVideoLayout = itemView.findViewById(R.id.layout_video);
            mThumbImageView = itemView.findViewById(R.id.image_view_thumb);
            mNameTextView = itemView.findViewById(R.id.text_view_name);
        }
    }
}
