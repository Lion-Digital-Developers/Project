package com.photogallery.imagegallery;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.photogallery.imagegallery.fragments.pictureBrowserFragment;
import com.photogallery.imagegallery.fragments_nav.GalleryFragment;
import com.photogallery.imagegallery.utils.MarginDecoration;
import com.photogallery.imagegallery.utils.PicHolder;
import com.photogallery.imagegallery.utils.imageFolder;
import com.photogallery.imagegallery.utils.itemClickListener;
import com.photogallery.imagegallery.utils.pictureFacer;
import com.photogallery.imagegallery.utils.pictureFolderAdapter;
import com.photogallery.imagegallery.utils.picture_Adapter;
import java.util.ArrayList;



public class ImageDisplay extends AppCompatActivity implements itemClickListener {

    RecyclerView imageRecycler;
    ArrayList<pictureFacer> allpictures;
    ProgressBar load;
    String foldePath;
    TextView folderName;
    ImageButton imageButton;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        folderName = findViewById(R.id.foldername);
        folderName.setText(getIntent().getStringExtra("folderName"));
        flag = true;
        foldePath =  getIntent().getStringExtra("folderPath");
        allpictures = new ArrayList<>();
        imageRecycler = findViewById(R.id.recycler);
        imageRecycler.addItemDecoration(new MarginDecoration(this));
        imageRecycler.hasFixedSize();
        load = findViewById(R.id.loader);
//        Toast.makeText(ImageDisplay.this,"FOlde rname "+foldePath,Toast.LENGTH_SHORT).show();

        imageButton = findViewById(R.id.menu);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(ImageDisplay.this, imageButton);
                popup.getMenu().add("Order By Ascending");
                popup.getMenu().add("Order By Descending");
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("Order By Ascending")){
                            flag= false;
                                load.setVisibility(View.VISIBLE);
                                allpictures = getAllImagesByFolder(foldePath,true);
                                imageRecycler.setAdapter(new picture_Adapter(allpictures,ImageDisplay.this,ImageDisplay.this));
                                load.setVisibility(View.GONE);
                        }else if(item.getTitle().equals("Order By Descending")){
                            flag= false;
                                load.setVisibility(View.VISIBLE);
                                allpictures = getAllImagesByFolder(foldePath,false);
                                imageRecycler.setAdapter(new picture_Adapter(allpictures,ImageDisplay.this,ImageDisplay.this));
                                load.setVisibility(View.GONE);
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
       if(flag){
           if(allpictures.isEmpty()){
               load.setVisibility(View.VISIBLE);
               allpictures = getAllImagesByFolder(foldePath,true);
               imageRecycler.setAdapter(new picture_Adapter(allpictures,ImageDisplay.this,ImageDisplay.this));
               load.setVisibility(View.GONE);
           }else{

           }
       }
    }

    /**
     *
     * @param holder The ViewHolder for the clicked picture
     * @param position The position in the grid of the picture that was clicked
     * @param pics An ArrayList of all the items in the Adapter
     */
    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {
        pictureBrowserFragment browser = pictureBrowserFragment.newInstance(pics,position,ImageDisplay.this);

        // Note that we need the API version check here because the actual transition classes (e.g. Fade)
        // are not in the support library and are only available in API 21+. The methods we are calling on the Fragment
        // ARE available in the support library (though they don't do anything on API < 21)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //browser.setEnterTransition(new Slide());
            //browser.setExitTransition(new Slide()); uncomment this to use slide transition and comment the two lines below
            browser.setEnterTransition(new Fade());
            browser.setExitTransition(new Fade());
        }

        getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(holder.picture, position+"picture")
                .add(R.id.displayContainer, browser)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onPicClicked(String pictureFolderPath,String folderName) {

    }

    /**
     * This Method gets all the images in the folder paths passed as a String to the method and returns
     * and ArrayList of pictureFacer a custom object that holds data of a given image
     * @param path a String corresponding to a folder path on the device external storage
     */
    public ArrayList<pictureFacer> getAllImagesByFolder(String path,boolean flag){
        ArrayList<pictureFacer> images = new ArrayList<>();
        Uri allVideosuri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.ImageColumns.DATA ,MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE};
        Cursor cursor = ImageDisplay.this.getContentResolver().query( allVideosuri, projection, MediaStore.Images.Media.DATA + " like ? ", new String[] {"%"+path+"%"}, null);
        //Toast.makeText(ImageDisplay.this,"Paht : "+path+" Flag : "+flag,Toast.LENGTH_SHORT).show();
        try {
            cursor.moveToFirst();
            do{
                pictureFacer pic = new pictureFacer();

                pic.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));

                pic.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
//                pic.setDateAdded(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)));

                pic.setPictureSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));

                images.add(pic);
            }while(cursor.moveToNext());
            cursor.close();
            ArrayList<pictureFacer> reSelection = new ArrayList<>();
            for(int i = images.size()-1;i > -1;i--){
                reSelection.add(images.get(i));
            }
            images = reSelection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(flag){
            for(int i=0;i < images.size();i++){
                for(int j=0;j<(images.size()-i-1);j++){
                    if(images.get(j).getPicturName().compareToIgnoreCase(images.get(j+1).getPicturName())>0){
                        pictureFacer image = images.get(j);
                        images.set(j,images.get(j+1));
                        images.set(j+1,image);
                    }
                }
            }
        }else{
            for(int i=0;i < images.size();i++){
                for(int j=0;j<(images.size()-i-1);j++){
                    if(images.get(j).getPicturName().compareToIgnoreCase(images.get(j+1).getPicturName())<0){
                        pictureFacer image = images.get(j);
                        images.set(j,images.get(j+1));
                        images.set(j+1,image);
                    }
                }
            }
        }
       // Toast.makeText(this, "Length of Image  "+images.size(), Toast.LENGTH_SHORT).show();
        return images;
    }


}
