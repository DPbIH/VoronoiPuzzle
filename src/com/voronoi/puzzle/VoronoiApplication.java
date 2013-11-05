package com.voronoi.puzzle;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;

public class VoronoiApplication extends Application {
	private String appPath_, 
	imgGalleryPath_,
	savedGamesPath_;
	
	@Override
    public void onCreate()
	{
        super.onCreate();
        DeployAppFileSystem();
	}
	
	public String ImageGalleryPath()
	{
		return imgGalleryPath_;
	}
	
	public String SavedGamesPath()
	{
		return savedGamesPath_;
	}
	
	public String AppFilesPath()
	{
		return appPath_;
	}
	
	private void InitPaths()
	{
		//appPath_ = getApplicationContext().getFilesDir().getAbsolutePath();
		if( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) )
		{
			appPath_ = Environment.getExternalStorageDirectory() + File.separator + "VoronoiFolder";
		}
		imgGalleryPath_ = appPath_ + File.separator + "Gallery";
		savedGamesPath_ = appPath_ + File.separator + "SavedGames";
	}
	private void DeployAppFileSystem()
	{
		InitPaths();
		
		CreateDir( imgGalleryPath_ );
		CreateDir( savedGamesPath_ );
		
		try 
		{
			Bitmap bmp1 = BitmapFactory.decodeResource(getResources(),R.drawable.texture);
			Bitmap bmp2 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
		       FileOutputStream out = new FileOutputStream(imgGalleryPath_ + File.separator + "texture.jpg");
		       bmp1.compress(Bitmap.CompressFormat.JPEG, 90, out);
		       out.close();
		       out = new FileOutputStream(imgGalleryPath_ + File.separator + "ic.jpg");
		       bmp2.compress(Bitmap.CompressFormat.JPEG, 90, out);
		       out.close();
		} catch (Exception e) {
		       e.printStackTrace();
		}
	}
	
	private void CreateDir( String path )
	{
		File newDir = new File(path);
		if( ! newDir.exists() )
		{
			newDir.mkdirs();
		}
	}
}
