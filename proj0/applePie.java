class applePie{
	public static void main(String[] args){
		In in2 = new In("./data/planets.txt");
		int numberOfPlanets = in2.readInt();
		double universeRadius = in2.readDouble();
		int arrayIndex = 0;
		Planet[] planetArray = new Planet[numberOfPlanets];
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
		System.out.println(planetArray[2].xxPos);
	}
}