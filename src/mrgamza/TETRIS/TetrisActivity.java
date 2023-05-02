package mrgamza.TETRIS;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TetrisActivity extends Activity
{
	public static final String TAG = "TETRIS";
	TetrisMain xTetrisMain;
	TextView xTextView_Message;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		xTetrisMain = (TetrisMain)findViewById(R.id.TetrisMain);
		xTextView_Message = (TextView)findViewById(R.id.Message);

		xTetrisMain.setMessage(xTextView_Message);
		xTetrisMain.setTitle(this);

		// 기존에 저장된 내용이 있다면...
		if(savedInstanceState == null)
		{
			xTetrisMain.setStatus(TetrisMain.READY);
		}
		else
		{
			Bundle map = savedInstanceState.getBundle(TAG);
			if(map != null)
			{
				xTetrisMain.restoreState(map);
			}
			else
			{
				xTetrisMain.setStatus(TetrisMain.PAUSE);
			}
		}

		xTetrisMain.update();
	}
}