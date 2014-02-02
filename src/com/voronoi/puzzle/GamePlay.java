package com.voronoi.puzzle;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class GamePlay extends Activity {

	private GameView 		gameView_		= null;
	private Diagram 		diagram_		= null;
	private Bitmap 			image_			= null;
	private TilesGallery    tilesGallery_   = null;

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
			
			initTilesGallery();
			initGameView();
		}
		catch( Exception ex )
		{
			return;
		}
	}
	
	public interface TileSelectedListener
	{
		void OnTileSelected( Tile tile );
	}
	
	private class TilesGallery
	{
		public TilesGallery()
		{
			Init();
		}
		
		private void Init()
		{
			layout_ = (LinearLayout) findViewById(R.id.tilesGallery);
		}
		
		public Collection<Tile> GetTiles()
		{
			return icon2tileMapping_.values();
		}
		
		public void AddTile( Tile tile )
		{
			ImageView tileIcon = CreateTileIcon(tile);
			
			tileIcon.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View view)
				{
					ImageView img = (ImageView)view;
					Tile tile = icon2tileMapping_.get(img);
					
					if( tile != null)
					{
						OnTileSelected( tile );
					}
				}
			});

			layout_.addView(tileIcon);
			
			icon2tileMapping_.put(tileIcon, tile);
		}

		private ImageView CreateTileIcon( Tile tile )
		{
			ImageView tileIcon = new ImageView( getApplicationContext() );
			tileIcon.setLayoutParams( GetLayoutParams() );
			tileIcon.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
			tileIcon.setImageBitmap( tile.getBitmap() );
			
			return tileIcon;
		}
		
		private LinearLayout.LayoutParams GetLayoutParams()
		{
			if( lp_ == null )
			{
				final int margin = 10;
				final int height = 100;
				
				lp_ = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				
				lp_.setMargins( margin, margin, margin, margin );
				lp_.height = height;
				lp_.width  = height;
			}
			
			return lp_;
		}
		
		private void OnTileSelected( Tile selectedTile )
		{
			for( TileSelectedListener listener: listeners_ )
			{
				listener.OnTileSelected( selectedTile );
			}
		}
		
		public void AddTileSelectedListener( TileSelectedListener listener )
		{
			listeners_.add( listener );
		}
		
		private LinearLayout              		layout_;
		private LinearLayout.LayoutParams 		lp_;
		private Map<ImageView, Tile>	  		icon2tileMapping_	= new HashMap<ImageView, Tile>();
		private ArrayList<TileSelectedListener> listeners_ 			= new ArrayList<TileSelectedListener>();
	}
	
	private void initTilesGallery()
	{
		tilesGallery_ = new TilesGallery();
		
		if( (image_ == null) || (diagram_ == null) )
		{
			return;
		}
		
		TilesCreator tc = new TilesCreator();
		
		for( Tile tile: tc.getTiles(diagram_, image_) )
		{
			tilesGallery_.AddTile(tile);
		}
		
		tilesGallery_.AddTileSelectedListener( new TileSelectedListener() 
		{
			@Override
			public void OnTileSelected(Tile tile)
			{
				gameView_.addTile( tile );
			}
		});
	}
	
	private void initGameView()
	{
		gameView_ = (GameView)findViewById(R.id.gameView);
		gameView_.setDiagram( diagram_ );
		gameView_.setImage(image_);
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
