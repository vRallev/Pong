package net.vrallev.android.pong;

import net.vrallev.android.pong.util.AndroidServices;
import net.vrallev.android.pong.util.L;
import net.vrallev.android.pong.util.PreferencesMgr;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import de.greenrobot.event.EventBus;

/**
 * 
 * @author Ralf Wondratschek
 *
 */
public class App extends Application {
	
	public static final String TAG = "Pong";

	private static App instance;
	private static boolean debug;
	private static EventBus eventBus;
	private static Handler guiHandler;
	private static PreferencesMgr preferencesMgr;
	
	/**
	 * The flag for the debug mode can be set in the manifest file.
	 * 
	 * @return {@code true}, if the app is running in debug modus.
	 */
	public static boolean isDebuggable() {
		return debug;
	}
	
	/**
	 * @return The only instance at runtime.
	 */
	public static App getInstance() {
		return instance;
	}
	
	/**
	 * @return The default {@link EventBus}.
	 */
	public static EventBus getEventBus() {
		return eventBus;
	}

	/**
	 * @return A {@link Handler}, which is prepared for the GUI Thread. 
	 */
	public static Handler getGuiHandler() {
		return guiHandler;
	}
	
	/**
	 * @return A singleton to get access to the {@link SharedPreferences}.
	 */
	public static PreferencesMgr getPreferencesMgr() {
		return preferencesMgr;
	}
	
	@Override
	public void onCreate() {
		debug = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
		instance = this;
		
		L.setInstance(new L(TAG, debug));
		
		AndroidServices.init(getApplicationContext());
		preferencesMgr = new PreferencesMgr(this);
		
		guiHandler = new Handler();
		
		eventBus = EventBus.getDefault();
		
		initDebugOptions(debug);
		initPreferences(preferencesMgr);

		super.onCreate();
	}
	
	private void initDebugOptions(boolean debug) {
		if (debug) {
			
		} else {

		}
	}
	
	private void initPreferences(PreferencesMgr preferencesMgr) {
	}
}
