package com.voronoi.puzzle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PuzzleEditor extends Activity implements MediaScannerConnectionClient 
{
	private static final int PICK_IMAGE_FROM_CHOOSER 	= 1;
	private static final int PICK_IMAGE_USE_CAMERA 		= 2;
	private static final String mimeTypeImage 			= "image/*";
	
	private String SCAN_PATH;
	private MediaScannerConnection conn;
	private Uri imgUri_, uriWorkaround_;
	private File fileSelectedInPreviewer_;
	
	private static EditorView editor_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_puzzle_editor);
		editor_ = (EditorView)findViewById(R.id.editorView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.puzzle_editor, menu);
		return true;
	}

	public void setImageUri( Uri uri )
	{
		imgUri_ = uri;
		try {
			editor_.SetBackgroundImage( uri );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void loadImage(View view) 
	{
		ImageSelectorDialog imageSelector = new ImageSelectorDialog();
		imageSelector.show( getFragmentManager(), "dialog" );
	}

	public void startGamePlay(View view) 
	{
		Intent intent = new Intent(this, GamePlay.class);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
		{
			startActivity(new Intent(this, StartPage.class) );     
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public static class ImageSelectorDialog extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) 
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.PickImageSource);
			builder.setItems(R.array.ImageSourcesArray, 
					new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					PuzzleEditor editor = (PuzzleEditor)getActivity();
					switch(which)
					{
					case 0:
						try 
						{
							editor.captureImageUsingCamera();
						}
						catch (IOException e) 
						{
							e.printStackTrace();
						}
						
						break;
					case 1:
						editor.pickImageFromAppGallery();
						break;
					case 2:
						editor.pickImageFromDeviceGallery();
						break;
					case 3:
						editor.exploreFileSystemAndPickImage( 
								Environment.getExternalStorageDirectory().getAbsolutePath() );
						break;
					};
				}
			});

			return builder.create();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		switch(requestCode)
		{
		case PICK_IMAGE_USE_CAMERA:
			if( (resultCode == RESULT_OK) && (data == null) )
			{
				setImageUri(uriWorkaround_);
				break;
			}
		case PICK_IMAGE_FROM_CHOOSER:
			if( (resultCode == RESULT_OK) )
	        {
	        	setImageUri( data.getData() );
	        }
			break;
		}
    }
	
	private File createFileForImageInAppGallery() throws IOException
	{
		File appGallery = new File( ((VoronoiApplication)this.getApplication()).ImageGalleryPath() );
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "Gallery_" + timeStamp;
	    File image = File.createTempFile( imageFileName, ".jpg", appGallery );
	    
	    return image;
	}
	
	public void captureImageUsingCamera() throws IOException
	{
		File img = createFileForImageInAppGallery();
		
		// MediaStore.EXTRA_OUTPUT bug workaround
		uriWorkaround_ = Uri.fromFile(img);
		
		Intent takePictureIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(img));
	    startActivityForResult( takePictureIntent, PICK_IMAGE_USE_CAMERA );
	}
	
	public void exploreFileSystemAndPickImage( String path )
	{
		File dir = new File( path );
        
        FileDialog fileDialog = new FileDialog(this, dir);
        fileDialog.setMIMEFilter( mimeTypeImage );
        
        fileDialog.addFileListener(new FileDialog.FileSelectedListener()
        {
            public void fileSelected(File file)
            {
               previewSelectedImage( file.getAbsolutePath() );
            }
        });
        
        fileDialog.showDialog();
	}
	
	public File getFileSelectedInPreviewer()
	{
		return fileSelectedInPreviewer_;
	}
	
	// To do: refactor this method
	private void previewSelectedImage( String imgPath )
	{
		fileSelectedInPreviewer_ = new File(imgPath);
	
		// create image for Preview Dialog
		Bitmap bitmap = ((BitmapDrawable) Drawable.createFromPath(imgPath)).getBitmap();
		Bitmap bitmapForPreview = scaleImg(bitmap, 150, true);
		Drawable dr = new BitmapDrawable(getResources(), bitmapForPreview );
		
		// create view for Preview Dialog
		ImageView image=new ImageView(this);
		image.setImageDrawable(dr);
		
		// Create Preview Dialog
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
	    builder.setCancelable(true);
	    builder.setView(image); 
	    builder.setTitle("Image Preview");
	    builder.setInverseBackgroundForced(true);
	    builder.setPositiveButton("Choose", new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int which) 
	        {
	            dialog.dismiss();
	            setImageUri( Uri.fromFile( getFileSelectedInPreviewer() ) );
	        }
	    });
	    
	    builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int which)
	        {
	            dialog.dismiss();
	            exploreFileSystemAndPickImage( getFileSelectedInPreviewer().getParent() );
	        }
	    });
	    
	    AlertDialog alert=builder.create();
	    alert.show();
	}
	
	private static Bitmap scaleImg(Bitmap realImage, float maxImageSize, boolean filter) 
	{
	    float ratio = Math.min(
	            (float) maxImageSize / realImage.getWidth(),
	            (float) maxImageSize / realImage.getHeight());
	    int width = Math.round((float) ratio * realImage.getWidth());
	    int height = Math.round((float) ratio * realImage.getHeight());

	    Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
	            height, filter);
	    return newBitmap;
	}
	
	public void pickImageFromAppGallery()
	{
		String path = ((VoronoiApplication)this.getApplication()).ImageGalleryPath();
		scanDirectory( path );
	}
	
	public void pickImageFromDeviceGallery()
	{
		Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		createImageChooser( uri );
	}

	private void scanDirectory( String dir )
	{
		File folder = new File(dir);
		File allFiles[] = folder.listFiles();
        SCAN_PATH = allFiles[0].getAbsolutePath();
		
		if(conn != null)
		{
			conn.disconnect();
		}

		conn = new MediaScannerConnection(this, this);
		conn.connect();
	}


	public void onMediaScannerConnected()
	{
        conn.scanFile(SCAN_PATH, null);
	}


	public void onScanCompleted(String path, Uri uri)
	{
		try
		{
			if (uri != null) 
			{
				createImageChooser(uri);
			}
		}
		finally 
		{
			conn.disconnect();
			conn = null;
		}
	}
	
	private void createImageChooser(Uri uri)
	{
		if (uri != null) 
		{
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setData(uri);
			startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_FROM_CHOOSER );
		}
	}

}
