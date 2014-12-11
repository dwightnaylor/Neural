package neural;

import graphics.fundamentals.WorldFrame;
import graphics.models.neural.FFNNModel;

public class FFNNDisplay extends WorldFrame {

	public FFNNDisplay(final FFNN network, final int width, final int height, final int displayDelay) {
		super(width, height);
		new FFNNModel(getWorld(), network);
		setVisible(true);
	}
}
