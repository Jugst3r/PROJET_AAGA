package algorithms;
import java.awt.Point;

public class ColoredPoint extends Point{
	private Couleur color;

	public ColoredPoint(int x, int y, Couleur color) {
		super(x, y);
		this.color = color;
	}

	public Couleur getColor() {
		return color;
	}
	
	public void setColor(Couleur c) {
		color = c;
	}
}
