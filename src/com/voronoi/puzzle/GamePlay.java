package com.voronoi.puzzle;

import java.util.ArrayList;

import com.voronoi.puzzle.diagramimpl.Diagram;
import com.voronoi.puzzle.puzzleimpl.Tile;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.Menu;

public class GamePlay extends Activity {

	private GameView 		gameView_	= null;
	private Diagram 		diagram_	= null;
	private Bitmap 			image_		= null;
	private ArrayList<Tile>	tiles_		= new ArrayList<Tile>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
			initGameView();
		}
		catch( Exception ex )
		{
			return;
		}
	}

	private void initGameView()
	{
		gameView_ 	= (GameView)findViewById(R.id.gameView);
		gameView_.setDiagram( diagram_ );
		gameView_.setImage(image_);
		
		for( Tile tile: tiles_ )
		{
			gameView_.addTile(tile);
		}
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
	public boolean onCreateOptionsMenu(Menu menu) {
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

}
