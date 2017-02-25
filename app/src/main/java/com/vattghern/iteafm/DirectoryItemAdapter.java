package com.vattghern.iteafm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DirectoryItemAdapter extends ArrayAdapter<DirectoryItem> {

  protected boolean isCheckBoxVisibility = false;
  private List<DirectoryItem> list;
  private Context context;
  private LayoutInflater inflater;
  private boolean[] selectedItems;

  public DirectoryItemAdapter(Context context, int resource) {
    super(context, resource);
    list = new ArrayList<>();
    this.context = context;
    inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  public void updateList(List<DirectoryItem> list) {
    this.list.clear();
    this.list.addAll(list);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return list.size();
  }

  public List<DirectoryItem> getList() {
    return list;
  }

  @Nullable
  @Override
  public DirectoryItem getItem(int position) {
    return list.get(position);
  }

  @NonNull
  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    View view = convertView;
    if (view == null) {
      view = inflater.inflate(R.layout.layout_list_item, parent, false);
    }

    ImageView ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
    ImageView ivImageType = (ImageView) view.findViewById(R.id.ivIconType);

    boolean imageSet = false;
    final DirectoryItem file = list.get(position);
    if (new File(file.getFilepath()).isDirectory()) {
      ivIcon.setImageResource(R.drawable.folder);
      Glide.with(context)
          .load(R.drawable.folder)
          .centerCrop()
          .override(80, 80)
          .into(ivIcon);
      ivImageType.setVisibility(View.GONE);
      imageSet = true;
    } else {
      if (file.getType().equalsIgnoreCase(".htm") || file.getType().equalsIgnoreCase(".html") ||
          file.getType().equalsIgnoreCase(".htmls") || file.getType().equalsIgnoreCase(".htt") ||
          file.getType().equalsIgnoreCase(".htx") || file.getType().equalsIgnoreCase(".java") ||
          file.getType().equalsIgnoreCase(".js") || file.getType().equalsIgnoreCase(".pl") ||
          file.getType().equalsIgnoreCase(".txt") || file.getType().equalsIgnoreCase(".xml")) {
        ivImageType.setVisibility(View.GONE);
        Glide.with(context)
            .load(R.drawable.txt)
            .centerCrop()
            .into(ivIcon);
        imageSet = true;
      }
      if (file.getType().equalsIgnoreCase(".doc") || file.getType().equalsIgnoreCase(".docx") ||
          file.getType().equalsIgnoreCase(".word")) {
        ivImageType.setVisibility(View.GONE);
        Glide.with(context)
            .load(R.drawable.msword)
            .centerCrop()
            .into(ivIcon);
        imageSet = true;
      }
      if (file.getType().equalsIgnoreCase(".pdf")) {
        ivImageType.setVisibility(View.GONE);
        Glide.with(context)
            .load(R.drawable.pdf)
            .centerCrop()
            .into(ivIcon);
        imageSet = true;
      }
      if (file.getType().equalsIgnoreCase(".mid") || file.getType().equalsIgnoreCase(".mp2") ||
          file.getType().equalsIgnoreCase(".mp3") || file.getType().equalsIgnoreCase(".wav")) {
        ivImageType.setVisibility(View.GONE);
        Glide.with(context)
            .load(R.drawable.audio)
            .centerCrop()
            .into(ivIcon);
        imageSet = true;
      }
      if (file.getType().equalsIgnoreCase(".bmp")) {
        ivImageType.setVisibility(View.VISIBLE);
        ivImageType.setImageResource(R.drawable.bmp);
        Glide.with(context)
            .load(new File(file.getFilepath()))
            .override(80, 80)
            .centerCrop().centerCrop()
            .into(ivIcon);
        imageSet = true;
      }
      if (file.getType().equalsIgnoreCase(".gif")) {
        ivImageType.setVisibility(View.VISIBLE);
        ivImageType.setImageResource(R.drawable.gif);
        Glide.with(context)
            .load(new File(file.getFilepath()))
            .override(80, 80)
            .centerCrop()
            .into(ivIcon);
        imageSet = true;
      }
      if (file.getType().equalsIgnoreCase(".jpe")) {
        ivImageType.setVisibility(View.VISIBLE);
        ivImageType.setImageResource(R.drawable.jpe);
        Glide.with(context)
            .load(new File(file.getFilepath()))
            .override(80, 80)
            .centerCrop()
            .into(ivIcon);
        imageSet = true;
      }
      if (file.getType().equalsIgnoreCase(".jpeg")) {
        ivImageType.setVisibility(View.VISIBLE);
        ivImageType.setImageResource(R.drawable.jpeg);
        Glide.with(context)
            .load(new File(file.getFilepath()))
            .override(80, 80)
            .centerCrop()
            .into(ivIcon);
        imageSet = true;
      }
      if (file.getType().equalsIgnoreCase(".jpg")) {
        ivImageType.setVisibility(View.VISIBLE);
        ivImageType.setImageResource(R.drawable.jpg);
        Glide.with(context)
            .load(new File(file.getFilepath()))
            .override(80, 80)
            .centerCrop()
            .into(ivIcon);
        imageSet = true;
      }
      if (file.getType().equalsIgnoreCase(".pic")) {
        ivImageType.setVisibility(View.VISIBLE);
        ivImageType.setImageResource(R.drawable.pic);
        Glide.with(context)
            .load(new File(file.getFilepath()))
            .override(80, 80)
            .centerCrop()
            .into(ivIcon);
        imageSet = true;
      }
      if (file.getType().equalsIgnoreCase(".png")) {
        ivImageType.setVisibility(View.VISIBLE);
        ivImageType.setImageResource(R.drawable.png);
        Glide.with(context)
            .load(new File(file.getFilepath()))
            .override(80, 80)
            .centerCrop()
            .into(ivIcon);
        imageSet = true;
      }
      if (file.getType().equalsIgnoreCase(".avi") || file.getType().equalsIgnoreCase(".mjpg") ||
          file.getType().equalsIgnoreCase(".mpeg") || file.getType().equalsIgnoreCase(".mpg") ||
          file.getType().equalsIgnoreCase(".mp4")) {
        ivImageType.setVisibility(View.GONE);
        Glide.with(context)
            .load(R.drawable.video)
            .centerCrop()
            .centerCrop()
            .into(ivIcon);
        imageSet = true;
      }
      if (file.getType().equalsIgnoreCase(".zip")) {
        ivImageType.setVisibility(View.GONE);
        Glide.with(context)
            .load(R.drawable.zip)
            .centerCrop()
            .centerCrop()
            .into(ivIcon);
        imageSet = true;
      }
    }
    if (!imageSet) {
      ivImageType.setVisibility(View.GONE);
      Glide.with(context)
          .load(R.drawable.file)
          .centerCrop()
          .centerCrop()
          .into(ivIcon);
    }

    TextView tvName = (TextView) view.findViewById(R.id.tvName);
    tvName.setText(list.get(position).getName());
    TextView tvLastModified = (TextView) view.findViewById(R.id.tvLastModified);
    tvLastModified.setText(list.get(position).getLastModified()
    );
    TextView tvSize = (TextView) view.findViewById(R.id.tvSize);
    tvSize.setText("");
    if (new File(file.getFilepath()).isFile()) {
      tvSize.setText(list.get(position).getSize());
    }
    final CheckBox cbSelected = (CheckBox) view.findViewById(R.id.cbSelected);
    cbSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
          if (cbSelected.isChecked()) {
            list.get(position).setSelected(true);
          } else {
            list.get(position).setSelected(false);
          }
        }

    );
    if (isCheckBoxVisibility) {
      cbSelected.setVisibility(View.VISIBLE);
    } else {
      cbSelected.setVisibility(View.GONE);
    }
    cbSelected.setChecked(list.get(position).getSelected());
    return view;
  }
}
