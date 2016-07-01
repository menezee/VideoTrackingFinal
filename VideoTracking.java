package imageProcessing;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

public class VideoTracking {
	static JFrame jFrameWindow;
	static JLabel img1, img2;
	static List<HashMap<String, Position>> circles = new ArrayList<HashMap<String, Position>>();
	static HashMap<String, Position> visitedWhitePixels = new HashMap<String, Position>();
	static List<Circle> circlesDiscovered = new ArrayList<Circle>();
	static List<Frame> frames = new ArrayList<Frame>();
	static int ALGORITHM = 2; 
	static Circle THE_CIRCLE;
	static Circle THE_CIRCLE2;
	static int vectorToFind;
	
	public static void initUIComponents() {
		jFrameWindow = new JFrame();
		jFrameWindow.setAlwaysOnTop(true);
		jFrameWindow.setTitle("Media");
		jFrameWindow.setBounds(600, 250, 680, 540);
		jFrameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrameWindow.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

		JPanel original = new JPanel();
		original.setBorder(new LineBorder(new Color(0, 0, 0)));
		jFrameWindow.getContentPane().add(original);

		img1 = new JLabel("");
		img1.setIcon(new ImageIcon("./images/parrot.jpg"));
		original.add(img1);

		JPanel edited = new JPanel();
		edited.setBorder(new LineBorder(new Color(0, 0, 0)));
		// jFrameWindow.getContentPane().add(edited);

		img2 = new JLabel("");
		img2.setIcon(new ImageIcon("./images/parrot.jpg"));
		edited.add(img2);

		jFrameWindow.repaint();
		jFrameWindow.validate();
		jFrameWindow.setVisible(true);
	}

	public static void updateImage(Mat m, JLabel label, String frame) {
		Highgui.imwrite("./images/frame_" + frame + ".jpg", m);
		ImageIcon img = new ImageIcon("./images/frame_" + frame + ".jpg");

		label.setIcon(img);
		jFrameWindow.repaint();
		jFrameWindow.validate();
		jFrameWindow.setVisible(true);
	}

	public static void main(String[] args) throws InterruptedException {

		//----------------- INIT DEFAULT -------------------
		
		initUIComponents();

		img2.setIcon(new ImageIcon("./images/cat.jpg"));

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		VideoCapture videoCapture = new VideoCapture("./video/VideoBike.avi");
		videoCapture.open("./video/VideoBike.avi");
		Mat mat = new Mat();

		if (!videoCapture.isOpened()) {
			System.out.println("media failed to open");
		}

		//----------------- INIT DEFAULT -------------------
		
		else {
			int frameCounter = 0;
			
			//para cada frame
			while (videoCapture.grab()) {
				
				//inicio algoritmo 1
				if(frameCounter > 0 && ALGORITHM == 1){
					if(frames.size() > 0){
						
						//limpar registros execucao anterior
						clear();
						
						videoCapture.retrieve(mat);
						
						Frame frameDetails = new Frame(frameCounter, mat);
						frames.add(frameDetails);
						
						jFrameWindow.setTitle("frame:" + frameCounter);
						
//						updateImage(mat, img1, Integer.toString(frameCounter));
						
						findCircles(mat);
						
						int counter = 1;
						
						//----------------- INIT OF PIXELS IN CIRCLE LOOP -----------------
						for (HashMap<String, Position> hm : circles) {
							Iterator it = hm.entrySet().iterator();
							
							Position mostLeft = null;
							Position mostRight = null;
							Position mostTop = null;
							Position mostBottom = null;
							
							//para cada pixel dentro do circulo
							while (it.hasNext()) {
								Map.Entry pair = (Map.Entry) it.next();
								Position p = (Position) pair.getValue();
								
								//circle edges
								if(mostLeft == null || p.x < mostLeft.x) mostLeft = p;
								if(mostRight == null || p.x > mostRight.x) mostRight = p;
								if(mostTop == null || p.y < mostTop.y) mostTop = p;
								if(mostBottom == null || p.y > mostBottom.y) mostBottom= p;
								
//								System.out.println(pair.getKey() + " = " + pair.getValue());
							}

							int radius = Math.abs(mostLeft.x - mostRight.x) /2;
							int finalX = mostLeft.x + (Math.abs(mostLeft.x - mostRight.x) /2);
							int finalY = mostTop.y + (Math.abs(mostTop.y - mostBottom.y) /2);
							
							circlesDiscovered.add(new Circle(hm, counter, new Position(finalX, finalY)));
							
							//Core.circle(mat, new Point(finalX, finalY), radius, new Scalar(255, 0, 0), -1);
							//updateImage(mat, img1, frameCounter + "_altered_"+ Math.random() * 256);
							counter++;
						}
						//----------------- INIT OF PIXELS IN CIRCLE LOOP -----------------
						
						frameDetails.setCircles(circlesDiscovered);
						
						//find circle1
						Circle found = null;
						Circle found2 = null;
						
						for (Circle c : circlesDiscovered) {
							if (found == null || (Math.abs(c.getPosition().x - THE_CIRCLE.getPosition().x)
									+ Math.abs(c.getPosition().y - THE_CIRCLE.getPosition().y) < (Math.abs(
											found.getPosition().x - THE_CIRCLE.getPosition().x) + Math
													.abs(found.getPosition().y - THE_CIRCLE.getPosition().y)))) {
								found = c;
							}
						}
						
						for(Circle c: circlesDiscovered){
							if(found2==null || (Math.abs(c.getPosition().x - THE_CIRCLE2.getPosition().x) + Math.abs(c.getPosition().y - THE_CIRCLE2.getPosition().y) < 
									(Math.abs(found2.getPosition().x - THE_CIRCLE2.getPosition().x) + Math.abs(found2.getPosition().y - THE_CIRCLE2.getPosition().y)))){
								found2 = c;
							}
						}
						
						THE_CIRCLE2 = found2;
						THE_CIRCLE = found;
						
						Core.circle(mat, new Point(found2.getPosition().y, found2.getPosition().x), 5, new Scalar(0, 0, 255), -1);
						Core.circle(mat, new Point(found.getPosition().y, found.getPosition().x), 5, new Scalar(0, 255, 0), -1);
						
						updateImage(mat, img1, frameCounter + "_altered_"+ Math.random() * 256);
						//find circle1
					}
				}
				//fim algoritmo 1
				
				//inicio algoritmo 2
				else if(frameCounter > 0 && ALGORITHM == 2){
					clear();
					
					//FIND MATRIX IN OLD FRAME
					int[][] matrix = findMatrixAroundCircle(THE_CIRCLE, mat);
					printMatrix(matrix);
					
					System.out.println();
					
					videoCapture.retrieve(mat);
					Frame frameDetails = new Frame(frameCounter, mat);
					frames.add(frameDetails);
					jFrameWindow.setTitle("frame:" + frameCounter);

					Circle circleSamePosition = new Circle(
							new Position(THE_CIRCLE.getPosition().y, THE_CIRCLE.getPosition().x));
					int[][] matrixSamePlaceFramePlusOne = findMatrixAroundCircle(circleSamePosition, mat);
					System.out.println("matrixSamePlaceFramePlusOne");
					printMatrix(matrixSamePlaceFramePlusOne);
					System.out.println();
					
					Circle circleUp = new Circle(
							new Position(THE_CIRCLE.getPosition().y - 5, THE_CIRCLE.getPosition().x));
					int[][] matrixUpFramePlusOne = findMatrixAroundCircle(circleUp, mat);
					System.out.println("matrixUpFramePlusOne");
					printMatrix(matrixUpFramePlusOne);
					System.out.println();
					
					Circle circleRight = new Circle(
							new Position(THE_CIRCLE.getPosition().y, THE_CIRCLE.getPosition().x + 5));
					int[][] matrixRightFramePlusOne = findMatrixAroundCircle(circleRight, mat);
					System.out.println("matrixRightFramePlusOne");
					printMatrix(matrixRightFramePlusOne);
					System.out.println();
					
					Circle circleDown = new Circle(
							new Position(THE_CIRCLE.getPosition().y + 5, THE_CIRCLE.getPosition().x));
					int[][] matrixDownFramePlusOne = findMatrixAroundCircle(circleDown, mat);
					System.out.println("matrixDownFramePlusOne");
					printMatrix(matrixDownFramePlusOne);
					System.out.println();
					
					Circle circleLeft = new Circle(
							new Position(THE_CIRCLE.getPosition().y, THE_CIRCLE.getPosition().x - 5));
					int[][] matrixLeftFramePlusOne = findMatrixAroundCircle(circleLeft, mat);
					System.out.println("matrixLeftFramePlusOne");
					printMatrix(matrixLeftFramePlusOne);
					System.out.println();
					
					System.out.println("Matriz matrixSamePlaceFramePlusOne tem "
							+ calculateSumOfAbsoluteDifferencesBetween(matrix, matrixSamePlaceFramePlusOne)
							+ " pontos iguais.");
					
					System.out.println("Matriz matrixUpFramePlusOne tem "
							+ calculateSumOfAbsoluteDifferencesBetween(matrix, matrixUpFramePlusOne)
							+ " pontos iguais.");
					
					System.out.println("Matriz matrixRightFramePlusOne tem "
							+ calculateSumOfAbsoluteDifferencesBetween(matrix, matrixRightFramePlusOne)
							+ " pontos iguais.");
					
					System.out.println("Matriz matrixDownFramePlusOne tem "
							+ calculateSumOfAbsoluteDifferencesBetween(matrix, matrixDownFramePlusOne)
							+ " pontos iguais.");
					
					System.out.println("Matriz matrixLeftFramePlusOne tem "
							+ calculateSumOfAbsoluteDifferencesBetween(matrix, matrixLeftFramePlusOne)
							+ " pontos iguais.");
				
					System.out.println();
				}
				//fim algoritmo 2

				//inicio algoritmo 3
				else if(frameCounter > 0 && ALGORITHM == 3){
					clear();
					
					if(frameCounter == 1){
						Core.circle(mat, new Point(THE_CIRCLE.getPosition().y, THE_CIRCLE.getPosition().x), 6, new Scalar(0, 0, 255), -1);
						Core.circle(mat, new Point(THE_CIRCLE2.getPosition().y, THE_CIRCLE2.getPosition().x), 6, new Scalar(0, 0, 255), -1);
						
						Core.line(mat, new Point(THE_CIRCLE.getPosition().y, THE_CIRCLE.getPosition().x), 
								   new Point(THE_CIRCLE2.getPosition().y, THE_CIRCLE2.getPosition().x), 
								   new Scalar(0, 0, 255));
						
						updateImage(mat, img1, "algoritmo3");
						
						System.out.println("Ponto 15: [" + THE_CIRCLE.getPosition().y + ", " + THE_CIRCLE.getPosition().x + "]" + 
								" se conecta no ponto 16: [" + THE_CIRCLE2.getPosition().y + ", " + THE_CIRCLE2.getPosition().x + "]");
						System.out.println("Distancia do vetor: " + ((Math.abs(THE_CIRCLE.getPosition().y - THE_CIRCLE2.getPosition().y)) + 
								(Math.abs(THE_CIRCLE.getPosition().x - THE_CIRCLE2.getPosition().x))));
						System.out.println();
					}
					
					videoCapture.retrieve(mat);
					Frame frameDetails = new Frame(frameCounter, mat);
					frames.add(frameDetails);
					jFrameWindow.setTitle("frame:" + frameCounter);
					findCircles(mat);
					
					int counter = 1;
					
					//----------------- INIT OF PIXELS IN CIRCLE LOOP -----------------
					for (HashMap<String, Position> hm : circles) {
						Iterator it = hm.entrySet().iterator();
						
						Position mostLeft = null;
						Position mostRight = null;
						Position mostTop = null;
						Position mostBottom = null;
						
						//para cada pixel dentro do circulo
						while (it.hasNext()) {
							Map.Entry pair = (Map.Entry) it.next();
							Position p = (Position) pair.getValue();
							
							//circle edges
							if(mostLeft == null || p.x < mostLeft.x) mostLeft = p;
							if(mostRight == null || p.x > mostRight.x) mostRight = p;
							if(mostTop == null || p.y < mostTop.y) mostTop = p;
							if(mostBottom == null || p.y > mostBottom.y) mostBottom= p;
							
//							System.out.println(pair.getKey() + " = " + pair.getValue());
						}

						int radius = Math.abs(mostLeft.x - mostRight.x) /2;
						int finalX = mostLeft.x + (Math.abs(mostLeft.x - mostRight.x) /2);
						int finalY = mostTop.y + (Math.abs(mostTop.y - mostBottom.y) /2);
						
						circlesDiscovered.add(new Circle(hm, counter, new Position(finalX, finalY)));
						
						//Core.circle(mat, new Point(finalX, finalY), radius, new Scalar(255, 0, 0), -1);
						//updateImage(mat, img1, frameCounter + "_altered_"+ Math.random() * 256);
						counter++;
					}
					
					Circle Circle1Found = null;
					Circle Circle2Found = null;
					int sizeOfVectorToFind = 99999;
					
					for(Circle c: circlesDiscovered){
						for(Circle c2: circlesDiscovered){
							if(!(c.getPosition().x == c2.getPosition().x && c.getPosition().y == c2.getPosition().y)){
								
								int euclideanDistance = (Math.abs(c.getPosition().x) - Math.abs(c2.getPosition().x)) + ((Math.abs(c.getPosition().y) - Math.abs(c2.getPosition().y)));
								
								if(Math.abs(euclideanDistance - vectorToFind) < sizeOfVectorToFind){
									sizeOfVectorToFind = euclideanDistance;
									Circle1Found = c;
									Circle2Found = c2;
								}
							}
						}
					}
					
					
					
					
					Core.circle(mat, new Point(Circle1Found.getPosition().y, Circle1Found.getPosition().x), 6, new Scalar(0, 0, 255), -1);
					Core.circle(mat, new Point(Circle2Found.getPosition().y, Circle2Found.getPosition().x), 6, new Scalar(0, 0, 255), -1);
					
					Core.line(mat, new Point(Circle1Found.getPosition().y, Circle1Found.getPosition().x), 
							   new Point(Circle2Found.getPosition().y, Circle2Found.getPosition().x), 
							   new Scalar(0, 0, 255));
					
					updateImage(mat, img1, "algoritmo3" + Math.random());
					
					System.out.println("Ponto 15: [" + Circle1Found.getPosition().y + ", " + Circle1Found.getPosition().x + "]" + 
							" se conecta no ponto 16: [" + Circle2Found.getPosition().y + ", " + Circle2Found.getPosition().x + "]");
					System.out.println("Distancia do vetor: " + sizeOfVectorToFind);
					System.out.println();
					
					vectorToFind = sizeOfVectorToFind;
					
					
					
					//TODO
					//para cada aresta, identificar possivel vertice
				}
				//fim algoritmo 3
				
				//1a execucao para encontrar marcadores
				else{
					videoCapture.retrieve(mat);
					
					Frame frameDetails = new Frame(frameCounter, mat);
					frames.add(frameDetails);
					
					//seta titulo com o num do frame
					jFrameWindow.setTitle("frame:" + frameCounter);
					
					//salva a imagem com o numero do frame	
					//seta na UI a imagem com o mat
					updateImage(mat, img1, Integer.toString(frameCounter));

					//populates circles (List<HashMap<String, Position>>)
					findCircles(mat);
					
					int counter = 1;
					
					//----------------- INIT OF PIXELS IN CIRCLE LOOP -----------------
					for (HashMap<String, Position> hm : circles) {
						Iterator it = hm.entrySet().iterator();
						
						Position mostLeft = null;
						Position mostRight = null;
						Position mostTop = null;
						Position mostBottom = null;
						
						//para cada pixel dentro do circulo
						while (it.hasNext()) {
							Map.Entry pair = (Map.Entry) it.next();
							Position p = (Position) pair.getValue();
							
							//circle edges
							if(mostLeft == null || p.x < mostLeft.x) mostLeft = p;
							if(mostRight == null || p.x > mostRight.x) mostRight = p;
							if(mostTop == null || p.y < mostTop.y) mostTop = p;
							if(mostBottom == null || p.y > mostBottom.y) mostBottom= p;
							
//							System.out.println(pair.getKey() + " = " + pair.getValue());
						}

						//encontra o raio e centro do marcador
						int radius = Math.abs(mostLeft.x - mostRight.x) /2;
						int finalX = mostLeft.x + (Math.abs(mostLeft.x - mostRight.x) /2);
						int finalY = mostTop.y + (Math.abs(mostTop.y - mostBottom.y) /2);
						
						circlesDiscovered.add(new Circle(hm, counter, new Position(finalX, finalY)));
						
						//HARDCODED CIRCLES
						if(counter == 14 && ALGORITHM == 1){
							THE_CIRCLE2 = circlesDiscovered.get(circlesDiscovered.size()-1);
							Core.circle(mat, new Point(finalX, finalY), radius, new Scalar(0, 0, 255), -1);
						}
						
						else if(counter == 16 && ALGORITHM == 1){
							THE_CIRCLE = circlesDiscovered.get(circlesDiscovered.size()-1);
							Core.circle(mat, new Point(finalX, finalY), radius, new Scalar(0, 255, 0), -1);
						}
						
						else if(counter == 7 && ALGORITHM == 2){
							THE_CIRCLE = circlesDiscovered.get(circlesDiscovered.size()-1);
							Core.circle(mat, new Point(finalX, finalY), radius, new Scalar(0, 255, 0), -1);
						}
						
						else if(counter == 15 && ALGORITHM == 3){
							THE_CIRCLE = circlesDiscovered.get(circlesDiscovered.size()-1);
							Core.circle(mat, new Point(finalX, finalY), radius, new Scalar(255, 255, 0), -1);
						}
						
						else if(counter == 16 && ALGORITHM == 3){
							THE_CIRCLE2 = circlesDiscovered.get(circlesDiscovered.size()-1);
							Core.circle(mat, new Point(finalX, finalY), radius, new Scalar(255, 255, 0), -1);
						}
						
						else if(counter != 16 && ALGORITHM == 3){
							Core.circle(mat, new Point(finalX, finalY), radius, new Scalar(255, 255, 255), -1);
						}
						
						else{
							Core.circle(mat, new Point(finalX, finalY), radius, new Scalar(255, 0, 0), -1);
						}
						updateImage(mat, img1, frameCounter + "_altered_"+ Math.random() * 256);
						counter++;
					}
					//----------------- INIT OF PIXELS IN CIRCLE LOOP -----------------
					
					//seta marcadores no frame atual
					frameDetails.setCircles(circlesDiscovered);
					
					System.out.println("Circles found in first loop: " + circlesDiscovered.size());
					for(Circle c: circlesDiscovered){
						System.out.println("Circle: " + c.getId() + " - Size: " + c.getPixels().entrySet().size());
					}
					
					
					//inicio monta esqueleto
					/*findConnectionsInCirclesFound();
					
					//draw line
					for(Circle c: frameDetails.getCircles()){
						if(c.getConnectsTo()!=null){
							Core.line(mat, new Point(c.getPosition().y, c.getPosition().x), 
										   new Point(c.getConnectsTo().getPosition().y, c.getConnectsTo().getPosition().x), 
										   new Scalar(255, 255, 255));
						}
					}
					
					updateImage(mat, img1, frameCounter + "_altered_with_lines");*/
					//fim monta esqueleto
				}
					Thread.sleep(500);

					frameCounter++;
			}
			videoCapture.release();
		}
	}
	
	public static void floodFillCircle(Position seed, Mat m, HashMap<String, Position> positions) {
		positions.put(new Position(seed.y, seed.x).getCode(), new Position(seed.y, seed.x));
		visitedWhitePixels.put(new Position(seed.y, seed.x).getCode(), new Position(seed.y, seed.x));

		// UP
		if (m.get(seed.y - 1, seed.x) != null && getIntensity(m.get(seed.y - 1, seed.x)) > 100/*m.get(seed.y - 1, seed.x)[0] > 100*/
				&& positions.get(new Position(seed.y - 1, seed.x).getCode()) == null) {

			floodFillCircle(new Position(seed.y - 1, seed.x), m, positions);
		}

		// DOWN
		if (m.get(seed.y + 1, seed.x) != null && getIntensity(m.get(seed.y + 1, seed.x)) > 100/*m.get(seed.y + 1, seed.x)[0] > 100*/
				&& positions.get(new Position(seed.y + 1, seed.x).getCode()) == null) {

			floodFillCircle(new Position(seed.y + 1, seed.x), m, positions);
		}

		// RIGHT
		if (m.get(seed.y, seed.x + 1) != null && getIntensity(m.get(seed.y, seed.x + 1)) > 100/*m.get(seed.y, seed.x + 1)[0] > 100*/
				&& positions.get(new Position(seed.y, seed.x + 1).getCode()) == null) {

			floodFillCircle(new Position(seed.y, seed.x + 1), m, positions);
		}

		// LEFT
		if (m.get(seed.y, seed.x - 1) != null && getIntensity(m.get(seed.y, seed.x - 1)) > 100/*m.get(seed.y, seed.x - 1)[0] > 100*/	
				&& positions.get(new Position(seed.y, seed.x - 1).getCode()) == null) {

			floodFillCircle(new Position(seed.y, seed.x - 1), m, positions);
		}
	}

	public static void findCircles(Mat mat){
		//para cada pixel
		for (int i = 0; i < mat.rows(); i++) {
			for (int j = 0; j < mat.cols(); j++) {

				//se encontrar um pixel com cor acima de 250
				//e o pixel nao fizer parte de outro circulo
				if (getIntensity(mat.get(i, j)) > 250/*mat.get(i, j)[0] > 250*/ && visitedWhitePixels.get(new Position(i, j).getCode()) == null) {
					
					//cria um novo circulo
					HashMap<String, Position> newCircle = new HashMap<String, Position>();
					
					//adiciona na lista de circulos encontrados
					circles.add(newCircle);
					
					//floodfill a partir de i, j
					floodFillCircle(new Position(i, j), mat, newCircle);

					// se o floodfill encontrar menos de 13 pixels como parte do circulo, considera ruido
					if (newCircle.entrySet().size() < 13) {
						circles.remove(circles.size() - 1);
					}
				}
			}
		}
	}

	public static void clear(){
		circles = new ArrayList<HashMap<String, Position>>();
		visitedWhitePixels = new HashMap<String, Position>();
		circlesDiscovered = new ArrayList<Circle>();
	}
	
	public static void findConnectionsInCirclesFound() {
		Iterator it2 = circlesDiscovered.iterator();
		while (it2.hasNext()) {

			int minDist = 999;

			Circle circ = (Circle) it2.next();
			if (circ.getConnectsTo() != null)
				return;
			for (Circle c : circlesDiscovered) {
				if (c.getConnectsTo() == null && circ.getId() != c.getId()
						&& ((Math.abs(circ.getPosition().x - c.getPosition().x)
								+ (Math.abs(circ.getPosition().y - c.getPosition().y)))) < minDist) {
					circ.setConnectsTo(c);
					circ.setConnectionDistance((Math.abs(circ.getPosition().x - c.getPosition().x)
							+ (Math.abs(circ.getPosition().y - c.getPosition().y))));
					minDist = (Math.abs(circ.getPosition().x - c.getPosition().x)
							+ (Math.abs(circ.getPosition().y - c.getPosition().y)));
				}
			}

			if (circ.getConnectsTo() != null) {
				System.out.println("Circle " + circ.getId() + " connects to " + circ.getConnectsTo().getId()
						+ " with min distance " + minDist);
			}
		}
	}
	
	public static int getIntensity(double[] BGR){
		return (int) (BGR[1] * 0.587 + BGR[2] * 0.299 + BGR[0] * 0.114);
	}

	public static int[][] findMatrixAroundCircle(Circle c, Mat m){
		
		int[][] matrix = new int[15][15];
		
		for (int i = c.getPosition().y - 7, i_counter = 0; i < c.getPosition().y + 8; i++, i_counter++) {
			for (int j = c.getPosition().x - 7, j_counter = 0; j < c.getPosition().x + 8; j++, j_counter++) {
				matrix[i_counter][j_counter] = getIntensity(m.get(j, i)) > 100 ? 1 : 0;
			}
		}
		
		return matrix;
		
	}
	
	public static void printMatrix(int[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	public static int calculateSumOfAbsoluteDifferencesBetween(int[][] m1, int[][] m2) {
		int counter = 0;
		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m1[0].length; j++) {
				if (m1[i][j] != m2[i][j]) {
					counter++;
				}
			}
		}
		return counter;
	}
}