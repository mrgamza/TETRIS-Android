package mrgamza.TETRIS;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class TetrisDraw extends View
{
	public static int screenLength; // 화면의 크기

	public Rect rectScreen = new Rect(); // 사용 가능한 화면 영
	public Rect rectBoard = new Rect(); // 순수하게 테트리스 판 영역

	public final static int COLS = 10; // 판의 칸 수
	public final static int ROWS = 20; // 판의 줄 수
	public int blockColor[]; // 블럭의 색지정

	private byte status = READY; // 현재 게임의 상태

	public byte getStatus()
	{
		return status;
	}

	public void setStatus(byte status)
	{
		this.status = status;
	}

	public static final byte PAUSE = 0; // 일시 중지
	public static final byte READY = 1; // 게임 시작 전 준비 완료
	public static final byte RUN = 2; // 게임 진행 중
	public static final byte END = 3; // 게임 종료됨

	byte prevBoard[][]; // 직전 판의 내용
	byte nowBoard[][]; // 현재 (변경 후) 판의 내용

	Block nowBlock[] = new Block[4]; // 현재의 블럭

	public TetrisDraw(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initialize();
	}

	public TetrisDraw(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize();
	}

	public TetrisDraw(Context context)
	{
		super(context);
		initialize();
	}

	// 게임의 초기화
	public void initialize()
	{
		blockColor = new int[8];

		blockColor[0] = Color.BLACK; // BackGround

		blockColor[1] = Color.RED;
		blockColor[2] = Color.GREEN;
		blockColor[3] = Color.BLUE;
		blockColor[4] = Color.CYAN;
		blockColor[5] = Color.GRAY;
		blockColor[6] = Color.WHITE;
		blockColor[7] = Color.YELLOW;

		prevBoard = new byte[COLS][ROWS + 4];
		nowBoard = new byte[COLS][ROWS + 4];
	}

	// 화면을 깨끗하게 정리한다.
	void cleanBoard()
	{
		for(int col = 0; col < COLS; col++)
		{
			for(int row = 0; row < ROWS; row++)
			{
				prevBoard[col][row] = -1;
				nowBoard[col][row] = 0;
			}
		}

		nowBlock = new Block[4];
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		int width = w / COLS;
		int height = h / ROWS;

		screenLength = Math.min(width, height);

		rectScreen = new Rect(0, 0, w, h);

		int xoffset = (w - screenLength * COLS) / 2;
		int yoffset = (h - screenLength * ROWS) / 2;

		rectBoard = new Rect(xoffset, yoffset, xoffset + screenLength * COLS, yoffset + screenLength * ROWS);
	}
	
	private boolean moveBlock(Block from[], Block to[])
	{
		// 블럭이 움직일수 있는지 확인한다.
		re:
		
		for(int i=0 ; i< to.length ; i++)
		{
			// 화면밖으로 가려고 한다면 false를 반환
			if(!to[i].isBounds())
			{
				return false;
			}
			
			// 블럭이 있다면 움직이지 말라는거 같은데...
			// 이해 필요
			if(nowBoard[to[i].getX()][to[i].getY()] != 0)
			{
				for(int j=0 ; j<from.length ; j++)
				{
					if(to[i].isEqual(from[j]))
					{
						continue re;
					}
				}
				return false;
			}
		}
	
		// 현재 블럭을 그리고 기존의 블럭은 지운다.
		for(int i=0 ; i<from.length; i++)
		{
			if(from[i].isBounds())
			{
				nowBoard[from[i].getX()][from[i].getY()] = 0;
				prevBoard[from[i].getX()][from[i].getY()] = -1;
			}
		}
		// 색을 변경하여준다.
		for(int i=0 ; i<to.length ; i++)
		{
			nowBoard[to[i].getX()][to[i].getY()] = to[i].getColor();
		}
		
		return true;
	}
	
	protected boolean newBlock()
	{

		// 새로운 블럭이므로, 기존의 위치는 판 밖의 영역으로 지정한다.
		Block old[] = new Block[4];
		old[0] = old[1] = old[2] = old[3] = new Block(-1, -1, (byte)0);

		// 임의의 모양을 가져오도록 한다.
		int blockType = (int)(Math.random() * 7);

		// 블럭을 생성한다.
		newBlock(blockType);

		// 생성한 블럭을 이동한다.
		// 만약, 이동에 실패한다면(화면 중앙 상단에 새로운 블럭이 생성되는 위치에
		// 이미 다른 블럭이 존재하는 경우) false 를 반환한다.
		return moveBlock(old, nowBlock);

	}

	/*
	 * 화면 중앙 상단에 지정된 형태의 새로운 블럭을 생성한다. 생성된 블럭은 현재의 블럭으로 지정된다.
	 */
	private void newBlock(int type)
	{

		int m = COLS / 2;

		switch (type)
		{
			case 0 :
				// ####
				nowBlock[0] = new Block(m - 1, 0, (byte)1);
				nowBlock[1] = new Block(m - 2, 0, (byte)1);
				nowBlock[2] = new Block(m, 0, (byte)1);
				nowBlock[3] = new Block(m + 1, 0, (byte)1);
				break;

			case 1 :
				// ###
				// #
				nowBlock[0] = new Block(m, 0, (byte)5);
				nowBlock[1] = new Block(m, 1, (byte)5);
				nowBlock[2] = new Block(m - 1, 0, (byte)5);
				nowBlock[3] = new Block(m + 1, 0, (byte)5);
				break;

			case 2 :
				// ##
				// ##
				nowBlock[0] = new Block(m, 0, (byte)2);
				nowBlock[1] = new Block(m - 1, 1, (byte)2);
				nowBlock[2] = new Block(m, 1, (byte)2);
				nowBlock[3] = new Block(m + 1, 0, (byte)2);
				break;

			case 3 :
				// ##
				// ##
				nowBlock[0] = new Block(m, 0, (byte)7);
				nowBlock[1] = new Block(m + 1, 1, (byte)7);
				nowBlock[2] = new Block(m, 1, (byte)7);
				nowBlock[3] = new Block(m - 1, 0, (byte)7);
				break;

			case 4 :
				// ##
				// ##
				nowBlock[0] = new Block(m - 1, 1, (byte)3);
				nowBlock[1] = new Block(m, 1, (byte)3);
				nowBlock[2] = new Block(m - 1, 0, (byte)3);
				nowBlock[3] = new Block(m, 0, (byte)3);

				break;

			case 5 :
				// #
				// ###
				nowBlock[0] = new Block(m, 1, (byte)6);
				nowBlock[1] = new Block(m - 1, 1, (byte)6);
				nowBlock[2] = new Block(m + 1, 1, (byte)6);
				nowBlock[3] = new Block(m + 1, 0, (byte)6);
				break;

			case 6 :
				// #
				// ###
				nowBlock[0] = new Block(m, 1, (byte)4);
				nowBlock[1] = new Block(m + 1, 1, (byte)4);
				nowBlock[2] = new Block(m - 1, 1, (byte)4);
				nowBlock[3] = new Block(m - 1, 0, (byte)4);
				break;
		}
	}
	
	/**
	 * 현재의 블럭을 이동한다.
	 * 
	 * @param byx
	 *            수평 이동 변위
	 * @param byy
	 *            수직 이동 변위
	 * @param rotate
	 *            회전 여부
	 * 
	 * @return 이동 가능한 경우 true, 그렇지 못할 경우 false
	 */
	synchronized boolean moveCheck(int byx, int byy, boolean rotate)
	{

		Block newpos[] = new Block[4];

		for(int i = 0; i < 4; i++)
		{
			if(rotate)
			{
				int dx = nowBlock[i].getX() - nowBlock[0].getX();
				int dy = nowBlock[i].getY() - nowBlock[0].getY();

				newpos[i] = new Block(nowBlock[0].getX() + dy, nowBlock[0].getY() - dx, nowBlock[i].getColor());

			}
			else
			{
				newpos[i] = new Block(nowBlock[i].getX() + byx, nowBlock[i].getY() + byy, nowBlock[i].getColor());
			}
		}

		if(!moveBlock(nowBlock, newpos))
			return false;

		nowBlock = newpos;

		return true;
	}

	/**
	 * 화면에 그리는 부분이다.
	 */
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		Paint p = new Paint();

		p.setColor(0xffffffff);
		canvas.drawRect(rectScreen, p);

		p.setColor(Color.BLACK);
		canvas.drawRect(rectBoard, p);

		for(int c = 0; c < COLS; c++)
		{
			for(int r = 0; r < ROWS; r++)
			{
				{
					p.setColor(blockColor[nowBoard[c][r]]);

					// Log.v(Tetris4Android.TAG4LOG,
					// "("+rectSquare.left+","+rectSquare.top+")-("+rectSquare.right+","+rectSquare.bottom+") COLOR = "
					// + colors[boardCurr[c][r]]);

					// RoundRect
					{
						RectF rectSquare = new RectF(
									rectBoard.left + screenLength * c, rectBoard.top + screenLength * r
								, rectBoard.left + screenLength * (c + 1) - 1, rectBoard.top + screenLength * (r + 1) - 1
							);
						canvas.drawRoundRect(rectSquare, 4F, 4F, p);
					}
					// // BasicRect
					// {
					// Rect rectSquare = new Rect(
					// rectBoard.left + squareLength * c, rectBoard.top +
					// squareLength * r
					// , rectBoard.left + squareLength * (c + 1) - 1,
					// rectBoard.top + squareLength * (r + 1) - 1
					// );
					// canvas.drawRect(rectSquare, p);
					// }
					nowBoard[c][r] = nowBoard[c][r];
				}
			}
		}
	}

	/* 채워진 라인이 있다면 지운다. */
	int removeLine()
	{
		int removeLines = 0;
		
		for(int r = ROWS - 1; r >= 0; r--)
		{
			int c;
			for(c = 0; c < COLS; c++)
			{
				if(nowBoard[c][r] <= 0) // 빈 공간이 있다면, 더 살펴볼 필요가 없다.
					break;
			}

			// 채워졌다면...
			if(c == COLS)
			{
				for(int k = r; k > 0; k--)
				{
					for(int l = 0; l < COLS; l++)
					{
						nowBoard[l][k] = nowBoard[l][k - 1];
					}
				}
				// 현재 한 줄이 올라갔으므로, 다시 현재의 줄을 검사한다.
				r++;
				removeLines += (++removeLines);
			}
		}
		
		return removeLines;
	}

	/* 현재의 상태를 저장할 수 있도록 한다. 이것은 restoreState() 와 쌍을 이루어야 한다. */
	public Bundle saveState()
	{
		Bundle map = new Bundle();

		byte board[] = new byte[COLS * ROWS];
		for(int c = 0; c < COLS; c++)
		{
			for(int r = 0; r < ROWS; r++)
			{
				board[r * COLS + c] = nowBoard[c][r];
			}
		}

		map.putByte("mode", status);
		map.putByteArray("boardCurr", board);

		return map;
	}

	/* 현재의 상태를 복원한다. 이것은 saveState() 와 쌍을 이루어야 한다. */
	public void restoreState(Bundle map)
	{
		setStatus(PAUSE);

		byte board[];
		board = map.getByteArray("boardCurr");

		for(int c = 0; c < COLS; c++)
		{
			for(int r = 0; r < ROWS; r++)
			{
				nowBoard[c][r] = board[r * COLS + c];
			}
		}

		setStatus(map.getByte("mode"));
	}
}
