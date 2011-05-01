/* Copyright (c) 2010 ARTags Project owners (see http://www.artags.org)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.artags.android.app;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import org.artags.android.app.ar.BrowserService;

/**
 *
 * @author Pierre Levy, Pierre Gros
 */
public class MainActivity extends Activity implements OnClickListener
{

    public static final String INTENT_PACKAGE = "org.artags.android.app";
    public static final String INTENT_MAIN_CLASS = INTENT_PACKAGE + ".MainActivity";
    public static final String INTENT_MYLOCATION_CLASS = INTENT_PACKAGE + ".MyLocationActivity";
    private static final String INTENT_DRAW_CLASS = INTENT_PACKAGE + ".DrawActivity";
    private static final String INTENT_PREFERENCES_CLASS = INTENT_PACKAGE + ".PreferencesActivity";
    private static final String INTENT_CREDITS_CLASS = INTENT_PACKAGE + ".CreditsActivity";
    private static final int DIALOG_PROGRESS = 0;
    private static final int PREFERENCES_MENU_ID = 0;
    private static final int CREDITS_MENU_ID = 1;
    private ImageButton mButtonDraw;
    private ImageButton mButtonDisplay;
    private ProgressThread progressThread;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.main);
        mButtonDraw = (ImageButton) findViewById(R.id.button_draw);
        mButtonDisplay = (ImageButton) findViewById(R.id.button_display);

        mButtonDraw.setOnClickListener(this);
        mButtonDisplay.setOnClickListener(this);
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case DIALOG_PROGRESS:
                progressDialog = new ProgressDialog(this);
                //               progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage(getString(R.string.dialog_loading));
                progressThread = new ProgressThread(handler);
                progressThread.start();
                return progressDialog;
        }
        return null;
    }

    public void onClick(View view)
    {
        if (view == mButtonDraw)
        {
            Intent intent = new Intent();
            intent.setClassName(INTENT_PACKAGE, INTENT_DRAW_CLASS);
            startActivity(intent);
        } else if (view == mButtonDisplay)
        {
            showDialog(DIALOG_PROGRESS);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        {

            if ((keyCode == KeyEvent.KEYCODE_BACK) && isMenuVisible())
            {
                showHideMenu();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean launchAugmentedReality()
    {
        BrowserService.instance().startBrowser(this);

        return true;
    }
    final Handler handler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            boolean bOk = msg.getData().getBoolean("completed");
            dismissDialog(DIALOG_PROGRESS);
            removeDialog(DIALOG_PROGRESS);
        }
    };

    private class ProgressThread extends Thread
    {

        Handler mHandler;

        ProgressThread(Handler h)
        {
            mHandler = h;
        }

        @Override
        public void run()
        {
            Looper.prepare();
            boolean bLaunch = launchAugmentedReality();
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putBoolean("completed", bLaunch);
            msg.setData(b);
            mHandler.sendMessage(msg);
            Looper.loop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        {
            createMenu();
        } else
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_preferences:
                onPreferences();
                return true;

            case R.id.menu_credits:
                onCredits();
                return true;

        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        {
            showHideMenu();
        }
        return true;
    }

    private void onCredits()
    {
        Intent intentCredits = new Intent();
        intentCredits.setClassName(INTENT_PACKAGE, INTENT_CREDITS_CLASS);
        startActivity(intentCredits);
    }

    private void onPreferences()
    {
        Intent intent = new Intent();
        intent.setClassName(INTENT_PACKAGE, INTENT_PREFERENCES_CLASS);
        startActivity(intent);
    }

    ////////////////////////////////////////////////////////////////////////////
    // specific menu implementation before Honeycomb
    public void menuSelected(int item)
    {

        switch (item)
        {
            case PREFERENCES_MENU_ID:
                onPreferences();
                break;

            case CREDITS_MENU_ID:
                onCredits();
                break;

        }
        hideMenu();
    }

    //create and populate the menu
    private void createMenu()
    {
        LayoutInflater inflater = (LayoutInflater) getWindow().getLayoutInflater();
        View menuView = inflater.inflate(R.layout.menu_main, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addContentView(menuView, layoutParams);

        Button undoButton = (Button) this.findViewById(R.id.button_config);
        undoButton.setOnClickListener(new View.OnClickListener()
        {
            //@Override

            public void onClick(View v)
            {
                menuSelected(PREFERENCES_MENU_ID);
            }
        });

        Button sendButton = (Button) this.findViewById(R.id.button_credits);
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            //@Override

            public void onClick(View v)
            {
                menuSelected(CREDITS_MENU_ID);
            }
        });
    }
    //Call this to show the menu or hide it if already displayed

    private void showHideMenu()
    {
        LinearLayout footer = (LinearLayout) this.findViewById(R.id.footer_organize);
        if (footer == null)
        {
            return;
        }
        if (isMenuVisible())
        {
            footer.setVisibility(View.GONE);
        } else
        {
            footer.setVisibility(View.VISIBLE);
        }
    }

    private void hideMenu()
    {
        LinearLayout footer = (LinearLayout) this.findViewById(R.id.footer_organize);
        if (footer == null)
        {
            return;
        }
        footer.setVisibility(View.GONE);

    }

    private boolean isMenuVisible()
    {
        LinearLayout footer = (LinearLayout) this.findViewById(R.id.footer_organize);
        if (footer == null)
        {
            return false;
        }
        int visible = footer.getVisibility();
        switch (visible)
        {
            case View.GONE:
            case View.INVISIBLE:
                return false;
            case View.VISIBLE:
            default:
                return true;
        }
    }
}
