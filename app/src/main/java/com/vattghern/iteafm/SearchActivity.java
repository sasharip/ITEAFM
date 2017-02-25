package com.vattghern.iteafm;

import static com.vattghern.iteafm.Constants.DIRECTORY_COPY_TO;
import static com.vattghern.iteafm.Constants.INTENT_COPY;
import static com.vattghern.iteafm.Constants.INTENT_MAIN;
import static com.vattghern.iteafm.Constants.INTENT_MOVE;
import static com.vattghern.iteafm.Constants.PATH;
import static com.vattghern.iteafm.FileWriterReader.write;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchActivity extends AppCompatActivity {

  private DirectoryItemAdapter directoryItemAdapter;
  private List items = new ArrayList<DirectoryItem>();
  private ListView listView;
  private LinearLayout llButtons;
  private LinearLayout llDelete;
  private LinearLayout llCopy;
  private LinearLayout bMove;
  private LinearLayout llShare;
  private LinearLayout llAddToFavorites;
  private EditText etSearch;
  private ImageButton ibHome;
  private ImageButton ibSearch;
  private ImageButton ibSort;
  private Comparator comparator = new DirectoryItem.CompName();
  private String[] sortVariants = {"Size", "Date", "Name"};

  private class SearchTask extends AsyncTask<String, Void, ArrayList<DirectoryItem>> {

    private ArrayList<DirectoryItem> result = new ArrayList<DirectoryItem>();

    private void fileSearch(String directory, String query) {
      File dir = new File(directory);
      String[] list = dir.list();
      if (list != null) {
        for (String file : list) {
          DirectoryItem di = new DirectoryItem(directory, file, false);
          if (!file.startsWith(".") && file.toLowerCase().contains(query.toLowerCase())) {
            Log.e("search", "found " + file);
            result.add(di);
          }
          if (new File(di.getFilepath()).isDirectory()) {
            fileSearch(di.getFilepath(), query);
          }
        }
      }
    }

    @Override
    protected ArrayList<DirectoryItem> doInBackground(String... params) {
      fileSearch("/sdcard", params[0]);
      return result;
    }
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    setViews();
    directoryItemAdapter = new DirectoryItemAdapter(this, R.layout.layout_list_item);
    listView.setAdapter(directoryItemAdapter);
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  public void onBackPressed() {
    if (directoryItemAdapter.isCheckBoxVisibility) {
      llButtons.setVisibility(View.GONE);
      directoryItemAdapter.isCheckBoxVisibility = false;
    } else {
      finish();
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
    if (requestCode == INTENT_MAIN && resultCode == RESULT_OK) {
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

  public static void shareMultiple(ArrayList<Uri> files, Context context) {
    final Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
    intent.setType("*/*");
    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
    context.startActivity(Intent.createChooser(intent, "Share via"));
  }


  //a method sets views
  private void setViews() {
    ibSort = (ImageButton) findViewById(R.id.ibSort);
    ibSort.setOnClickListener((v) -> {
      AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
      builder.setTitle("Sort by")
          .setIcon(R.drawable.sort)
          .setItems(sortVariants, (dialog, which) -> {
            if (which == 0) {
              comparator = new DirectoryItem.CompSize();
            }
            if (which == 1) {
              comparator = new DirectoryItem.CompDate();
            }
            if (which == 2) {
              comparator = new DirectoryItem.CompName();
            }
            Toast.makeText(getApplicationContext(), sortVariants[which], Toast.LENGTH_LONG)
                .show();
            Collections.sort(items, comparator);
            directoryItemAdapter.updateList(items);
          });
      builder.create();
      builder.show();
    });
    ibHome = (ImageButton) findViewById(R.id.ibHome);
    ibHome.setOnClickListener((v) -> finish());
    etSearch = (EditText) findViewById(R.id.etSearch);
    ibSearch = (ImageButton) findViewById(R.id.ibSearch);
    ibSearch.setOnClickListener((v) -> {
      SearchTask searchTask = new SearchTask();
      if (etSearch.getText().toString() != "") {
        searchTask.execute(etSearch.getText().toString());
      }
      try {
        items = searchTask.get();
        Collections.sort(items, comparator);
        directoryItemAdapter.updateList(items);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
    });
    listView = (ListView) findViewById(R.id.listView);
    llButtons = (LinearLayout) findViewById(R.id.llButtons);
    llAddToFavorites = (LinearLayout) findViewById(R.id.llAddToFavorites);
    llAddToFavorites.setOnClickListener(v -> {
      List<DirectoryItem> list = directoryItemAdapter.getList();
      for (int i = 0; i < list.size(); i++) {
        if (list.get(i).getSelected()) {
          write(list.get(i).getFilepath());
        }
      }
      directoryItemAdapter.isCheckBoxVisibility = false;
      llButtons.setVisibility(View.GONE);
      directoryItemAdapter.updateList(items);
    });
    llShare = (LinearLayout) findViewById(R.id.llShare);
    llShare.setOnClickListener((v) -> {
      List<DirectoryItem> list = directoryItemAdapter.getList();
      ArrayList<Uri> filesToShare = new ArrayList<Uri>();
      for (int i = 0; i < list.size(); i++) {
        if (list.get(i).getSelected()) {
          filesToShare.add(Uri.fromFile(new File(list.get(i).getFilepath())));
        }
      }
      shareMultiple(filesToShare, SearchActivity.this);
      directoryItemAdapter.isCheckBoxVisibility = false;
      llButtons.setVisibility(View.GONE);
    });
    llDelete = (LinearLayout) findViewById(R.id.llDelete);
    llDelete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
        builder.setTitle("Delete " + getCountOfSelectedItems() + " items?")
            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    List<DirectoryItem> list = directoryItemAdapter.getList();
                    for (int i = 0; i < list.size(); i++) {
                      if (list.get(i).getSelected()) {
                        DirectoryItem di = (DirectoryItem) list.get(i);
                        delete(list.get(i).getFilepath());
                        items.remove(i);
                      }
                    }
                    directoryItemAdapter.updateList(items);
                  }
                }
            )
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
              }
            });
        AlertDialog alert = builder.create();
        alert.show();
        directoryItemAdapter.isCheckBoxVisibility = false;
        llButtons.setVisibility(View.GONE);
      }
    });
    llCopy = (LinearLayout) findViewById(R.id.llCopy);
    llCopy.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (getCountOfSelectedItems() > 0) {
          directoryItemAdapter.isCheckBoxVisibility = false;
          Intent intent = new Intent(SearchActivity.this, CopyMoveActivity.class);
          intent.putExtra(PATH, "/");
          startActivityForResult(intent, INTENT_COPY);
        }
      }
    });
    bMove = (LinearLayout) findViewById(R.id.llMove);
    bMove.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (getCountOfSelectedItems() > 0) {
          directoryItemAdapter.isCheckBoxVisibility = false;
          Intent intent = new Intent(SearchActivity.this, CopyMoveActivity.class);
          intent.putExtra(PATH, "/");
          startActivityForResult(intent, INTENT_MOVE);
        }
      }
    });
    listView.setOnItemClickListener((parent, view, position, id) -> {
      DirectoryItem file = (DirectoryItem) items.get(position);
      String filename = file.getFilepath();
      File intentFile = new File(filename);
      if (intentFile.isDirectory()) {
        Intent directoryIntent = new Intent(SearchActivity.this, MainActivity.class);
        directoryIntent.putExtra(PATH, filename);
        startActivityForResult(directoryIntent, INTENT_MAIN);
      }
      if (intentFile.isFile()) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(intentFile), file.getIntentType());
        startActivity(intent);
      }
    }
    );

    listView.setOnItemLongClickListener((parent, view, position, id) -> {
      llButtons.setVisibility(View.VISIBLE);
      directoryItemAdapter.isCheckBoxVisibility = true;
      DirectoryItem di = (DirectoryItem) items.get(position);
      di.setSelected(true);
      items.remove(position);
      items.add(position, di);
      directoryItemAdapter.updateList(items);
      return false;
    }
    );
  }
}
