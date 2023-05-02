package mrgamza.TETRIS;

public class Block
{
	int x, y; // X 좌표, Y 좌표
	byte color; // 색깔
	
	Block(int x, int y, byte color)
	{
		
		this.x = x;
		this.y = y;
		this.color = color;
	}
	
	int getX()
	{
		return x;
	}
	
	int getY()
	{
		return y;
	}
	
	byte getColor()
	{
		return color;
	}
	
	// 현재의 사각형이 판 안에 존재하는 것인지 확인
	boolean isBounds()
	{
		return (x >= 0 && x < TetrisMain.COLS && y >= 0 && y < TetrisMain.ROWS);
	}
	
	boolean isEqual(Block block)
	{
		return x == block.x && y == block.y && color == block.color;
	}
}