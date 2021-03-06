function MySubmarine(scene) {

    CGFobject.call(this, scene);

    this.angle = Math.PI;

    this.heliceAngle = 0;
    this.heliceRPS = 1;

	this.rudderAngle = 0;
	this.inclination = 0;

	this.periscopeHeight = 0.7;
	this.periscopeMaxHeigth = 1;
	this.periscopeMinHeigth = 0.2;

    this.x = 8;
    this.y = 2;
    this.z = 8;

    this.position = [this.x,this.y,this.z];

    this.vx = 0;
    this.vz = 0;
    this.speed = 0;
    this.maxSpeed = 5;

    this.torpedoActivated;
    this.portugalActivated;

    this.mainCylinder = new MyCylinder(scene,20,20);
    this.frontSemisphere = new MyLamp(scene,20,20);
    this.backSemisphere = new MyLamp(scene,20,20);
    this.topCylinder = new MyCylinder(scene,20,20);
    this.periscope = new MyCylinder(scene,20,20);
    this.topPeriscope = new MyCylinder(scene,20,20);
    this.topCylinderBase = new MyCircle(scene,20);
    this.topPeriscopeBase = new MyCircle(scene,20);
    this.firstPropeller = new MyCylinder(scene,20,20);
    this.secondPropeller = new MyCylinder(scene,20,20);
    this.firstPropellerSemisphere = new MyLamp(scene,20,20);
    this.secondPropellerSemisphere = new MyLamp(scene,20,20);
    this.firstPropellerParallelepiped = new MyUnitCubeQuad(scene);
    this.secondPropellerParallelepiped = new MyUnitCubeQuad(scene);
    this.frontTrapezoid = new MyTrapezoid(scene);
    this.horizontalTrapezoid = new MyTrapezoid(scene);
    this.verticalTrapezoid = new MyTrapezoid(scene);
    this.torpedo = new MyTorpedo(scene);

    this.portugalAppearance=new CGFappearance(this.scene);
	this.portugalAppearance.setAmbient(1, 1, 1, 0.2);
	this.portugalAppearance.setDiffuse(1, 1, 1, 0.2);
	this.portugalAppearance.setSpecular(1, 1, 1, 0.3);
	this.portugalAppearance.setShininess(100);
    this.portugalAppearance.loadTexture("../resources/images/portugal.png");

	this.redAppearance=new CGFappearance(this.scene);
	this.redAppearance.setAmbient(1, 1, 1, 0.2);
	this.redAppearance.setDiffuse(1, 1, 1, 0.2);
	this.redAppearance.setSpecular(1, 1, 1, 0.3);
	this.redAppearance.setShininess(100);
    this.redAppearance.loadTexture("../resources/images/red.png");

    this.greenAppearance=new CGFappearance(this.scene);
	this.greenAppearance.setAmbient(1, 1, 1, 0.2);
	this.greenAppearance.setDiffuse(1, 1, 1, 0.2);
	this.greenAppearance.setSpecular(1, 1, 1, 0.3);
	this.greenAppearance.setShininess(100);
    this.greenAppearance.loadTexture("../resources/images/green.png");
};

MySubmarine.prototype = Object.create(CGFobject.prototype);
MySubmarine.prototype.constructor = MyTriangle;
MySubmarine.prototype.display = function() {
    
	// Display Movement as 1
    this.scene.pushMatrix();
    
    this.scene.translate(this.x, this.y, this.z);
    this.scene.rotate(this.angle, 0, 1, 0)
    this.scene.rotate(-this.inclination,1,0,0);

    // Main Cylinder
    this.scene.pushMatrix();
        this.scene.translate(1,2,1);
        this.scene.rotate(Math.PI,0,1,0);
        this.scene.scale(0.73,1,4.08);
        if(this.portugalActivated)
        	this.portugalAppearance.apply();
		this.mainCylinder.display();
	this.scene.popMatrix();

	// Front Semisphere
    this.scene.pushMatrix();
        this.scene.translate(1,2,1);
        this.scene.scale(0.73,1,1);
        if(this.portugalActivated)
        	this.redAppearance.apply();
        this.frontSemisphere.display();
	this.scene.popMatrix();

	// Top Cylinder
	this.scene.pushMatrix();
        this.scene.translate(1,2.8,0);
        this.scene.rotate(Math.PI,0,1,1);
        this.scene.scale(0.73,0.57,1);
		this.topCylinder.display();
	this.scene.popMatrix();

	// Periscope
	this.scene.pushMatrix();
        this.scene.translate(1,2.8+this.periscopeHeight,0.3);
        this.scene.rotate(Math.PI,0,1,1);
        this.scene.scale(0.1,0.1,1);
		this.periscope.display();
	this.scene.popMatrix();

	// Top Periscope
	this.scene.pushMatrix();
        this.scene.translate(1,3.7+this.periscopeHeight,0.2);
        this.scene.rotate(Math.PI,0,0,1);
        this.scene.scale(0.1,0.1,0.3);
		this.topPeriscope.display();
	this.scene.popMatrix();

	// Top Cylinder Base
	this.scene.pushMatrix();
		this.scene.translate(1,3.8,0);
        this.scene.rotate(Math.PI,0,1,-1);
        this.scene.scale(0.73,0.57,1);
		this.topCylinderBase.display();
	this.scene.popMatrix();

	// Top Periscope Base
	this.scene.pushMatrix();
		this.scene.translate(1,3.8+this.periscopeHeight,0.5);
        this.scene.rotate(Math.PI,0,1,0);
        this.scene.scale(0.1,0.1,0.3);
		this.topPeriscopeBase.display();
	this.scene.popMatrix();

	// Front Trapezoid // Leme Frente
	this.scene.pushMatrix();
		this.scene.translate(1,3.2,0);
		this.scene.rotate(Math.PI/2,0,1,0);
		this.scene.rotate(-this.inclination,0,0,1);
		this.scene.scale(2,2,1.42*2);
		this.frontTrapezoid.display();
	this.scene.popMatrix();
	
	// Back Semisphere
	this.scene.pushMatrix();
        this.scene.translate(1,2,-3.08);
        this.scene.rotate(Math.PI,0,1,0);
        this.scene.scale(0.73,1,1);
        if(this.portugalActivated)
        	this.greenAppearance.apply();
        this.backSemisphere.display();
	this.scene.popMatrix();

	// First Propeller
	this.scene.pushMatrix();
		this.scene.translate(2,1.3,-3.08);
        this.scene.rotate(Math.PI,0,0,1);
        this.scene.scale(0.4,0.4,0.5);
		this.firstPropeller.display();
	this.scene.popMatrix();

	// Second Propeller
	this.scene.pushMatrix();
		this.scene.translate(0,1.3,-3.08);
        this.scene.rotate(Math.PI,0,0,1);
        this.scene.scale(0.4,0.4,0.5);
		this.firstPropeller.display();
	this.scene.popMatrix();

	// First Propeller Semisphere
	this.scene.pushMatrix();
        this.scene.translate(2,1.3,-3);
        this.scene.rotate(Math.PI,0,1,0);
        this.scene.scale(0.1,0.1,0.1);
		this.firstPropellerSemisphere.display();
	this.scene.popMatrix();

	// Second Propeller Semisphere
	this.scene.pushMatrix();
        this.scene.translate(0,1.3,-3);
        this.scene.rotate(Math.PI,0,1,0);
        this.scene.scale(0.1,0.1,0.1);
		this.secondPropellerSemisphere.display();
	this.scene.popMatrix();

	// First Propeller Parallelepiped // Left Helice
	this.scene.pushMatrix();
        this.scene.translate(2,1.3,-2.9);
        this.scene.rotate(this.heliceAngle * Math.PI / 180.0,0,0,1);
        this.scene.scale(0.5,0.1,0.1);
		this.firstPropellerParallelepiped.display();
	this.scene.popMatrix();

	// Second Propeller Parallelepiped // Rigth Helice 
	this.scene.pushMatrix();;
        this.scene.translate(0,1.3,-2.9);
       	this.scene.rotate(-this.heliceAngle * Math.PI / 180.0,0,0,1);
        this.scene.scale(0.5,0.1,0.1);
		this.secondPropellerParallelepiped.display();
	this.scene.popMatrix();

	// Horizontal Trapezoid
	this.scene.pushMatrix();
		this.scene.translate(1,2,-3);
		this.scene.rotate(-Math.PI/2,0,1,0);
		this.scene.rotate(this.inclination,0,0,1);
		this.scene.scale(2,2,2.34*1.75);
		this.horizontalTrapezoid.display();
	this.scene.popMatrix();

	// Vertical Trapezoid
	this.scene.pushMatrix();
		this.scene.translate(1,2,-3);
		this.scene.rotate(this.rudderAngle, 0, 1, 0);
		this.scene.rotate(-Math.PI/2,1,0,0);
		this.scene.rotate(-Math.PI/2,0,0,1);
		this.scene.scale(2,2,2.34*1.75);
		this.verticalTrapezoid.display();
	this.scene.popMatrix();

this.scene.popMatrix();

	// Torpedo
	if(this.torpedoActivated){
		if(this.portugalActivated)
			this.redAppearance.apply();
		this.torpedo.display();
	}

};

MySubmarine.prototype.rotate = function(angle){
    this.angle += angle * this.speed * 0.8 * Math.PI / 180.0;
	

    if(angle < 0){
    	this.rudderAngle += 0.1;
    	if(this.rudderAngle > 0.7)
    		this.rudderAngle = 0.7;
    }
    	
    else{
    	this.rudderAngle -= 0.1;
    	if(this.rudderAngle < -0.7)
    		this.rudderAngle = -0.7;
    	}

}

MySubmarine.prototype.decreaseVelocity = function(){
    if(this.speed < -this.maxSpeed){
    	this.speed = -this.maxSpeed;
    }
    else{
    	this.speed -= 0.5;
    }
}

MySubmarine.prototype.increaseVelocity = function(){

    if(this.speed > this.maxSpeed){
    	this.speed = this.maxSpeed;
    }
    else{
    	this.speed += 0.5;
    }
}


MySubmarine.prototype.setInclination = function(deltaInclination){
    
    this.inclination += deltaInclination;

    if (this.inclination > Math.PI/2){
        this.inclination = Math.PI/2;
    }
    if (this.inclination < -Math.PI/2){
        this.inclination = -Math.PI/2;
    }

}

MySubmarine.prototype.setPeriscopeHeight = function(deltaHeight) {
    this.periscopeHeight += deltaHeight;
    
    if (this.periscopeHeight > this.periscopeMaxHeigth) {
        this.periscopeHeight = this.periscopeMaxHeigth;
    } else if (this.periscopeHeight < this.periscopeMinHeigth) {
        this.periscopeHeight = this.periscopeMinHeigth;
    }
}

MySubmarine.prototype.setSpeed = function(speed){
	this.speed = speed;
}

MySubmarine.prototype.updatePosition = function(t){
    this.time = t / 1000;

	this.speed = (Math.round(this.speed * 2) / 2);
		 
	// Speed Parameters
    this.vx = this.speed * Math.cos(this.angle - Math.PI/2);
    this.vz = this.speed * Math.sin(this.angle + Math.PI/2);
    this.vy = this.speed * Math.sin(this.inclination);

    //Position
    this.x += this.vx * this.time;
    this.z += this.vz * this.time;
    this.y += this.vy * this.time;


    // Helices
	this.heliceRPS = 2 * this.speed;
    this.heliceAngle = this.heliceAngle + t * 0.36 * this.heliceRPS;


	// Vertical Rudder

	if (this.rudderAngle < 0) {
        this.rudderAngle += 0.0001 * t;
    	if (this.rudderAngle > 0)
            this.rudderAngle = 0;
    } else if (this.rudderAngle > 0) {
        this.rudderAngle -= 0.0001 * t;
        if (this.rudderAngle < 0)
            this.rudderAngle = 0;
    }
    // Torpedo
    if(this.torpedoActivated){
    	this.torpedo.updatePosition(t);
    }
}

MySubmarine.prototype.activateTorpedo = function(bool){
    this.torpedoActivated = bool;
	if(this.torpedoActivated){
		this.torpedo.setPos(this.angle,this.inclination,this.x,this.y,this.z);
	}
    	
}

MySubmarine.prototype.lockTarget = function(targetPos){
    this.torpedo.lockTarget(targetPos);
}

MySubmarine.prototype.destroy = function(){
	this.torpedo = new MyTorpedo(this.scene);
}

MySubmarine.prototype.portugal = function(bool){
	this.portugalActivated = bool;
}
