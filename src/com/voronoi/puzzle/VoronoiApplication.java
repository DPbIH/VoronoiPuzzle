package com.voronoi.puzzle;

import java.io.File;

import android.app.Application;
import android.os.Environment;

public class VoronoiApplication extends Application 
{
	private String appPath_, 
	imgGalleryPath_,
	savedGamesPath_;
	
	@Override
    public void onCreate()
	{
        super.onCreate();
        deployAppFileSystem();
	}
	
	public String imageGalleryPath()
	{
		return imgGalleryPath_;
	}
	
	public String savedGamesPath()
	{
		return savedGamesPath_;
	}
	
	public String appFilesPath()
	{
		return appPath_;
	}
	
	private void initPaths()
	{
		//appPath_ = getApplicationContext().getFilesDir().getAbsolutePath();
		if( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) )
		{
			appPath_ = Environment.getExternalStorageDirectory() + File.separator + "VoronoiFolder";
		}
		imgGalleryPath_ = appPath_ + File.separator + "Gallery";
		savedGamesPath_ = appPath_ + File.separator + "SavedGames";
	}
	
	private void deployAppFileSystem()
	{
		initPaths();
		
		createDir( imgGalleryPath_ );
		createDir( savedGamesPath_ );
	}
	
	private void createDir( String path )
	{
		File newDir = new File(path);
		if( ! newDir.exists() )
		{
			newDir.mkdirs();
		}
	}
}
