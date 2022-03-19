package com.photogallery.imagegallery.fragments_nav;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.photogallery.imagegallery.ImageDisplay;
import com.photogallery.imagegallery.R;
import com.photogallery.imagegallery.utils.MarginDecoration;
import com.photogallery.imagegallery.utils.PicHolder;
import com.photogallery.imagegallery.utils.imageFolder;
import com.photogallery.imagegallery.utils.itemClickListener;
import com.photogallery.imagegallery.utils.pictureFacer;
import com.photogallery.imagegallery.utils.pictureFolderAdapter;

import java.util.ArrayList;


public class GalleryFragment extends Fragment implements itemClickListener {

    RecyclerView folderRecycler;
    TextView empty;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    ImageButton imageButton;
    ArrayList<imageFolder> folds;
    boolean flag;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_gallery,container,false);
        flag = true;
       folds = new ArrayList<imageFolder>();
        if(ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        empty =rootview.findViewById(R.id.empty);
        imageButton = rootview.findViewById(R.id.menu);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(rootview.getContext(), imageButton);
                popup.getMenu().add("Order By Ascending");
                popup.getMenu().add("Order By Descending");
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("Order By Ascending")){
                            flag = false;
                            folds = getPicturePaths(true);
                            RecyclerView.Adapter folderAdapter = new pictureFolderAdapter(folds,getActivity(),GalleryFragment.this);
                            folderRecycler.setAdapter(folderAdapter);

                        }else if(item.getTitle().equals("Order By Descending")){
                            folds = getPicturePaths(false);
                            flag=  false;
                            RecyclerView.Adapter folderAdapter = new pictureFolderAdapter(folds,getActivity(),GalleryFragment.this);
                            folderRecycler.setAdapter(folderAdapter);

                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
        folderRecycler = rootview.findViewById(R.id.folderRecycler);
        folderRecycler.addItemDecoration(new MarginDecoration(getActivity()));
        folderRecycler.hasFixedSize();
        if(flag){
            folds = getPicturePaths(true);
        }

        if(folds.isEmpty()){
            empty.setVisibility(View.VISIBLE);
        }else{
            RecyclerView.Adapter folderAdapter = new pictureFolderAdapter(folds,getActivity(),GalleryFragment.this);
            folderRecycler.setAdapter(folderAdapter);
        }

        changeStatusBarColor();

        return rootview;
    }

    /**1
     * @return
     * gets all folders with pictures on the device and loads each of them in a custom object imageFolder
     * the returns an ArrayList of these custom objects
     */
    private ArrayList<imageFolder> getPicturePaths(boolean flag){
        ArrayList<imageFolder> picFolders = new ArrayList<>();
        ArrayList<String> picPaths = new ArrayList<>();
        Uri allImagesuri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.ImageColumns.DATA ,MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.Media.BUCKET_ID};
        Cursor cursor = getActivity().getContentResolver().query(allImagesuri, projection, null, null, null);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
            }
            do{
                imageFolder folds = new imageFolder();
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                //String folderpaths =  datapath.replace(name,"");
                String folderpaths = datapath.substring(0, datapath.lastIndexOf(folder+"/"));
                folderpaths = folderpaths+folder+"/";
                if (!picPaths.contains(folderpaths)) {
                    picPaths.add(folderpaths);

                    folds.setPath(folderpaths);
                    folds.setFolderName(folder);
                    folds.setFirstPic(datapath);//if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                    folds.addpics();
                    picFolders.add(folds);
                }else{
                    for(int i = 0;i<picFolders.size();i++){
                        if(picFolders.get(i).getPath().equals(folderpaths)){
                            picFolders.get(i).setFirstPic(datapath);
                            picFolders.get(i).addpics();
                        }
                    }
                }
            }while(cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i = 0;i < picFolders.size();i++){
            Log.d("picture folders",picFolders.get(i).getFolderName()+" and path = "+picFolders.get(i).getPath()+" "+picFolders.get(i).getNumberOfPics());
        }


        if(flag){
            for(int i=0;i < picFolders.size();i++){
                for(int j=0;j<(picFolders.size()-i-1);j++){
                    if(picFolders.get(j).getFolderName().compareToIgnoreCase(picFolders.get(j+1).getFolderName())>0){
                       imageFolder folder = picFolders.get(j);
                       picFolders.set(j,picFolders.get(j+1));
                       picFolders.set(j+1,folder);
                    }
                }
            }
        }else{
            for(int i=0;i < picFolders.size();i++){
                for(int j=0;j<(picFolders.size()-i-1);j++){
                    if(picFolders.get(j).getFolderName().compareToIgnoreCase(picFolders.get(j+1).getFolderName())<0){
                        imageFolder folder = picFolders.get(j);
                        picFolders.set(j,picFolders.get(j+1));
                        picFolders.set(j+1,folder);
                    }
                }
            }
        }


        return picFolders;
    }


    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {

    }

    /**
     * Each time an item in the RecyclerView is clicked this method from the implementation of the transitListerner
     * in this activity is executed, this is possible because this class is passed as a parameter in the creation
     * of the RecyclerView's Adapter, see the adapter class to understand better what is happening here
     * @param pictureFolderPath a String corresponding to a folder path on the device external storage
     */
    @Override
    public void onPicClicked(String pictureFolderPath,String folderName) {
        Intent move = new Intent(getActivity(), ImageDisplay.class);
        move.putExtra("folderPath",pictureFolderPath);
        move.putExtra("folderName",folderName);

        //move.putExtra("recyclerItemSize",getCardsOptimalWidth(4));
        startActivity(move);
    }


    /**
     * Default status bar height 24dp,with code API level 24
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeStatusBarColor()
    {
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.black));

    }

}
