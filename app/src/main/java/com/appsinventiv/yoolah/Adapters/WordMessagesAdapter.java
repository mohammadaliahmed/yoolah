package com.appsinventiv.yoolah.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.appsinventiv.yoolah.Activites.ChattingScreen;
import com.appsinventiv.yoolah.Activites.PlayVideo;
import com.appsinventiv.yoolah.Activites.ViewPictures;
import com.appsinventiv.yoolah.Database.Word;
import com.appsinventiv.yoolah.Models.MessageModel;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.Constants;
import com.appsinventiv.yoolah.Utils.DownloadFile;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class WordMessagesAdapter extends RecyclerView.Adapter<WordMessagesAdapter.ViewHolder> implements Handler.Callback {
    Context context;

    List<Word> itemList;

    public int BUBBLE = 2;
    public int RIGHT_CHAT = 1;
    public int LEFT_CHAT = 0;

    private MediaPlayer mediaPlayer;
    private ViewHolder mAudioPlayingHolder;
    private int mPlayingPosition = -1;
    private Handler uiUpdateHandler = new Handler(this);
    private static final int MSG_UPDATE_SEEK_BAR = 1845;
    MessagesCallback callback;


    public WordMessagesAdapter(Context context, List<Word> itemList, MessagesCallback callback) {
        this.context = context;
        this.itemList = itemList;
        this.callback = callback;

    }

    public void setItemList(List<Word> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    @Override
    public int getItemViewType(int position) {
        Word model = itemList.get(position);
        if (model.getMessageType().equals(Constants.MESSAGE_TYPE_BUBBLE)) {
            return BUBBLE;
        } else {
            if (model.getMessageById().equals(SharedPrefs.getUserModel().getId())) {
                return RIGHT_CHAT;
            } else {
                return LEFT_CHAT;

            }
        }


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        if (viewType == RIGHT_CHAT) {
            View view = LayoutInflater.from(context).inflate(R.layout.right_chat_layout, parent, false);
            viewHolder = new ViewHolder(view);
        } else if (viewType == BUBBLE) {
            View view = LayoutInflater.from(context).inflate(R.layout.bubble_chat_layout, parent, false);
            viewHolder = new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.left_chat_layout, parent, false);
            viewHolder = new ViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Word model = itemList.get(position);
        if (getItemViewType(position) == BUBBLE) {
            viewHolder.bubbleText.setVisibility(View.VISIBLE);
            viewHolder.bubbleText.setText(model.getMessageText());
        } else {
            if (model.getMessageType().equals(Constants.MESSAGE_TYPE_IMAGE)) {
                viewHolder.image.setVisibility(View.VISIBLE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.document.setVisibility(View.GONE);

                viewHolder.audio.setVisibility(View.GONE);
                if (model.getImageUrl().contains("/storage")) {
                    Glide.with(context).load(model.getImageUrl()).into(viewHolder.image);


                } else {
                    Glide.with(context).load(AppConfig.BASE_URL_Image + model.getImageUrl()).into(viewHolder.image);

                }
                viewHolder.deletedLayout.setVisibility(View.GONE);
                viewHolder.videoLayout.setVisibility(View.GONE);
                viewHolder.location.setVisibility(View.GONE);
                viewHolder.contactLayout.setVisibility(View.GONE);
                viewHolder.oldMessageLayout.setVisibility(View.GONE);
                ;


            } else if (model.getMessageType().equals(Constants.MESSAGE_TYPE_CONTACT)) {
                viewHolder.image.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.contactLayout.setVisibility(View.VISIBLE);
                viewHolder.document.setVisibility(View.GONE);
                viewHolder.contactText.setText(model.getMessageText());
                viewHolder.audio.setVisibility(View.GONE);
                viewHolder.deletedLayout.setVisibility(View.GONE);
                viewHolder.videoLayout.setVisibility(View.GONE);

                viewHolder.location.setVisibility(View.GONE);
                viewHolder.oldMessageLayout.setVisibility(View.GONE);
                ;


            } else if (model.getMessageType().equals(Constants.MESSAGE_TYPE_TEXT)) {
                viewHolder.image.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.VISIBLE);
                viewHolder.contactLayout.setVisibility(View.GONE);
                viewHolder.document.setVisibility(View.GONE);

                viewHolder.messageText.setText(model.getMessageText());
                viewHolder.audio.setVisibility(View.GONE);
                viewHolder.deletedLayout.setVisibility(View.GONE);
                viewHolder.videoLayout.setVisibility(View.GONE);
                viewHolder.location.setVisibility(View.GONE);
                viewHolder.oldMessageLayout.setVisibility(View.GONE);
                ;


            } else if (model.getMessageType().equals(Constants.MESSAGE_TYPE_REPLY)) {
                viewHolder.image.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.VISIBLE);
                viewHolder.oldMessageLayout.setVisibility(View.VISIBLE);
                viewHolder.oldMessageText.setVisibility(View.VISIBLE);
                viewHolder.contactLayout.setVisibility(View.GONE);
                viewHolder.document.setVisibility(View.GONE);
                viewHolder.messageText.setText(model.getMessageText());

                viewHolder.audio.setVisibility(View.GONE);
                viewHolder.deletedLayout.setVisibility(View.GONE);
                viewHolder.videoLayout.setVisibility(View.GONE);
                viewHolder.location.setVisibility(View.GONE);

                for (int i = 0; i < itemList.size(); i++) {
                    if (itemList.get(i).getServerId().equals(model.getOldId())) {
                        if (itemList.get(i).getMessageType().equalsIgnoreCase(Constants.MESSAGE_TYPE_IMAGE)) {
                            viewHolder.oldMessageText.setText(itemList.get(i).getMessageByName() + ":\nPhoto");
                            viewHolder.oldMessageImage.setVisibility(View.VISIBLE);
                            Glide.with(context).load(AppConfig.BASE_URL_Image + itemList.get(i).getImageUrl()).into(viewHolder.oldMessageImage);
                        } else {
                            viewHolder.oldMessageImage.setVisibility(View.GONE);
                            viewHolder.oldMessageText.setText(itemList.get(i).getMessageByName() + ":\n" + itemList.get(i).getMessageText());
                        }
                    }
                }

            } else if (model.getMessageType().equals(Constants.MESSAGE_TYPE_LOCATION)) {
                viewHolder.image.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.location.setVisibility(View.VISIBLE);
                viewHolder.document.setVisibility(View.GONE);
                viewHolder.audio.setVisibility(View.GONE);
                viewHolder.deletedLayout.setVisibility(View.GONE);
                viewHolder.filename.setText(model.getFilename());
                viewHolder.videoLayout.setVisibility(View.GONE);
                viewHolder.contactLayout.setVisibility(View.GONE);
                viewHolder.oldMessageLayout.setVisibility(View.GONE);

                Glide.with(context).load(AppConfig.BASE_URL_Image + model.getImageUrl()).into(viewHolder.location);


            } else if (model.getMessageType().equals(Constants.MESSAGE_TYPE_DOCUMENT)) {
                viewHolder.image.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.document.setVisibility(View.VISIBLE);
                viewHolder.audio.setVisibility(View.GONE);
                viewHolder.deletedLayout.setVisibility(View.GONE);
                viewHolder.filename.setText(model.getFilename());
                viewHolder.videoLayout.setVisibility(View.GONE);
                viewHolder.location.setVisibility(View.GONE);
                viewHolder.contactLayout.setVisibility(View.GONE);
                viewHolder.oldMessageLayout.setVisibility(View.GONE);
                ;


            } else if (model.getMessageType().equals(Constants.MESSAGE_TYPE_DELETED)) {
                viewHolder.image.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.document.setVisibility(View.GONE);

                viewHolder.audio.setVisibility(View.GONE);
                viewHolder.document.setVisibility(View.GONE);

                viewHolder.deletedLayout.setVisibility(View.VISIBLE);
                viewHolder.videoLayout.setVisibility(View.GONE);
                viewHolder.location.setVisibility(View.GONE);
                viewHolder.contactLayout.setVisibility(View.GONE);
                viewHolder.oldMessageLayout.setVisibility(View.GONE);
                ;


            } else if (model.getMessageType().equals(Constants.MESSAGE_TYPE_VIDEO)) {
                viewHolder.image.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.document.setVisibility(View.GONE);
                viewHolder.videoLayout.setVisibility(View.VISIBLE);
                Glide.with(context).load(AppConfig.BASE_URL_Image + model.getImageUrl()).into(viewHolder.videoImage);

                if (model.getVideoUrl().contains("/storage")) {
                    viewHolder.videoProgress.setVisibility(View.GONE);
//                    Glide.with(context).load(AppConfig.BASE_URL_Image + model.getImageUrl()).into(viewHolder.videoImage);
                    Glide.with(context).load(AppConfig.BASE_URL_Videos + model.getVideoUrl()).into(viewHolder.videoImage);

                } else {
                    viewHolder.videoProgress.setVisibility(View.VISIBLE);
                    Glide.with(context).load(model.getVideoUrl()).into(viewHolder.videoImage);

                }

                viewHolder.audio.setVisibility(View.GONE);
                viewHolder.document.setVisibility(View.GONE);

                viewHolder.deletedLayout.setVisibility(View.GONE);
                viewHolder.location.setVisibility(View.GONE);
                viewHolder.contactLayout.setVisibility(View.GONE);
                viewHolder.oldMessageLayout.setVisibility(View.GONE);
                ;


            } else if (model.getMessageType().equals(Constants.MESSAGE_TYPE_AUDIO)) {
                viewHolder.audio.setVisibility(View.VISIBLE);
                viewHolder.image.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.document.setVisibility(View.GONE);

                viewHolder.playPause.setVisibility(View.VISIBLE);
                viewHolder.deletedLayout.setVisibility(View.GONE);
                viewHolder.videoLayout.setVisibility(View.GONE);


                viewHolder.audioTime.setText(CommonUtils.getDuration(model.getMediaTime()));

                if (position == mPlayingPosition) {
                    mAudioPlayingHolder = viewHolder;
                    updatePlayingView();
                } else {
                    updateInitialPlayerView(viewHolder);
                }
                viewHolder.location.setVisibility(View.GONE);
                viewHolder.contactLayout.setVisibility(View.GONE);
                viewHolder.oldMessageLayout.setVisibility(View.GONE);
                ;


            } else {
                viewHolder.audio.setVisibility(View.GONE);
                viewHolder.image.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.document.setVisibility(View.GONE);

                viewHolder.playPause.setVisibility(View.GONE);
                viewHolder.deletedLayout.setVisibility(View.GONE);
                viewHolder.videoLayout.setVisibility(View.GONE);
                viewHolder.location.setVisibility(View.GONE);
                viewHolder.contactLayout.setVisibility(View.GONE);
                viewHolder.oldMessageLayout.setVisibility(View.GONE);
                ;


            }

            if (ChattingScreen.participantsMap.size() > 0) {
                Glide.with(context).load(AppConfig.BASE_URL_Image + ChattingScreen.participantsMap.get(model.getMessageById()).getThumbnailUrl()).placeholder(R.drawable.ic_profile_plc).into(viewHolder.userImage);
            }
            viewHolder.name.setText(model.getMessageByName());

            viewHolder.time.setText(CommonUtils.getTimeOnly(model.getTime()));


            viewHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ViewPictures.class);
                    i.putExtra("url", model.getImageUrl());
                    context.startActivity(i);
                }
            });

            viewHolder.playPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


//                if (getItemViewType(position) == LEFT_CHAT) {
//                    if (model.getAudioUrl().contains("https://firebasestorage"))
//                        downloadAudio(model);
//                }

                    performPlayButtonClick(model, viewHolder);

                }
            });
            viewHolder.location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + model.getMessageText());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(mapIntent);
                    }
                }
            });
            viewHolder.oldMessageText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (model.getMessageType().equalsIgnoreCase(Constants.MESSAGE_TYPE_REPLY)) {
                        callback.onReplyMessageClick(model);
                    }
                }
            });

            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    callback.onDelete(model, position);


                    return false;
                }
            });
            viewHolder.image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    callback.onDelete(model, position);


                    return false;
                }
            });

            viewHolder.contactLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String num = model.getMessageText().replaceAll("\\D+", "");

                    Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num));
                    context.startActivity(i);
                }
            });

            viewHolder.playVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, PlayVideo.class);
                    i.putExtra("videoUrl", AppConfig.BASE_URL_Videos + model.getVideoUrl());
                    context.startActivity(i);
                }
            });
            viewHolder.document.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String filename = model.getFilename();
                    File applictionFile = new File(model.getDocumentUrl());

                    if (model.getDocumentUrl().contains("/storage")) {
                        if (applictionFile != null && applictionFile.exists()) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(applictionFile), getMimeType(applictionFile.getAbsolutePath()));
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            context.startActivity(intent);
                        }
                    } else {
                        DownloadFile.fromUrl(AppConfig.BASE_URL_Documents + model.getDocumentUrl(), filename);
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(applictionFile), getMimeType(applictionFile.getAbsolutePath()));
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(intent);
                        String path = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS) + "/" + filename;
                        model.setDocumentUrl(path);
                        callback.onUpdateMessage(model);
                    }

                }
            });
        }


    }

    private String getMimeType(String url) {
        String parts[] = url.split("\\.");
        String extension = parts[parts.length - 1];
        String type = null;
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private void downloadAudio(MessageModel model) {
        String audioLocalUrl = Long.toHexString(Double.doubleToLongBits(Math.random()));

        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/r";

        String filename = "" + model.getAudioUrl().substring(model.getAudioUrl().length() - 5, model.getAudioUrl().length());


        File applictionFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/" + filename + ".mp3");

        if (applictionFile != null && applictionFile.exists()) {
        } else {


            DownloadFile.fromUrl(model.getAudioUrl(), filename + ".mp3");
            String fname = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS) + "/" + filename + ".mp3";
//            callbacks.setAudioDownloadUrl(model, fname);


        }
    }

    private void updateInitialPlayerView(ViewHolder holder) {
        if (holder == mAudioPlayingHolder) {
            uiUpdateHandler.removeMessages(MSG_UPDATE_SEEK_BAR);
        }
        holder.seekBar.setProgress(0);
        holder.playPause.setImageResource(R.drawable.play_btn);
    }

    private void updatePlayingView() {
        if (mediaPlayer == null || mAudioPlayingHolder == null) return;
        mAudioPlayingHolder.seekBar.setProgress(mediaPlayer.getCurrentPosition() * 100 / mediaPlayer.getDuration());

        if (mediaPlayer.isPlaying()) {
            uiUpdateHandler.sendEmptyMessageDelayed(MSG_UPDATE_SEEK_BAR, 100);
            mAudioPlayingHolder.playPause.setImageResource(R.drawable.stop);

        } else {
            uiUpdateHandler.removeMessages(MSG_UPDATE_SEEK_BAR);
            mAudioPlayingHolder.playPause.setImageResource(R.drawable.play_btn);
        }
        mAudioPlayingHolder.audioTime.setText(CommonUtils.getDuration(mediaPlayer.getCurrentPosition()));

    }

    private void startMediaPlayer(Word model) {


        try {
            mediaPlayer = new MediaPlayer();
            try {
                if (model.getAudioUrl().contains("/storage")) {
                    mediaPlayer = MediaPlayer.create(context, Uri.parse(model.getAudioUrl()));
                } else {
                    mediaPlayer = MediaPlayer.create(context, Uri.parse(AppConfig.BASE_URL_AUDIO + model.getAudioUrl()));

                }
            } catch (Exception e) {
                e.printStackTrace();
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(model.getAudioUrl());
            }
            if (mediaPlayer == null) return;

            mediaPlayer.setOnCompletionListener(mp -> releaseMediaPlayer());
            if (mAudioPlayingHolder != null)
                mediaPlayer.seekTo(mAudioPlayingHolder.seekBar.getProgress());
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateNonPlayingView(ViewHolder holder) {
        if (holder == mAudioPlayingHolder) {
            uiUpdateHandler.removeMessages(MSG_UPDATE_SEEK_BAR);
        }
        holder.seekBar.setProgress(0);
        holder.playPause.setImageResource(R.drawable.play_btn);
    }

    private void releaseMediaPlayer() {

        if (null != mAudioPlayingHolder) {
            updateNonPlayingView(mAudioPlayingHolder);
        }
        if (null != mediaPlayer) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mPlayingPosition = -1;
    }

    public void stopPlayer() {
        if (null != mediaPlayer) {
            releaseMediaPlayer();
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case MSG_UPDATE_SEEK_BAR: {

                int percentage = mediaPlayer.getCurrentPosition() * 100 / mediaPlayer.getDuration();
                mAudioPlayingHolder.seekBar.setProgress(percentage);
                mAudioPlayingHolder.audioTime.setText(CommonUtils.getDuration(mediaPlayer.getCurrentPosition()));
                uiUpdateHandler.sendEmptyMessageDelayed(MSG_UPDATE_SEEK_BAR, 100);
                return true;
            }
        }
        return false;
    }

    public void activityBackPressed() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.stop();
            releaseMediaPlayer();

        }
    }

    private void performPlayButtonClick(Word recordingItem, ViewHolder myViewHolder) {

        int currentPosition = itemList.indexOf(recordingItem);
        if (currentPosition == mPlayingPosition) {
            // toggle between play/pause of audio
            if (mediaPlayer == null) return;
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        } else {
            // start another audio playback
            Word previousPlayObject = mPlayingPosition == -1 ? null : itemList.get(mPlayingPosition);
            mPlayingPosition = currentPosition;
            if (mediaPlayer != null) {
                if (null != mAudioPlayingHolder) {
                    if (previousPlayObject != null)
                        mAudioPlayingHolder.audioTime.setText(CommonUtils.getDuration(previousPlayObject.getMediaTime()));
                    updateNonPlayingView(mAudioPlayingHolder);
                }
                mediaPlayer.release();
            }
            mAudioPlayingHolder = myViewHolder;
            startMediaPlayer(recordingItem);
        }
        updatePlayingView();
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, time, name, audioTime;
        ImageView image, playPause, location, oldMessageImage;
        CircleImageView userImage;
        SeekBar seekBar;
        RelativeLayout audio, oldMessageLayout;
        LinearLayout deletedLayout;
        TextView filename;
        LinearLayout document;
        ImageView playVideo, videoImage;
        RelativeLayout videoLayout;
        TextView bubbleText;
        ProgressBar videoProgress;
        LinearLayout contactLayout;
        TextView contactText, oldMessageText;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            image = itemView.findViewById(R.id.image);
            time = itemView.findViewById(R.id.time);
            audio = itemView.findViewById(R.id.audio);
            userImage = itemView.findViewById(R.id.userImage);
            name = itemView.findViewById(R.id.name);
            deletedLayout = itemView.findViewById(R.id.deletedLayout);
            document = itemView.findViewById(R.id.document);
            filename = itemView.findViewById(R.id.filename);
            videoImage = itemView.findViewById(R.id.videoImage);
            videoLayout = itemView.findViewById(R.id.videoLayout);
            playPause = itemView.findViewById(R.id.playPause);
            seekBar = itemView.findViewById(R.id.seek);
            playVideo = itemView.findViewById(R.id.playVideo);
            oldMessageText = itemView.findViewById(R.id.oldMessageText);
            audioTime = itemView.findViewById(R.id.audioTime);
            bubbleText = itemView.findViewById(R.id.bubbleText);
            videoProgress = itemView.findViewById(R.id.videoProgress);
            location = itemView.findViewById(R.id.location);
            contactLayout = itemView.findViewById(R.id.contactLayout);
            contactText = itemView.findViewById(R.id.contactText);
            oldMessageLayout = itemView.findViewById(R.id.oldMessageLayout);
            oldMessageImage = itemView.findViewById(R.id.oldMessageImage);


        }
    }

    public interface MessagesCallback {
        public void onDelete(Word messageModel, int position);

        public void onUpdateMessage(Word messageModel);

        public void onReplyMessageClick(Word messageModel);
    }


}
