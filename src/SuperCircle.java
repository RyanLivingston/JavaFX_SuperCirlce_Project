import java.text.DecimalFormat;
import java.util.Random;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class SuperCircle {
	private Random gen = new Random();
	private Circle circle;
	private int radius;
	private double centerX = MainDriver.WINDOW_CENTER_X;
	private double centerY = MainDriver.WINDOW_CENTER_Y;

	@SuppressWarnings("unused")
	private Point center = new Point();
	private circlePoint[] points = new circlePoint[3];
	private Line[] lines = new Line[3];
	private double[] distances = new double[3];
	private double[] angles = new double[3];
	private Group group = new Group();

	public SuperCircle() {
		radius = 50;
		init();
	}

	public SuperCircle(int radius) {
		this.radius = radius;
		init();

	}

	private void init() {
		circle = new Circle(centerX, centerY, radius);
		circle.setFill(Color.TRANSPARENT);
		circle.setStroke(Color.BLACK);
		circle.setStrokeWidth(2);
	}

	public void createPoints() {
		for (int i = 0; i < 3; i++)
			points[i] = genPoint();

		for (int i = 0; i < 3; i++) {

			points[i].getCircle().centerXProperty().addListener(this::pointListener);
			points[i].getCircle().centerYProperty().addListener(this::pointListener);

			lines[i] = new Line();
			lines[i].startXProperty().bind(points[i].getXProperty());
			lines[i].startYProperty().bind(points[i].getYProperty());
			
			lines[i].endXProperty().bind(points[(i + 1) % 3].getXProperty());
			lines[i].endYProperty().bind(points[(i + 1) % 3].getYProperty());
		}
		updateProperties();

	}

	public Group getCircle() {
		group.getChildren().add(circle);
		group.getChildren().addAll(lines);
		for (int i = 0; i < 3; i++) {
			group.getChildren().add(points[i].getAngleText());
			group.getChildren().add(points[i].getCircle());
		}
		return group;
	}

	private void pointListener(ObservableValue<? extends Number> val, Number oldValue, Number newValue) {
		updateProperties();
	}

	private void updateProperties() {
		DecimalFormat df = new DecimalFormat("#.0");

		for (int i = 0; i < 3; i++) {

				distances[i] = Point.getDistance(points[i], points[(i + 1)%3]);
		}

		double a = distances[1];
		double b = distances[2];
		double c = distances[0];

		try {
			angles[0] = (Double
					.parseDouble(df.format(180 * (Math.acos(((a * a) - (b * b) - (c * c)) / (-2 * b * c))) / Math.PI)));
			angles[1] = (Double
					.parseDouble(df.format(180 * (Math.acos(((b * b) - (a * a) - (c * c)) / (-2 * a * c))) / Math.PI)));
			angles[2] = (Double
					.parseDouble(df.format(180 * (Math.acos(((c * c) - (b * b) - (a * a)) / (-2 * a * b))) / Math.PI)));
		} catch (NumberFormatException e) {
			angles[0] = (180 * (Math.acos(((a * a) - (b * b) - (c * c)) / (-2 * b * c))) / Math.PI);
			angles[1] = (180 * (Math.acos(((b * b) - (a * a) - (c * c)) / (-2 * a * c))) / Math.PI);
			angles[2] = (180 * (Math.acos(((c * c) - (b * b) - (a * a)) / (-2 * a * b))) / Math.PI);
		}

		double tmpRadius = radius;

		for (int i = 0; i < 3; i++) {
			Text t = points[i].getAngleText();
			t.setText(String.valueOf(angles[i]));

			boolean shortDist;

			if (distances[(i) % 3] < 65 || distances[(i + 2) % 3] < 65)
				shortDist = true;
			else
				shortDist = false;

			if (angles[i] > 45 && !shortDist) {
				double dy, dx, dr, x1, x2, y1, y2, xf, yf;
				double r = 30;

				dx = points[(i + 1) % 3].x.get() - points[i].x.get();
				dy = points[(i + 1) % 3].y.get() - points[i].y.get();
				dr = Math.hypot(dx, dy);

				x1 = ((dx) * Math.sqrt((r * r) * (dr * dr))) / (dr * dr);
				y1 = ((dy) * Math.sqrt((r * r) * (dr * dr))) / (dr * dr);

				dx = points[(i + 2) % 3].x.get() - points[i].x.get();
				dy = points[(i + 2) % 3].y.get() - points[i].y.get();
				dr = Math.hypot(dx, dy);

				x2 = ((dx) * Math.sqrt((r * r) * (dr * dr))) / (dr * dr);
				y2 = ((dy) * Math.sqrt((r * r) * (dr * dr))) / (dr * dr);

				xf = r * ((x1 + x2) / (Math.hypot((x1 + x2), (y1 + y2))));
				yf = ((y1 + y2) / (x1 + x2)) * (xf);

				t.setX(xf + points[i].getX() + centerX - 12);
				t.setY(-1 * (yf + points[i].getY()) + centerY + 5);

			} else {
				t.setX((points[i].x.get() * ((tmpRadius + 25) / tmpRadius)) + (centerX - 14));
				t.setY(((points[i].y.get() * ((tmpRadius + 15) / tmpRadius)) * -1) + (centerY + 5));
			}
		}
	}

	private circlePoint genPoint() {
		double tmpX = gen.nextInt(radius * 2) - radius;
		double tmpY = circlePoint.genYCoord(tmpX, radius);
		if (tmpX % 2 != 0)
			tmpY = tmpY * -1;
		return new circlePoint(tmpX, tmpY, radius);
	}

	public void updatePos(double newCenterX, double newCenterY) {

		centerX = MainDriver.WINDOW_CENTER_X;
		centerY = MainDriver.WINDOW_CENTER_Y;

		if (MainDriver.WINDOW_SIZE_X < MainDriver.WINDOW_SIZE_Y)
			radius = (int) (MainDriver.WINDOW_SIZE_X * MainDriver.CIRCLE_WINDOW_RATIO / 2);
		else
			radius = (int) (MainDriver.WINDOW_SIZE_Y * MainDriver.CIRCLE_WINDOW_RATIO / 2);

		circle.setCenterX(centerX);
		circle.setCenterY(centerY);
		circle.setRadius(radius);

		if (radius < 50)
			circlePoint.setRadius(4);
		else
			circlePoint.setRadius(6);

		for (circlePoint p : points)
			p.updatePos(radius);
	}

	public void showDebug() {
		int y = 15;
		StringProperty comma = new SimpleStringProperty(", ");
		StringProperty radius = new SimpleStringProperty("Radius: ");

		Text radiusLabel = new Text(10, y, "");
		radiusLabel.textProperty().bind(radius.concat(circle.radiusProperty()));

		group.getChildren().add(radiusLabel);

		for (int i = 0; i < 3; i++) {
			y += 15;
			StringProperty pText = new SimpleStringProperty("P" + i + ": ");
			Text p = new Text(10, y, "");
			p.textProperty().bind(pText.concat(points[i].x).concat(comma).concat(points[i].y));

			group.getChildren().add(p);
		}
		Text note = new Text(10, y += 15, "(DISABLE IN MAINDRIVER)");
		group.getChildren().add(note);

	}

}
