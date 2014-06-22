import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Random;
import javax.swing.JFrame;

public class CUI2048 {
	public static void main(String[] args) throws InterruptedException,
			IOException {
			
		// ボードサイズ
		int Board_Size = 10;
		// ランダムに出現する要素
		int Appear_Element[] = { 2, 4 ,5};
		// ランダムに出現する要素数
		int Appear_Number = 2;
		// クリアナンバー（n^x になってなければエラー終了）
		int Clear_Number = 2048;

		new Game(Board_Size, Appear_Element, Appear_Number,	Clear_Number);
	}

}

@SuppressWarnings("serial")
class Game extends JFrame implements KeyListener {
	int Space = 0;
	int B_SIZE, APPEAR_ELEMENT[], APPEAR_NUM, CLEAR_NUMBER;
	int BOARD[][];

	Direction in = new Direction();
	Random rand = new Random();
	boolean Move_flag = false;

	// コンストラクタ
	Game(int board_size, int[] appear_element, int appear_number,
			int clear_number) throws InterruptedException, IOException {

		super("KeyControl :  ←,↑,→,↓   or   a,w,d,s");
		addKeyListener(this);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 300);
		this.setVisible(true);

		this.B_SIZE = board_size;
		this.APPEAR_ELEMENT = appear_element;
		this.APPEAR_NUM = appear_number;
		this.BOARD = new int[B_SIZE][B_SIZE];
		this.CLEAR_NUMBER = clear_number;

		// ゲームボードの初期化
		for (int i = 0; i < B_SIZE; i++)
			for (int j = 0; j < B_SIZE; j++)
				BOARD[i][j] = Space;

		Game_roop();

	}

	// ゲームのメインループ
	void Game_roop() throws InterruptedException {

		appear();
		this.print_board();

		while (true) {

			// クリア判定
			Clear_Judge();

			// 入力待ちフラグON
			in.wait_in();

			// 入力されるまでWait
			while (!in.go)
				Thread.sleep(50);

			// 入力方向に応じたアクション
			this.move();

			// 入力前と入力後に変化がある場合のみゲームが進行される
			if (Move_flag) {
				this.appear();
				this.print_board();
			}
			
		}
	}

	// クリア判定メソッド
	void Clear_Judge() {
		for (int i = 0; i < B_SIZE; i++)
			for (int j = 0; j < B_SIZE; j++)
				if (BOARD[i][j] == CLEAR_NUMBER) {
					System.out.println();
					System.out.println("---GAME CLEAR!---");
					System.exit(0);
				}
	}

	// ゲームボード出力（コンソール）メソッド
	void print_board() {
		System.out.println();
		for (int i = 0; i < B_SIZE; i++) {
			System.out.println();
			for (int j = 0; j < B_SIZE; j++){
				System.out.print(String.format("%1$3d" , BOARD[i][j]));
				if(j != B_SIZE-1)
					System.out.print(",");
			}
		}
	}

	// ゲームボードの空いたマスのランダムな位置に要素を出現させるメソッド
	void appear() {
		int x, y;
		boolean gameover;

		for (int a = 0; a < this.APPEAR_NUM; a++) {
			gameover = true;
			while (true) {
				x = Math.abs(rand.nextInt() % B_SIZE);
				y = Math.abs(rand.nextInt() % B_SIZE);

				if (BOARD[y][x] == Space) {
					BOARD[y][x] = APPEAR_ELEMENT[Math.abs(rand.nextInt()
							% APPEAR_ELEMENT.length)];
					break;
				}

				for (int i = 0; i < B_SIZE; i++)
					for (int j = 0; j < B_SIZE; j++)
						if (BOARD[i][j] == Space) {
							gameover = false;
							break;
						}

				if (gameover) {
					System.out.println("---GAME OVER---");
					System.exit(0);
				}

			}
		}
	}

	// 入力（上下左右）に応じて数値を移動させる操作メソッド
	void move() {
		// 方向に応じて探索順序を変える必要がある。
		Move_flag = false;
		// 上
		if (in.get_dir() == 0) {
			for (int i = 0; i < B_SIZE; i++)
				for (int j = 0; j < B_SIZE; j++)
					if (!(BOARD[i][j] == Space))
						moveNum(j, i);

		}
		// 右
		else if (in.get_dir() == 1) {
			for (int i = B_SIZE - 1; i >= 0; i--)
				for (int j = 0; j < B_SIZE; j++)
					if (BOARD[j][i] != Space)
						moveNum(i, j);

		}
		// 下
		else if (in.get_dir() == 2) {
			for (int i = B_SIZE - 1; i >= 0; i--)
				for (int j = 0; j < B_SIZE; j++)
					if (BOARD[i][j] != Space)
						moveNum(j, i);

		}
		// 左
		else if (in.get_dir() == 3)
			for (int i = 0; i < B_SIZE; i++)
				for (int j = 0; j < B_SIZE; j++)
					if (BOARD[j][i] != Space)
						moveNum(i, j);

	}

	// 実際に数値の移動を行うメソッド
	private void moveNum(int x, int y) {
		int next_x, next_y;

		while (true) {
			next_x = x + in.x;
			next_y = y + in.y;

			if (next_x < 0 || next_x >= B_SIZE || next_y < 0
					|| next_y >= B_SIZE) {
				return;
			} else if (BOARD[y][x] != BOARD[next_y][next_x]
					&& BOARD[next_y][next_x] != Space) {
				return;
			}

			if (BOARD[next_y][next_x] == Space) {
				BOARD[next_y][next_x] = BOARD[y][x];
				BOARD[y][x] = Space;
				Move_flag = true;
			} else if (BOARD[y][x] == BOARD[next_y][next_x]) {
				BOARD[next_y][next_x] *= 2;

				BOARD[y][x] = Space;
				Move_flag = true;
				return;
			}

			x = next_x;
			y = next_y;

		}

	}

	@Override
	// キー入力リスナー
	public void keyPressed(KeyEvent e) {
		
		//↑:38 →:39 ↓:40 ←:37
		if (e.getKeyCode() == 38 || e.getKeyChar() == 'w') {	in.set(0, -1); }
		else if (e.getKeyCode() == 39 || e.getKeyChar() == 'd') { in.set(1, 0); }
		else if (e.getKeyCode() == 40 || e.getKeyChar() == 's') { in.set(0, 1); } 
		else if (e.getKeyCode() == 37 || e.getKeyChar() == 'a') { in.set(-1, 0);}

		this.in.go();

	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

}

// 方向クラス
class Direction {
	int x, y;
	boolean go;

	Direction() {
		this.x = -1;
		this.y = -1;
		this.go = false;
	}

	void set(int X, int Y) {
		this.x = X;
		this.y = Y;
	}

	public void wait_in() {
		this.go = false;
	}

	void go() {
		this.go = true;
	}

	int get_dir() {
		if (this.x == 0 && this.y == -1) { return 0;	} //上
		else if (this.x == 1 && this.y == 0) { return 1;	} //右
		else if (this.x == 0 && this.y == 1) { return 2;	} //下		
		else if (this.x == -1 && this.y == 0) { return 3;} //左
		else
			System.exit(1);

		return -1;
	}
}
