public class NBody{

	public static double readRadius(String planetFileLocation){
		In in1 = new In(planetFileLocation);
		in1.readInt();
		double radiusOfUniverse = in1.readDouble();
		return radiusOfUniverse;
	}

	public static Planet[] readPlanets(String planetFileLocation){
		In in2 = new In(planetFileLocation);
		int numberOfPlanets = in2.readInt();
		in2.readDouble();
		Planet[] planetArray = new Planet[numberOfPlanets];
		int arrayIndex = 0;
		while (arrayIndex < numberOfPlanets){
			double first = in2.readDouble();
			double second = in2.readDouble();
			double third = in2.readDouble();
			double fourth = in2.readDouble();
			double fifth = in2.readDouble();
			String sixth = in2.readString();
			planetArray[arrayIndex] = new Planet(first,second,third,fourth,fifth,sixth);
			arrayIndex = arrayIndex + 1;
		}
		return planetArray;
	}

	public static void main(String[] args){
		double totalDuration = Double.parseDouble(args[0]);
		double intervalDuration = Double.parseDouble(args[1]);
		String planetFileLocation = args[2];
		double scale = readRadius(planetFileLocation);
		StdDraw.setScale(-1*scale,scale);
		StdDraw.clear();
		StdDraw.picture(0,0,"./images/starfield.jpg");
		Planet[] array = readPlanets(planetFileLocation);
		int totalPlanets = array.length;
		int index0 = 0;
		while (index0 < totalPlanets){
			array[index0].draw();
			index0 = index0 + 1;
		}
		double currentTime = 0;
		while (currentTime <= totalDuration){
			int forcesIndex = 0;
			double[] xForces = new double[totalPlanets];
			double[] yForces = new double[totalPlanets];
			// Update the forces
			while (forcesIndex < totalPlanets){
				xForces[forcesIndex] = array[forcesIndex].calcNetForceExertedByX(array);
				yForces[forcesIndex] = array[forcesIndex].calcNetForceExertedByY(array);
				forcesIndex = forcesIndex + 1;
			}
			// Update the positions and velocity
			int updateIndex = 0;
			while (updateIndex < totalPlanets){
				array[updateIndex].update(intervalDuration, xForces[updateIndex], yForces[updateIndex]);
				updateIndex = updateIndex + 1;
			}
			// Update the drawings
			StdDraw.picture(0,0,"./images/starfield.jpg");
			index0 = 0;
			while (index0 < totalPlanets){
				array[index0].draw();
				index0 = index0 + 1;
			}
			StdDraw.show(10);

			currentTime = currentTime + intervalDuration;
		}
		//Print out the results
		StdOut.printf("%d\n", array.length);
		StdOut.printf("%.2e\n", scale);
		for (int i = 0; i < array.length; i++) {
			StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
   				array[i].xxPos, array[i].yyPos, array[i].xxVel, array[i].yyVel, array[i].mass, array[i].imgFileName);
		}
	}

}