package imageProcessing;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;

public class Frame {

	private int id;
	private Mat mat;
	private List<Circle> circles;
	
	public Frame(int id, Mat mat, List<Circle> circles) {
		super();
		this.id = id;
		this.mat = mat;
		this.circles = circles;
	}

	public Frame(int id, Mat mat) {
		super();
		this.id = id;
		this.mat = mat;
		this.circles = new ArrayList<Circle>();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Mat getMat() {
		return mat;
	}

	public void setMat(Mat mat) {
		this.mat = mat;
	}

	public List<Circle> getCircles() {
		return circles;
	}

	public void setCircles(List<Circle> circles) {
		this.circles = circles;
	}
}
