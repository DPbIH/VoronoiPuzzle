package com.voronoi.puzzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.voronoi.puzzle.diagramimpl.Diagram;
import com.voronoi.puzzle.puzzleimpl.Tile;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class GamePlay extends Activity {

	private GameView 		gameView_		= null;
	private Diagram 		diagram_		= null;
	private Bitmap 			image_			= null;
	private LinearLayout	tilesGallery_	= null;
	private ArrayList<Tile>			tiles_				= new ArrayList<Tile>();
	private Map<ImageView, Tile>	tileViewMapping_	= new HashMap<ImageView, Tile>();

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gameplay);
		
		init();
	}
	
	private void init()
	{
		try
		{
			diagram_	= new Diagram();
			diagram_.fromStringArray( getIntent().getStringArrayExtra( GlobalConstants.DIAGRAM_STRING_EXTRA ) );
			
			byte[] bmpByteArr = getIntent().getByteArrayExtra(GlobalConstants.PUZZLE_IMAGE_EXTRA);
			image_ = BitmapFactory.decodeByteArray( bmpByteArr, 0, bmpByteArr.length );   
			
			createTiles();
			initTilesGallery();
			initGameView();
		}
		catch( Exception ex )
		{
			return;
		}
	}
	
	private void initTilesGallery()
	{
		tilesGallery_ = (LinearLayout) findViewById(R.id.tilesGallery);

		for( Tile tile: tiles_ )
		{
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(10, 10, 10, 10);
			lp.height = 100; lp.width = 100;
			
			ImageView tileIcon = new ImageView( getApplicationContext() );
			tileIcon.setLayoutParams( lp );
			tileIcon.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
			tileIcon.setImageBitmap( tile.getBitmap() );
			tileIcon.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View view)
				{
					ImageView img = (ImageView)view;
					Tile tile = tileViewMapping_.get(img);
					
					if( tile != null)
					{
						gameView_.addTile(tile);
						//img.setEnabled(false);
					}
				}
			});

			tilesGallery_.addView(tileIcon);
			tileViewMapping_.put(tileIcon, tile);
		}
	}
	
	private void initGameView()
	{
		gameView_ = (GameView)findViewById(R.id.gameView);
		gameView_.setDiagram( diagram_ );
		gameView_.setImage(image_);
	}
	
	public void createTiles()
	{
		tiles_.clear();
		
		if( (image_ == null) || (diagram_ == null) )
		{
			return;
		}
		
		TilesCreator tc = new TilesCreator();
		tiles_ = tc.getTiles(diagram_, image_);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  
	{
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
	    {
	    	startActivity(new Intent(this, PuzzleEditor.class) );     
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}

	public void enableHintImage(View view) 
	{
		boolean on = ((ToggleButton)view).isChecked();
		gameView_.showImage( on ? GameView.ImageMonochrome : GameView.ImageNone );
	}
	
	public void enableHintGrid(View view)
	{
		boolean on = ((ToggleButton)view).isChecked();
		gameView_.showDiagram( on );
	}
	
	public void enableHintIndex(View view) 
	{
		boolean on = ((ToggleButton)view).isChecked();
		gameView_.showTilesNumbers( on );
	}
}
