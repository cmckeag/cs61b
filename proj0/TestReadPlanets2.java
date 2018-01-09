public class TestReadPlanets2{

	public static void main(String[] args){
	Planet[] actualOutput = NBody.readPlanets("./data/planets.txt");
	System.out.println(actualOutput[2].xxPos);
}

}