package mrgamza.TETRIS;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.GestureDetector.*;
import android.widget.*;

public class TetrisMain extends TetrisDraw
{
	private int FLING_INTERVER = 40;
	private int DOWN_INTERVER = 40;
	
	private 	GestureDetector xGestureDetector;
	
	private Context xContext = null;
	
	private Activity xActivity = null;
	
	private RefreshHandler xHandler = new RefreshHandler();
	
	private TextView xTextView_Message = null;
	
	private boolean isMove = false;
	private int iLastXPosition = 0;
	private int iLastYPosition = 0;
	
	private long iScore = 0;
	
	public TextView getMessage()
	{
		return xTextView_Message;
	}

	public void setMessage(TextView _message)
	{
		xTextView_Message = _message;
		xTextView_Message.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				cleanScore();
				setInterver();
				cleanBoard();
				xTextView_Message.setVisibility(INVISIBLE);
				invalidate();
				newBlock();
				setStatus(RUN);
				update();
			}
		});
	}
	
	private void cleanScore()
	{
		iScore = 0;
	}
	
	private void setInterver()
	{
		int iInterver = rectScreen.width() / COLS;
		
		FLING_INTERVER = iInterver + (iInterver / 10);
		DOWN_INTERVER = iInterver + (iInterver / 10);
	}
	
	public TetrisMain(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		
		xContext = context;
	}

	public TetrisMain(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		xContext = context;
	}

	public TetrisMain(Context context)
	{
		super(context);
		
		xContext = context;
	}

	public void initialize()
	{
		setFocusable(true);
		xGestureDetector = new GestureDetector(xContext, new SimpleGestureListener());
		
		super.initialize();
	}
	
	public void update()
	{
		if(getStatus() == RUN)
		{
			if(!moveCheck(0, 1, false))
			{
				iScore += removeLine() * 100;
				if(!newBlock())
				{
					if(xTextView_Message != null)
					{
						xTextView_Message.setText(R.string.end);
						xTextView_Message.setVisibility(VISIBLE);
					}
					setStatus(END);
					return;
				}
			}
			xHandler.sleep(600);
		}
	}
	
	public void setMessageView(TextView messageView)
	{
		this.xTextView_Message = messageView;
	}
	
	public void setTitle(Activity activity)
	{
		xActivity = activity;
	}

	@Override
	public void setStatus(byte mode)
	{
		switch (mode)
		{
			case RUN :
				xTextView_Message.setVisibility(INVISIBLE);
				super.setStatus(mode);
				update();
				break;
			case PAUSE :
				xTextView_Message.setText(R.string.pause);
				xTextView_Message.setVisibility(VISIBLE);
				break;
			case END :
				xTextView_Message.setText(R.string.end);
				xTextView_Message.setVisibility(VISIBLE);
				break;
			case READY :
				xTextView_Message.setText(R.string.ready);
				xTextView_Message.setVisibility(VISIBLE);
				break;
		}

		super.setStatus(mode);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
			case KeyEvent.KEYCODE_DPAD_CENTER :
				cleanBoard();
				xTextView_Message.setVisibility(INVISIBLE);
				invalidate();
				newBlock();
				setStatus(RUN);
				update();
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT :
				if(getStatus() == RUN)
					this.moveCheck(-1, 0, false);
				invalidate();
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT :
				if(getStatus() == RUN)
					this.moveCheck(1, 0, false);
				invalidate();
				break;
			case KeyEvent.KEYCODE_DPAD_UP :
				if(getStatus() == RUN)
					this.moveCheck(0, 0, true);
				invalidate();
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN :
				if(getStatus() == RUN)
					while (moveCheck(0, 1, false));
				invalidate();
				break;
			case KeyEvent.KEYCODE_SPACE :
				switch (getStatus())
				{
					case RUN :
						setStatus(PAUSE);
						xTextView_Message.setText(R.string.pause);
						xTextView_Message.setVisibility(VISIBLE);
						break;
					case PAUSE :
						xTextView_Message.setVisibility(INVISIBLE);
						setStatus(RUN);
						update();
						break;
				}
				break;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		super.onTouchEvent(event);
		
		if(getStatus() == RUN)
		{
			int iMotion = event.getAction();
			int iX = (int)event.getX();
			int iY = (int)event.getY();
			
			xGestureDetector.onTouchEvent(event);
			
			switch(iMotion)
			{
				case MotionEvent.ACTION_DOWN :
				{
					isMove = true;
					
					iLastXPosition = iX;
					iLastYPosition = iY;
					
					break;
				}
				
				case MotionEvent.ACTION_UP :
				{
					isMove = false;
					
					break;
				}
				
				case MotionEvent.ACTION_MOVE :
				{
					if(isMove)
					{
						boolean isLeft = ((iLastXPosition - iX) >= FLING_INTERVER);
						boolean isRight = ((iX - iLastXPosition) >= FLING_INTERVER);
						boolean isDown = ((iY - iLastYPosition) >= DOWN_INTERVER);
						
						if(isDown)
						{
							moveCheck(0, 1, false);
							invalidate();
							
							iLastXPosition = iX;
							iLastYPosition = iY;
						}
						else if(isLeft)
						{
							moveCheck(-1, 0, false);
							invalidate();	
							
							iLastXPosition = iX;
						}
						else if(isRight)
						{
							moveCheck(1, 0, false);
							invalidate();
							
							iLastXPosition = iX;
						}
					}
					
					break;
				}
			}
		}
		
		return true;
	}
	
	public class SimpleGestureListener implements OnGestureListener
	{
		@Override
		public boolean onDown(MotionEvent event)
		{
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e)
		{
			;;;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
		{
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e)
		{
			;;;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e)
		{
			moveCheck(0, 0, true);
			invalidate();
			
			return false;
		}
	
	}
	
	private class RefreshHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			TetrisMain.this.update();
			TetrisMain.this.invalidate();
			xActivity.setTitle("SCORE : " + iScore);
		}

		public void sleep(long delayMillis)
		{
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};
}
