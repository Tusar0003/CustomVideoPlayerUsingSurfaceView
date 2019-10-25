package com.example.anagramchecker.fragment;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.anagramchecker.R;
import com.example.anagramchecker.adapter.VideoAdapter;
import com.example.anagramchecker.model.Video;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoListFragment extends Fragment implements VideoAdapter.OnVideoCallBack {

    private static VideoListFragment mVideoListFragment;

    private RecyclerView mVideoRecyclerView;
    private GridLayoutManager mGridLayoutManager;

    private List<Video> mVideoList;

    public VideoListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        mVideoListFragment = this;

        mVideoRecyclerView = view.findViewById(R.id.recycler_view_video);
        mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        mVideoRecyclerView.setLayoutManager(mGridLayoutManager);

        mVideoList = new ArrayList<>();

        fetchVideosFromGallery();

        return view;
    }

    @Override
    public void onVideoClick(String path) {
        Bundle bundle = new Bundle();
        bundle.putString("Path", path);

//        VideoFragment videoFragment = new VideoFragment();
//        videoFragment.setArguments(bundle);
//        videoFragment.show(getSupportFragmentManager(), null);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        VideoFragment videoFragment = new VideoFragment();
        videoFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.layout_main, videoFragment)
                .addToBackStack(null)
                .commit();
    }

    private void fetchVideosFromGallery() {
        Uri uri;
        Cursor cursor;
        int columnIndexData, columnIndexFolder, columnId, thumb;
        String absolutePath;
        String[] name;

        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Thumbnails.DATA};

        String orderBy = MediaStore.Images.Media.DATE_TAKEN;

        cursor = getContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
        columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        columnIndexFolder = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        columnId = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        thumb = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
//        name = String.valueOf(cursor.getColumnIndexOrThrow(MediaStore.));

        while (cursor.moveToNext()) {
            absolutePath = cursor.getString(columnIndexData);

            File sdCardRoot = Environment.getExternalStorageDirectory();
            File file = new File(sdCardRoot, absolutePath);
            name = absolutePath.split("/");

            Video video = new Video();
            video.setName(name[name.length - 1]);
            video.setSelected(false);
            video.setPath(absolutePath);
            video.setThumb(cursor.getString(thumb));

            mVideoList.add(video);
        }

        VideoAdapter adapter = new VideoAdapter(getContext(), mVideoList);
        mVideoRecyclerView.setAdapter(adapter);
    }

    public static VideoListFragment getInstance() {
        return mVideoListFragment;
    }
}
