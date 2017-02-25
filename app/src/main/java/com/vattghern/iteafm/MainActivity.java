package com.vattghern.iteafm;

import static com.vattghern.iteafm.Constants.DIRECTORY_COPY_TO;
import static com.vattghern.iteafm.Constants.INTENT_COPY;
import static com.vattghern.iteafm.Constants.INTENT_MOVE;
import static com.vattghern.iteafm.Constants.PATH;
import static com.vattghern.iteafm.FileWriterReader.write;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.Drawer.OnDrawerItemClickListener;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private DirectoryItemAdapter directoryItemAdapter;
  private List items = new ArrayList<DirectoryItem>();
  private String path = "/";
  private Comparator comparator = new DirectoryItem.CompName();
  private String[] sortVariants = {"Size", "Date", "Name"};

  private List<String> horizontalList;
  private static HorizontalAdapter horizontalAdapter;

  public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

    private List<String> horizontalList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

      public TextView txtView;
      public ImageView ivRootDirectory;

      public MyViewHolder(View view) {
        super(view);
        txtView = (TextView) view.findViewById(R.id.txtView);
      }
    }

    public HorizontalAdapter(List<String> horizontalList) {
      this.horizontalList = horizontalList;
    }

    private String getQiuckPath(int index) {
      String quickPath = "/";
      for (int i = 0; i <= index; i++) {
        quickPath += horizontalList.get(i) + "/";
      }
      return quickPath;
    }

    public void updateHorizontalList(List<String> horizontalList) {
      this.horizontalList.clear();
      this.horizontalList.addAll(horizontalList);
      notifyDataSetChanged();
    }

    @Override
    public HorizontalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View itemView = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.horizontal_item_view, parent, false);
      return new HorizontalAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HorizontalAdapter.MyViewHolder holder, final int position) {
      holder.txtView.setText(horizontalList.get(position));
      holder.txtView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (position < horizontalList.size() - 1) {
            path = getQiuckPath(position);
            refreshList();
          }
        }
      });
    }

    @Override
    public int getItemCount() {
      return horizontalList.size();
    }
  }

  @BindView(R.id.tvTitle)
  TextView tvTitle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    //permissions
    ActivityCompat.requestPermissions(MainActivity.this,
                                      new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    if (getIntent().hasExtra(PATH)) {
      path = getIntent().getStringExtra(PATH);
    }
    setViews();
    horizontalAdapter = new HorizontalAdapter(getCurrentPathButtonsList());
    LinearLayoutManager horizontalLayoutManagaer =
        new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
    horizontal_recycler_view.setLayoutManager(horizontalLayoutManagaer);
    horizontal_recycler_view.setAdapter(horizontalAdapter);
    Drawer result = new DrawerBuilder().withActivity(this)
        .withHasStableIds(true)
        .addDrawerItems(new PrimaryDrawerItem().withName("Music")
                            .withIcon(R.drawable.folder)
                            .withIdentifier(1)
                            .withSelectable(false), new PrimaryDrawerItem().withName("Photos")
                            .withIcon(R.drawable.folder)
                            .withIdentifier(2)
                            .withSelectable(false), new PrimaryDrawerItem().withName("Videos")
                            .withIcon(R.drawable.folder)
                            .withIdentifier(3)
                            .withSelectable(false), new PrimaryDrawerItem().withName("Documents")
                            .withIcon(R.drawable.folder)
                            .withIdentifier(4)
                            .withSelectable(false))
        .withOnDrawerItemClickListener(new OnDrawerItemClickListener() {
          @Override
          public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            if (drawerItem != null) {
              Intent intent = null;
              if (drawerItem.getIdentifier() == 1) {
                intent = new Intent(MainActivity.this, MainActivity.class);
              }
            }
            return false;
          }
        })
        .withSavedInstance(savedInstanceState)
        .withShowDrawerOnFirstLaunch(true)
        .build();
    directoryItemAdapter = new DirectoryItemAdapter(this, R.layout.layout_list_item);

    listView.setAdapter(directoryItemAdapter);
    tvTitle.setText(path);
  }

  @Override
  protected void onResume() {
    refreshList();
    super.onResume();
  }

  @Override
  public void onBackPressed() {
    if (directoryItemAdapter.isCheckBoxVisibility) {
      directoryItemAdapter.isCheckBoxVisibility = false;
    } else if (path.equals("/")) {
      finish();
    } else {
      path = cutPath(path);
    }
    llButtons.setVisibility(View.GONE);
    refreshList();
  }

  private String cutPath(String path) {
    do {
      path = path.substring(0, path.length() - 1);
    } while (path.charAt(path.length() - 1) != '/');
    return path;
  }

  private void refreshList() {
    items.clear();
    File dir = new File(path);
    String[] list = dir.list();
    if (list != null) {
      for (String file : list) {
        if (!file.startsWith(".")) {
          items.add(new DirectoryItem(path, file, false));
        }
      }
    }
    horizontalList = getCurrentPathButtonsList();
    if (horizontalList.size() > 1) {
      horizontalList.remove(0);
    }
    horizontalAdapter.updateHorizontalList(horizontalList);
    Collections.sort(items, comparator);
    directoryItemAdapter.updateList(items);
  }

  private List<String> getCurrentPathButtonsList() {
    List<String> buttons = new ArrayList<String>(Arrays.asList(path.split("/")));
    return buttons;
  }

  private void makeNewFolder(String folder) {
    File file = null;
    boolean bool = false;
    String filepath;
    if (path.endsWith(File.separator)) {
      filepath = path + folder;
    } else {
      filepath = path + File.separator + folder;
    }
    try {
      file = new File(filepath);
      bool = file.mkdir();
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (bool) {
      refreshList();
    }
  }

  private int getCountOfSelectedItems() {
    int count = 0;
    List<DirectoryItem> list = directoryItemAdapter.getList();
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).getSelected()) {
        count++;
      }
    }
    return count;
  }

  private void moveFile(File file, File dir) throws IOException {
    if (file.isDirectory()) {
      File outputFile = new File(dir, file.getName());
      outputFile.mkdir();
      for (File f : file.listFiles()) {
        moveFile(f, outputFile);
      }
      delete(file.getPath());
    } else {
      File newFile = new File(dir, file.getName());
      FileChannel outputChannel = null;
      FileChannel inputChannel = null;
      try {
        outputChannel = new FileOutputStream(newFile).getChannel();
        inputChannel = new FileInputStream(file).getChannel();
        inputChannel.transferTo(0, inputChannel.size(), outputChannel);
        inputChannel.close();
        delete(file.getPath());
      } finally {
        if (inputChannel != null) {
          inputChannel.close();
        }
        if (outputChannel != null) {
          outputChannel.close();
        }
      }
    }
  }

  private void delete(String fileToDelete) {
    File F = new File(fileToDelete);
    if (!F.exists()) {
      return;
    }
    if (F.isDirectory()) {
      for (File file : F.listFiles()) {
        delete(file.getPath());
      }
      F.delete();
    } else {
      F.delete();
    }
  }

  private void copyFile(File file, File dir) throws IOException {
    if (file.isDirectory()) {
      File outputFile = new File(dir, file.getName());
      outputFile.mkdir();
      for (File f : file.listFiles()) {
        copyFile(f, outputFile);
      }
    } else {
      File newFile = new File(dir, file.getName());
      FileChannel outputChannel = null;
      FileChannel inputChannel = null;
      try {
        outputChannel = new FileOutputStream(newFile).getChannel();
        inputChannel = new FileInputStream(file).getChannel();
        inputChannel.transferTo(0, inputChannel.size(), outputChannel);
        inputChannel.close();
      } finally {
        if (inputChannel != null) {
          inputChannel.close();
        }
        if (outputChannel != null) {
          outputChannel.close();
        }
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    llButtons.setVisibility(View.GONE);
    if (resultCode == RESULT_CANCELED) {
      finish();
    }
    if (resultCode == RESULT_OK) {
      if (requestCode == INTENT_COPY) {
        if (data.hasExtra(DIRECTORY_COPY_TO)) {
          List<DirectoryItem> list = directoryItemAdapter.getList();
          for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getSelected()) {
              DirectoryItem di = (DirectoryItem) list.get(i);
              try {
                copyFile(new File(list.get(i).getFilepath()),
                         new File(data.getStringExtra(DIRECTORY_COPY_TO)));
              } catch (Throwable throwable) {

              }
            }
          }
        }
      }
      if (requestCode == INTENT_MOVE) {
        if (data.hasExtra(DIRECTORY_COPY_TO)) {
          List<DirectoryItem> list = directoryItemAdapter.getList();
          for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getSelected()) {
              DirectoryItem di = list.get(i);
              try {
                moveFile(new File(list.get(i).getFilepath()),
                         new File(data.getStringExtra(DIRECTORY_COPY_TO)));
              } catch (Throwable throwable) {

              }
            }
          }
        }
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void shareMultiple(ArrayList<Uri> files, Context context) {
    final Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
    intent.setType("*/*");
    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
    context.startActivity(Intent.createChooser(intent, "Share via"));
  }

  @BindView(R.id.horizontal_recycler_view)
  RecyclerView horizontal_recycler_view;

  @BindView(R.id.listView)
  ListView listView;

  @BindView(R.id.llButtons)
  LinearLayout llButtons;

  @OnClick(R.id.ibRootDirectory)
  public void ibRootDirectoryClick(View v) {
    path = "/";
    refreshList();
  }

  @BindView(R.id.llShare)
  LinearLayout llShare;

  @OnClick(R.id.ibNewFolder)
  public void ibNewFolderClick(View v) {
    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    final EditText etNewFolderName = new EditText(MainActivity.this);  //!!!!!
    builder.setTitle("Enter folder name")
        .setView(etNewFolderName)
        .setPositiveButton("Create", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            makeNewFolder(etNewFolderName.getText().toString());
            refreshList();
          }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
          }
        });
    AlertDialog alert = builder.create();
    alert.show();
  }

  @BindView(R.id.llAddToFavorites)
  LinearLayout llAddToFavorites;

  @OnClick(R.id.ibSort)
  public void ibSortClick(View v) {
    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    builder.setTitle("Sort by").setIcon(R.drawable.sort).setItems(sortVariants, (dialog, which) -> {
      if (which == 0) {
        comparator = new DirectoryItem.CompSize();
      }
      if (which == 1) {
        comparator = new DirectoryItem.CompDate();
      }
      if (which == 2) {
        comparator = new DirectoryItem.CompName();
      }
      Toast.makeText(MainActivity.this, sortVariants[which], Toast.LENGTH_LONG).show();
      Collections.sort(items, comparator);
      directoryItemAdapter.updateList(items);
    });

    builder.create();
    builder.show();
  }

  @OnClick(R.id.ibHome)
  public void ibHomeClick(View v) {
    setResult(RESULT_OK);
    finish();
  }

  @BindView(R.id.llDelete)
  LinearLayout llDelete;

  @BindView(R.id.llCopy)
  LinearLayout llCopy;

  @BindView(R.id.llMove)
  LinearLayout llMove;

  private void setViews() {
    llAddToFavorites.setOnClickListener(v -> {
      List<DirectoryItem> list = directoryItemAdapter.getList();
      ArrayList<String> filesToAdd = new ArrayList<String>();
      for (int i = 0; i < list.size(); i++) {
        if (list.get(i).getSelected()) {
          write(list.get(i).getFilepath());
        }
      }
      directoryItemAdapter.isCheckBoxVisibility = false;
      llButtons.setVisibility(View.GONE);
      refreshList();
    });
    llCopy.setOnClickListener(v -> {
      if (getCountOfSelectedItems() > 0) {
        directoryItemAdapter.isCheckBoxVisibility = false;
        Intent intent = new Intent(MainActivity.this, CopyMoveActivity.class);
        intent.putExtra(PATH, path);
        startActivityForResult(intent, INTENT_COPY);
      }
    });
    llMove.setOnClickListener(v -> {
      if (getCountOfSelectedItems() > 0) {
        directoryItemAdapter.isCheckBoxVisibility = false;
        Intent intent = new Intent(MainActivity.this, CopyMoveActivity.class);
        intent.putExtra(PATH, path);
        startActivityForResult(intent, INTENT_MOVE);
      }
    });
    llDelete.setOnClickListener(v -> {
      AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
      builder.setTitle("Delete " + getCountOfSelectedItems() + " items?")
          .setPositiveButton("YES", (dialog, which) -> {
            List<DirectoryItem> list = directoryItemAdapter.getList();
            for (int i = 0; i < list.size(); i++) {
              if (list.get(i).getSelected()) {
                DirectoryItem di = (DirectoryItem) list.get(i);
                delete(list.get(i).getFilepath());
              }
            }
            refreshList();
          })
          .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
      AlertDialog alert = builder.create();
      alert.show();
      directoryItemAdapter.isCheckBoxVisibility = false;
      llButtons.setVisibility(View.GONE);
    });
    llShare.setOnClickListener((v) -> {
      Toast.makeText(MainActivity.this, "share", Toast.LENGTH_LONG).show();
      List<DirectoryItem> list = directoryItemAdapter.getList();
      ArrayList<Uri> filesToShare = new ArrayList<Uri>();
      for (int i = 0; i < list.size(); i++) {
        if (list.get(i).getSelected()) {
          filesToShare.add(Uri.fromFile(new File(list.get(i).getFilepath())));
        }
      }
      shareMultiple(filesToShare, MainActivity.this);
      directoryItemAdapter.isCheckBoxVisibility = false;
      refreshList();
      llButtons.setVisibility(View.GONE);
    });
    listView.setOnItemClickListener((parent, view, position, id) -> {
      DirectoryItem file = (DirectoryItem) items.get(position);
      String filename = file.getFilepath();
      File intentFile = new File(filename);
      if (intentFile.isDirectory()) {
        path = filename;
        Log.i("TAG", path);

        refreshList();
      }
      if (intentFile.isFile()) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(intentFile), file.getIntentType());
        startActivity(intent);
      }
    });

    listView.setOnItemLongClickListener((parent, view, position, id) -> {
      llButtons.setVisibility(View.VISIBLE);
      directoryItemAdapter.isCheckBoxVisibility = true;
      DirectoryItem di = (DirectoryItem) items.get(position);
      di.setSelected(true);
      items.remove(position);
      items.add(position, di);
      directoryItemAdapter.updateList(items);
      return false;
    });
  }
}



