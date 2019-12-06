package projet_algav;
import java.awt.Point;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

public class MyPanel extends JPanel {
	private ArrayList<Point> points ;
	private ArrayList<Line> lines ;
	public MyPanel(ArrayList<Point> points,ArrayList<Line> lines) {
		this.points = points;
		this.lines = lines;
	}
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for(Point p: points) {
        	g.setColor(Color.RED);
        	g.drawOval((int)p.getX(), (int)p.getY(), 2, 2);
        	
        }
        g.setColor(Color.BLUE);
        for(Line l : lines) {
        	g.drawLine((int)l.getX1(), (int)l.getY1(), (int)l.getX2(), (int)l.getY2());
        }
    }

    //so our panel is the corerct size when pack() is called on Jframe
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }
}
