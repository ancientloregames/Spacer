package com.ancientlore.spacer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;


class GameView extends SurfaceView implements Runnable
{
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;
	volatile boolean playing;
	Thread gameThread = null;
	private Context context;
	private int screenX, screenY;
	private int upperPanelY;
	private Bitmap background;

	private Paint paint;
	private Paint paintInfo;
	private Paint paintTip;
	private Paint paintState;
	private Canvas canvas;
	private SurfaceHolder ourHolder;

	private GOPlayer player;
	private GOEnemy[] enemy;
	private int enemyCount;
	public ArrayList<GODust> dustList =
			new ArrayList<>();

	private GameState state;
	//private boolean gameEnded;
	private float distanceRemaining;
	private long timeTaken;
	private long timeStarted;
	private long fastestTime;

	ManagerSound _sm;

	public GameView(Context context)
	{
		super(context);
	}

	public GameView(Context context, int displayX, int displayY)
	{
		super(context);
		gameThread = new Thread(this);
		this.context = context;

		prefs = context.getSharedPreferences("HiScores", context.MODE_PRIVATE);
		editor = prefs.edit();
		fastestTime = prefs.getLong("HiScore", 1000000);
		editor.apply();

		ourHolder = getHolder();
		paint = new Paint();
		paintInfo = new Paint();
		paintInfo.setTextSize(displayY / 20);
		paintInfo.setColor(Color.WHITE);
		paintInfo.setTextAlign(Paint.Align.LEFT);
		paintTip = new Paint();
		paintTip.setTextSize(displayX / 26);
		paintTip.setColor(Color.WHITE);
		paintTip.setTextAlign(Paint.Align.CENTER);
		paintState = new Paint();
		paintState.setTextSize(displayX / 9);
		paintState.setColor(Color.WHITE);
		paintState.setTextAlign(Paint.Align.CENTER);
		background = BitmapFactory.decodeResource
				(context.getResources(), R.drawable.spacer_background);

		screenX = displayX;
		screenY = displayY;

		player = new GOPlayer(context, screenX, screenY);

		if (screenX > 1200)
		{
			enemyCount = 5;
		}
		else if (screenX > 1000)
		{
			enemyCount = 4;
		}
		else
		{
			enemyCount = 3;
		}

		enemy = new GOEnemy[enemyCount];
		for (int i = 0; i < enemyCount; i++)
		{
			enemy[i] = new GOEnemy(context, screenX, screenY);
		}

		int numSpecs = 40;
		for (int i = 0; i < numSpecs; i++)
		{
			GODust spec = new GODust(screenX, screenY);
			dustList.add(spec);
		}

		distanceRemaining = 10000; //10km
		timeStarted = System.currentTimeMillis();

		_sm = ManagerSound.getInstance();
		_sm.initialise(context);

		state = GameState.PAUSE;
	}

	@Override
	public void run()
	{
		while (playing)
		{
			if (state == GameState.PLAYING)
			{
				update();
			}
			draw();
			control();
		}
	}

	private void update()
	{
		boolean hitDetected = false;
		for (int i = 0; i < enemyCount; i++)
		{
			if (Rect.intersects(player.getHitBox(), enemy[i].getHitBox()))
			{
				hitDetected = true;
				_sm.playSound("collision");
				enemy[i].setX(-enemy[i].getHitBox().width());
				break;
			}
		}
		if (hitDetected)
		{
			player.reduceShieldStrength();
			hitDetected = false;
			if (player.getShieldStrength() < 0)
			{
				state = GameState.LOSE;
			}
		}

		player.update();
		for (int i = 0; i < enemyCount; i++)
		{
			enemy[i].update(player.getSpeed());
		}

		for (GODust sd : dustList)
		{
			sd.update(player.getSpeed());
		}

		if (state == GameState.PLAYING)
		{
			distanceRemaining -= player.getSpeed();
			timeTaken = System.currentTimeMillis() - timeStarted;
		}

		if (distanceRemaining < 0)
		{
			if (timeTaken < fastestTime)
			{
				editor.putLong("HiScore", timeTaken);
				editor.commit();
				fastestTime = timeTaken;
			}
			distanceRemaining = 0;
			state = GameState.WIN;
			_sm.playSound("win");
		}
	}

	private void draw()
	{
		if (ourHolder.getSurface().isValid())
		{
			canvas = ourHolder.lockCanvas();

			//paint.setColor(Color.argb(255, 0, 0, 0));
			canvas.drawBitmap(background, 0, 0, paint);
			paint.setColor(Color.argb(255, 255, 255, 255));
			for (GODust sd : dustList)
			{
				canvas.drawPoint(sd.getX(), sd.getY(), paint);
			}

			//canvas.drawColor(Color.argb(255, 0, 0, 0));
			canvas.drawBitmap
					(player.getBitmap(),
							player.getX(),
							player.getY(), paint);

			for (int i = 0; i < enemyCount; i++)
			{
				canvas.drawBitmap
						(enemy[i].getBitmap(),
								enemy[i].getX(),
								enemy[i].getY(), paint);
			}

			//Start drawing the HUD
			if (state == GameState.PLAYING)
			{
				canvas.drawText(getResources().getString(R.string.ingame_fastest) + ": " + fastestTime + "s", 10, paintInfo.getTextSize(), paintInfo);
				canvas.drawText(getResources().getString(R.string.ingame_time) + ": " + timeTaken + "s", screenX / 2, paintInfo.getTextSize(), paintInfo);
				canvas.drawText(getResources().getString(R.string.ingame_distance) + ": " + distanceRemaining / 1000 + "km",
						screenX / 3, screenY - 20, paintInfo);
				canvas.drawText(getResources().getString(R.string.ingame_shields) + ": " + player.getShieldStrength(), 10,
						screenY - 20, paintInfo);
				canvas.drawText(getResources().getString(R.string.ingame_speed) + ": " + player.getSpeed() * 60 + "mps",
						(screenX / 3) * 2, screenY - 20, paintInfo);
			}
			else if (state == GameState.LOSE)
			{
				canvas.drawColor(Color.argb(200, 0, 0, 0));
				canvas.drawText(getResources().getString(R.string.ingame_gameover), screenX / 2, screenY / 2, paintState);
				canvas.drawText(getResources().getString(R.string.ingame_fastest) + ": " +
						fastestTime + "s", screenX / 2, screenY / 2 +
						paintState.getTextSize() + 5, paintTip);
				canvas.drawText(getResources().getString(R.string.ingame_time) + ": " + timeTaken +
						"s", screenX / 2, screenY / 2 + paintState.getTextSize() + 5 +
						paintTip.getTextSize(), paintTip);
				canvas.drawText(getResources().getString(R.string.ingame_dist_remain) + ": " +
						distanceRemaining / 1000 + " KM", screenX / 2, screenY / 2 +
						paintState.getTextSize() + 10 + 2 * paintTip.getTextSize(), paintTip);
				canvas.drawText(getResources().getString(R.string.ingame_line_end), screenX / 2, screenY / 2 +
						paintState.getTextSize() + 15 + 3 * paintTip.getTextSize(), paintTip);
			}
			else if (state == GameState.PAUSE)
			{
				canvas.drawColor(Color.argb(200, 0, 0, 0));
				canvas.drawText(getResources().getString(R.string.ingame_pause), screenX / 2, screenY / 2, paintState);
				canvas.drawText(getResources().getString(R.string.ingame_line_1), screenX / 2, screenY / 2 +
						paintState.getTextSize() + 5, paintTip);
				canvas.drawText(getResources().getString(R.string.ingame_line_2), screenX / 2, screenY / 2 + paintState.getTextSize() + 5 +
						paintTip.getTextSize(), paintTip);
				canvas.drawText(getResources().getString(R.string.ingame_line_3), screenX / 2, screenY / 2 +
						paintState.getTextSize() + 10 + 2 * paintTip.getTextSize(), paintTip);
				canvas.drawText(getResources().getString(R.string.ingame_line_end), screenX / 2, screenY / 2 +
						paintState.getTextSize() + 15 + 3 * paintTip.getTextSize(), paintTip);
			}
			else
			{
				canvas.drawColor(Color.argb(200, 0, 0, 0));
				canvas.drawText(getResources().getString(R.string.ingame_win), screenX / 2, screenY / 2, paintState);
				canvas.drawText(getResources().getString(R.string.ingame_fastest) + ": " +
						fastestTime + "s", screenX / 2, screenY / 2 +
						paintState.getTextSize() + 5, paintTip);
				canvas.drawText(getResources().getString(R.string.ingame_time) + ": " + timeTaken +
						"s", screenX / 2, screenY / 2 + paintState.getTextSize() + 5 +
						paintTip.getTextSize(), paintTip);
				canvas.drawText(getResources().getString(R.string.ingame_dist_remain) + ": " +
						distanceRemaining / 1000 + " KM", screenX / 2, screenY / 2 +
						paintState.getTextSize() + 10 + 2 * paintTip.getTextSize(), paintTip);
				canvas.drawText(getResources().getString(R.string.ingame_line_end), screenX / 2, screenY / 2 +
						paintState.getTextSize() + 15 + 3 * paintTip.getTextSize(), paintTip);
			}
			//End drawing the HUD
			ourHolder.unlockCanvasAndPost(canvas);
		}
	}

	private void control()
	{
		try
		{
			gameThread.sleep(17);//1000(milliseconds)/60(FPS)
		}
		catch (InterruptedException e)
		{
		}
	}

	public void pause()
	{
		playing = false;
		try
		{
			gameThread.join();
		}
		catch (InterruptedException e)
		{
		}
	}

	public void resume()
	{
		playing = true;
		gameThread = new Thread(this);
		gameThread.start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent motionEvent)
	{
		switch (motionEvent.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
				if (state != GameState.PLAYING)
				{
					player.regainShieldStrength();
					distanceRemaining = 10000;
					state = GameState.PLAYING;
				}
				else
				{
					player.startBoosting();
				}
				break;
			case MotionEvent.ACTION_UP:
				player.stopBoosting();
				break;
		}
		return true;
	}
}
