package net.vrallev.android.pong.util;

import net.vrallev.android.pong.App;
import android.content.res.Configuration;
import android.graphics.Point;
import android.view.Surface;

/**
 * 
 * @author Ralf Wondratschek
 *
 */
public final class DisplayUtils {
	
	private DisplayUtils() {

	}
	
	public static int getDisplayDegrees() {
		int rotation = AndroidServices.getWindowManager().getDefaultDisplay().getRotation();
		switch (rotation) {
			case Surface.ROTATION_0:
				return 0;
			case Surface.ROTATION_90:
				return 90;
			case Surface.ROTATION_180:
				return 180;
			case Surface.ROTATION_270:
				return 270;
		}
		
		return -1;
	}

	public static Point getScreenSize() {
		Point point = new Point();
		AndroidServices.getWindowManager().getDefaultDisplay().getSize(point);
		return point;
	}
	
	public static boolean isPortrait() {
		return App.getInstance().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
	}
}
