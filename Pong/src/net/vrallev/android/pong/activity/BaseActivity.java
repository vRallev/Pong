package net.vrallev.android.pong.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Message;

/**
 * A base class for all activities in this application. It provides several helper methods to store data over configuration changes,
 * to enable the foreground dispatch system and to show dialogs.
 * 
 * @author Ralf Wondratschek
 *
 */
public abstract class BaseActivity extends Activity {
	
	private static final String KEY_MESSAGE_LIST = "messageList";
	private static final String NO_TAG = "no_tag";
	
	private static final int DISMISS_DIALOG = 396;
	private static final int SHOW_DIALOG = 634;
	private static final int REPLACE_FRAGMENT = 964;
	private static final int REMOVE_FRAGMENT = 684;
	
	private static Map<Class<? extends BaseActivity>, BaseActivity> activities = new HashMap<Class<? extends BaseActivity>, BaseActivity>();
	private static BaseActivity currentActivity;
	
	/**
	 * Gives you static access to the backstack. This method tries to find the last displayed,
	 * to the class corresponding instance.
	 * 
	 * @param clazz The {@link Class} you are trying to find. 
	 * @return The last displayed instance or {@code null}.
	 */
	public static <T extends BaseActivity> BaseActivity findLastVisibleActivity(Class<T> clazz) {
		return activities.get(clazz);
	}
	
	/**
	 * Gives you static access to the current displayed instance.
	 * 
	 * @return The currently displayed {@code BaseActivity}.
	 */
	public static BaseActivity getCurrentActivity() {
		return currentActivity;
	}
	
	private boolean mVisible = false;
	private ArrayList<Message> toRunWhenVisible;
	
	private RetainInstanceFragment mRetainFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		activities.put(getClass(), this);
		
		if (savedInstanceState != null) {
			toRunWhenVisible = savedInstanceState.getParcelableArrayList(KEY_MESSAGE_LIST);
		}
		
		if (toRunWhenVisible == null) {
			toRunWhenVisible = new ArrayList<Message>();
		}
		
		mRetainFragment = RetainInstanceFragment.findOrCreateFragment(getFragmentManager());
	}

	@Override
	protected void onResume() {
		super.onResume();

		currentActivity = this;
		
		mVisible = true;
		
		for (Message message : toRunWhenVisible) {
			switch (message.what) {
				
				case SHOW_DIALOG:
					DialogHolder holder = (DialogHolder) message.obj;
					internalShowDialog(holder.mFragment, holder.mTag);
					break;
				
				case DISMISS_DIALOG:
					internalDismissDialog((String) message.obj);
					break;
					
				case REPLACE_FRAGMENT:
					ReplaceFragmentHolder fragmentHolder = (ReplaceFragmentHolder) message.obj;
					internalReplaceFragment(fragmentHolder.mContainerViewId, fragmentHolder.mFragment, fragmentHolder.mTag, fragmentHolder.mTransition);
					break;
					
				case REMOVE_FRAGMENT:
					fragmentHolder = (ReplaceFragmentHolder) message.obj;
					internalRemoveFragment(fragmentHolder.mFragment, fragmentHolder.mTransition);
					break;
			}
		}
		
		toRunWhenVisible.clear();
	}

	@Override
	protected void onPause() {
		mVisible = false;
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		activities.remove(getClass());
		
		// check this explicit instance
		if (currentActivity == this) {
			currentActivity = null;
		}
		
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putParcelableArrayList(KEY_MESSAGE_LIST, toRunWhenVisible);
	}

	/**
	 * Helper method to display a {@link DialogFragment}. If the activity is not visible, the dialog gets stored. After
	 * the activity is visible again, the dialog gets displayed. 
	 * 
	 * @param dialogFragment The {@link DialogFragment}, which should be shown. 
	 * @param tag The fragment's tag. If the value is {@code null}, a default tag gets used. 
	 */
	public void showDialog(DialogFragment dialogFragment, String tag) {
		if (tag == null) {
			tag = NO_TAG;
		}
		
		if (mVisible) {
			internalShowDialog(dialogFragment, tag);
		} else {
			Message message = new Message();
			message.obj = new DialogHolder(dialogFragment, tag);
			message.what = SHOW_DIALOG;
			
			toRunWhenVisible.add(message);
		}
	}
	
	/**
	 * Helper method to dismiss a dialog. If the activity is not visible, the action gets stored. After the
	 * activity is visible again, the dialog gets dismissed. 
	 * 
	 * @param tag The dialog's tag. If the value is {@code null}, the default tag gets used. 
	 */
	public void dismissDialog(String tag) {
		if (tag == null) {
			tag = NO_TAG;
		}
		
		if (mVisible) {
			internalDismissDialog(tag);
		} else {
			Message message = new Message();
			message.obj = tag;
			message.what = DISMISS_DIALOG;
			
			toRunWhenVisible.add(message);
		}
	}
	
	public void replaceFragment(int containerViewId, Fragment fragment, String tag, int transition) {
		if (mVisible) {
			internalReplaceFragment(containerViewId, fragment, tag, transition);
		} else {
			Message message = new Message();
			message.what = REPLACE_FRAGMENT;
			message.obj = new ReplaceFragmentHolder(containerViewId, fragment, tag, transition);

			toRunWhenVisible.add(message);
		}
	}
	
	public void removeFragment(Fragment fragment, int transition) {
		if (mVisible) {
			internalRemoveFragment(fragment, transition);
		} else {
			Message message = new Message();
			message.what = REMOVE_FRAGMENT;
			message.obj = new ReplaceFragmentHolder(-1, fragment, null, transition);
			
			toRunWhenVisible.add(message);
		}
	}
	
	public Object put(String key, Object object) {
		return mRetainFragment.put(key, object);
	}
	
	public Object load(String key) {
		return mRetainFragment.get(key);
	}
	
	public Object remove(String key) {
		return mRetainFragment.remove(key);
	}
	
	private void internalShowDialog(DialogFragment dialogFragment, String tag) {
		dialogFragment.show(getFragmentManager(), tag);
	}

	private void internalDismissDialog(String tag) {
		Fragment fragment = getFragmentManager().findFragmentByTag(tag);
		if (fragment instanceof DialogFragment) {
			((DialogFragment) fragment).dismiss();
		}
	}
	
	private void internalReplaceFragment(int containerViewId, Fragment fragment, String tag, int transition) {
		getFragmentManager().beginTransaction()
				.setTransition(transition)
				.replace(containerViewId, fragment, tag)
				.commit();
	}
	
	private void internalRemoveFragment(Fragment fragment, int transition) {
		getFragmentManager().beginTransaction()
				.setTransition(transition)
				.remove(fragment)
				.commit();
	}
	
	/**
	 * Simple helper class.
	 * 
	 * @author Ralf Wondratschek
	 *
	 */
	private static class DialogHolder {
		public DialogFragment mFragment;
		public String mTag;

		public DialogHolder(DialogFragment fragment, String tag) {
			mFragment = fragment;
			mTag = tag;
		}
	}
	
	private static class ReplaceFragmentHolder {
		public Fragment mFragment;
		public int mContainerViewId;
		public String mTag;
		public int mTransition;
		
		public ReplaceFragmentHolder(int containerViewId, Fragment fragment, String tag, int transition) {
			mContainerViewId = containerViewId;
			mFragment = fragment;
			mTag = tag;
			mTransition = transition;
		}
	}
	
	/**
	 * Non-UI fragment to store and load objects. The same instance is retained over configuration changes. 
	 * 
	 * @author Ralf Wondratschek
	 *
	 */
	public static class RetainInstanceFragment extends Fragment {
		
		private static final String FRAGMENT_TAG = "myRetainTag";
		
		public static RetainInstanceFragment findOrCreateFragment(FragmentManager manager) {
			Fragment fragment = manager.findFragmentByTag(FRAGMENT_TAG);
			if (fragment instanceof RetainInstanceFragment) {
				return (RetainInstanceFragment) fragment;
			}
			
			RetainInstanceFragment retainInstanceFragment = new RetainInstanceFragment();
			manager.beginTransaction().add(retainInstanceFragment, FRAGMENT_TAG).commit();
			return retainInstanceFragment;
		}
		
		private Map<String, Object> mStorage;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			setRetainInstance(true);
			mStorage = new HashMap<String, Object>();
		}
		
		public Object put(String key, Object object) {
			return mStorage.put(key, object);
		}
		
		public Object get(String key) {
			return mStorage.get(key);
		}
		
		public Object remove(String key) {
			return mStorage.remove(key);
		}
	}
}
