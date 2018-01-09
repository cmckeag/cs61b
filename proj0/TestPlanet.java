class TestPlanet{
	public static void main(String[] args){
		Planet one = new Planet(0,1,0,0,10,"Planet 1");
		Planet two = new Planet(1,0,0,0,20,"Planet 2");
		double forceBetween = 0;
		forceBetween = one.calcForceExertedBy(two);
		System.out.println(forceBetween);
	}
}