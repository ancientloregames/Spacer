package com.ancientlore.spacer;

import android.graphics.Bitmap;


abstract class GameObject
{
	private int x;
	private int y;
	private Bitmap bitmap;

	public void scaleBitmap(final int y)
	{
        /*if (x < 1000){
            bitmap = Bitmap.createScaledBitmap(bitmap,
                    bitmap.getWidth()/3,
                    bitmap.getHeight()/3,false);
            }
        else{
            bitmap = Bitmap.createScaledBitmap(bitmap,
                    bitmap.getWidth()/2,
                    bitmap.getHeight()/2,false);
        }*/
		if (bitmap.getWidth() > bitmap.getHeight())
		{
			final float ratio = bitmap.getWidth() / bitmap.getHeight();
			bitmap = Bitmap.createScaledBitmap(bitmap,
					(int) (y / 8 * ratio), y / 8, false);
		}
		else
		{
			final float ratio = bitmap.getHeight() / bitmap.getWidth();
			bitmap = Bitmap.createScaledBitmap(bitmap,
					y / 8, (int) (y / 8 * ratio), false);
		}
	}

	public Bitmap getBitmap()
	{
		return bitmap;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public void setBitmap(Bitmap bitmap)
	{
		this.bitmap = bitmap;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public void setY(int y)
	{
		this.y = y;
	}
}
