public class Planet {
	public double xxPos;
	public double yyPos;
	public double xxVel;
	public double yyVel;
	public double mass;
	public String imgFileName;

	public Planet(double xP, double yP, double xV, double yV, double m, String img){
		xxPos = xP;
		yyPos = yP;
		xxVel = xV;
		yyVel = yV;
		mass = m;
		imgFileName = img;
	}

	public Planet(Planet p){
		this.xxPos = p.xxPos;
		this.yyPos = p.yyPos;
		this.xxVel = p.xxVel;
		this.yyVel = p.yyVel;
		this.mass = p.mass;
		this.imgFileName = p.imgFileName;
	}

	public double calcDistance(Planet p){
		double xDisplacement = this.xxPos - p.xxPos;
		double yDisplacement = this.yyPos - p.yyPos;
		double xSquared = xDisplacement * xDisplacement;
		double ySquared = yDisplacement * yDisplacement;
		double xPlusySum = xSquared + ySquared;
		double distanceSolution = Math.sqrt(xPlusySum);
		return distanceSolution;

	}

	public double calcForceExertedBy(Planet p){
		double gConstant = 6.67e-11;
		double mass1 = this.mass;
		double mass2 = p.mass;
		double displacement = this.calcDistance(p);
		double displacementSquared = displacement*displacement;
		double inverseDistance = 1.0 / displacementSquared;
		double forceSolution = gConstant * mass1 * mass2 * inverseDistance;
		return forceSolution;
	}
	public double calcForceExertedByX(Planet p){
		double xDifference = p.xxPos - this.xxPos;
		double xTotalDisplacement = this.calcDistance(p);
		double xTotalForce = this.calcForceExertedBy(p);
		double xForce = xDifference*xTotalForce/xTotalDisplacement;
		return xForce;
	}
	public double calcForceExertedByY(Planet p){
		double yDifference = p.yyPos - this.yyPos;
		double yTotalDisplacement = this.calcDistance(p);
		double yTotalForce = this.calcForceExertedBy(p);
		double yForce = yDifference*yTotalForce/yTotalDisplacement;
		return yForce;
	}
	public double calcNetForceExertedByX(Planet[] planetArray){
		int arrayLength = planetArray.length;
		int indexx = 0;
		double netxForce = 0;
		while (indexx < arrayLength){
			if (this.equals(planetArray[indexx]) == true){
				indexx = indexx + 1;
			}
			else{
				double changexForce = this.calcForceExertedByX(planetArray[indexx]);
				netxForce = netxForce + changexForce;
				indexx = indexx + 1;
			}
		}
		return netxForce;
	}
	public double calcNetForceExertedByY(Planet[] planetArray){
		int arrayLength = planetArray.length;
		int indexy = 0;
		double netyForce = 0;
		while (indexy < arrayLength){
			if (this.equals(planetArray[indexy]) == true){
				indexy = indexy + 1;
			}
			else{
				double changeyForce = this.calcForceExertedByY(planetArray[indexy]);
				netyForce = netyForce + changeyForce;
				indexy = indexy + 1;
			}
		}
		return netyForce;
	}
	public void update(double timeInterval, double xForceApplied, double yForceApplied){
		double xAccel = xForceApplied/this.mass;
		double yAccel = yForceApplied/this.mass;
		this.xxVel = this.xxVel + (timeInterval * xAccel);
		this.yyVel = this.yyVel + (timeInterval * yAccel);
		this.xxPos = this.xxPos + (timeInterval * this.xxVel);
		this.yyPos = this.yyPos + (timeInterval * this.yyVel);

	}

	public void draw(){
		double xCoord = this.xxPos;
		double yCoord = this.yyPos;
		String fileLocation = this.imgFileName;
		StdDraw.picture(xCoord,yCoord,"./images/"+fileLocation);
	}
}