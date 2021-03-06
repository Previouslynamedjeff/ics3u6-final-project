/**
 * This class represents a 2-dimensional vector with double precision.
 */
public class Vector {
    public static final Vector VECTOR_ZERO = new Vector(0, 0);

    private double x;
    private double y;

    /**
     * This constructs a new {@code Vector} object at (0, 0).
     */
    public Vector() {
        this.x = 0.0;
        this.y = 0.0;
    }

    /**
     * This constructs a new {@code Vector} object at the specified coordinate.
     * @param x The x-coordinate of the {@code Vector}.
     * @param y The y-coordinate of the {@code Vector}.
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * This method adds the x and y parts of a {@code Vector} to this {@code Vector}. 
     * For example, if the first {@code Vector} is at {@code (a, b)} and the second 
     * {@code Vector} s at {@code (c, d)}, This method will set the first {@code Vector} 
     * to {@code (a+c, b+d)}.
     * @param other The {@code Vector} to add.
     */
    public void add(Vector other) {
        this.x += other.getX();
        this.y += other.getY();
    }

    /**
     * This method adds an x and y value to this {@code Vector}. For example, if
     * this {@code Vector} is at {@code (a, b)}, then this {@code Vector} will become
     * {@code (a+dx, b+dy)}.
     * @param dx
     * @param dy
     */
    public void add(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * This method subtracts the x and y parts of a {@code Vector} to this 
     * {@code Vector}. For example, if the first {@code Vector} is at {@code (a, b)} 
     * and the second {@code Vector} is at {@code (c, d)}, This method will set the 
     * first {@code Vector} to {@code (a-c, b-d)}.
     * @param other The {@code Vector} to subtract.
     */
    public void sub(Vector other) {
        this.x -= other.getX();
        this.y -= other.getY();
    }

    /**
     * This method divides the x and y-coordinates of this {@code Vector} by a scalar.
     * @param scalar The number to divide this {@code Vector} by.
     */
    public void div(double scalar) {
        this.x /= scalar;
        this.y /= scalar;
    }
    /**
     * This method multiplies the x and y-coordinates of this {@code Vector} by a scalar.
     * @param scalar The number to multiply the {@code Vector} by.
     */
    public void mult(double scalar) {
        this.scale(scalar);
    }

    /**
     * This method calculates the length of this {@code Vector}. It will always 
     * return a positive value for all valid {@code Vector}s.
     * @return The length or magnitude of the {@code Vector}.
     */
    public double getMagnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }
    
    /**
     * This method gets the dot product of this and another {@code Vector}.
     * @param other The other {@code Vector}.
     * @return The dot product of the {@code Vector}s, a scalar.
     */
    public double dot(Vector other) {
        return this.x * other.getX() + this.y * other.getY();
    }
    
    /**
     * This method sets the length of this {@code Vector} to 1.
     */
    public void normalize() {
        this.scale(1 / this.getMagnitude());
    }

    /**
     * This method scales the length of this {@code Vector}.
     * @param scalar The length to scale this {@code Vector} by.
     */
    public void scale(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }

    /**
     * This method rotates this {@code Vector} in user coordinates.
     * @param angleDegrees The degree to rotate the vector in user coordinates.
     */
    public void rotate(double angleDegrees) {
        double angleRadians = Math.toRadians(angleDegrees);

        double cosAngle = Math.cos(angleRadians);
        double sinAngle = Math.sin(angleRadians);

        double x2 = cosAngle * this.x - sinAngle * this.y;
        double y2 = sinAngle * this.x + cosAngle * this.y;

        this.x = x2;
        this.y = y2;
    }

    /**
     * This method reflect this {@code Vector} or turns it 180 degrees.
     */
    public void reflect() {
        this.x *= -1;
        this.y *= -1;
    }

    /**
     * THis method reflects this {@code Vector} over a line.
     * @param xLine The line to reflect the vector over.
     */
    public void reflectHorizontally(double xLine) {
        this.x = 2 * xLine - this.x;
    }


    public Vector getVectorX() {
        return new Vector(this.x, 0);
    }

    public Vector getVectorY() {
        return new Vector(0, this.y);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    /**
     * This method sets this {@code Vector} to the specified length.
     * @param length The new length of this {@code Vector}.
     */
    public void setLength(double length) {
        double magnitude = this.getMagnitude();
        if (Double.compare(magnitude, 0.0) != 0) {
            this.scale(length / this.getMagnitude());
        } else {
            this.scale(0);
        }
    }

    /**
     * This method returns a copy of this {@code Vector}.
     * @return The copied {@code Vector}.
     */
    @Override
    public Vector clone() {
        return new Vector(this.getX(), this.getY());
    }

    /**
     * This method determines whether another object is the same as it.
     * @return {@code true} if they are the same, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector) {
            Vector vector = (Vector) obj;
            return Double.compare(this.x, vector.getX()) == 0 && Double.compare(this.y, vector.getY()) == 0;
        }

        return false;
    }
    /**
     * This method returns a string representation of this {@code Vector} in the form, (x, y).
     */
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    /**
     * This method returns the sum of two {@code Vector}s.
     * @param v1 The first {@code Vector}.
     * @param v2 The second {@code Vector}.
     * @return A new {@code Vector} that is a sum of the two {@code Vector}s.
     */
    public static Vector sum(Vector v1, Vector v2) {
        Vector tmp = v1.clone();
        tmp.add(v2);
        return tmp;
    }

    /**
     * This method returns the difference of two {@code Vector}s. If {@code v1 = (a, b)}
     * and {@code v2 = (c, d)}, this method will return {@code (a-c, b-d)}.
     * @param v1 The first {@code Vector}.
     * @param v2 The second {@code Vector}.
     * @return A new {@code Vector} that is the difference of the two {@code Vector}s.
     */
    public static Vector difference(Vector v1, Vector v2) {
        Vector tmp = v1.clone();
        tmp.sub(v2);
        return tmp;
    }

    /**
     * This method calculates the Manhattan distance between two {@code Vector}s.
     * The Manhattan distance is the sum of the differences in the X and Y values.
     * @param pos1 The first coordinate.
     * @param pos2 The second coordinate.
     * @return The Manhattan distance as a {@code double}.
     */
    public static double getManhattanDistanceFrom(Vector pos1, Vector pos2) {
        double deltaX = Math.abs(pos1.getX() - pos2.getX());
        double deltaY = Math.abs(pos1.getY() - pos2.getY());

        double distance = deltaX + deltaY;
        return distance;
    } 

    /**
     * This method calculates the Euclidean distance between two {@code Vector}s.
     * The Euclidean distance is the shortest distance between two points, calculated
     * using the Pythagorean Theorem.
     * @param pos1 The first coordinate.
     * @param pos2 The second coordinate.
     * @return The Euclidean distance as a {@code double}.
     */
    public static double getEuclideanDistanceFrom(Vector pos1, Vector pos2) {
        double distance = Math.sqrt(getSquareEuclideanDistanceFrom(pos1, pos2));
        return distance;
    }

    /**
     * This method calculates whether two {@code Vector}s are within a certain 
     * distance of each other. It uses Euclidean distance, but is faster since it
     * avoids using {@code Math.sqrt}.
     * @param pos1 The first coordinate.
     * @param pos2 The second coordinate.
     * @param checkDistance The distance to compare to.
     * @return A negative integer if the {@code Vector}s are closer than the specified 
     * distance, {@code 0} if they are exactly the distance, and a positive integer if 
     * they are farther than the distance. 
     */
    public static int compareDistance(Vector pos1, Vector pos2, double checkDistance) {
        double squareDistance = getSquareEuclideanDistanceFrom(pos1, pos2);
        double squareCheckDistance = checkDistance * checkDistance;
        return Double.compare(squareDistance, squareCheckDistance);
    }

    /**
     * This method calculates the Euclidean distance squared between two {@code Vector}s.
     * The Euclidean distance is the shortest distance between two points. 
     * @param pos1 The first coordinate.
     * @param pos2 The second coordinate.
     * @return The square of the Euclidean distance as a {@code double}.
     */
    public static double getSquareEuclideanDistanceFrom(Vector pos1, Vector pos2) {
        double deltaX = pos1.getX() - pos2.getX();
        double deltaY = pos1.getY() - pos2.getY();

        double squareDistance = deltaX * deltaX + deltaY * deltaY;
        return squareDistance;
    }

    /**
     * This method generates a vector with random integer x and y values.
     * @param minX The minimum value the x value can be.
     * @param maxX The maximum value the x value can be.
     * @param minY The minimum value the y value can be.
     * @param maxY The maximum value the y value can be.
     * @return A new {@code Vector} object at the randomized coordinate.
     */
    public static Vector getRandomInstance(int minX, int maxX, int minY, int maxY) {
        int rangeX = maxX - minX + 1;
        int rangeY = maxY - minY + 1;
        int x = (int) (rangeX * Math.random() + minX);
        int y = (int) (rangeY * Math.random() + minY);

        return new Vector(x, y);
    }

    /**
     * This method returns a reflected copy of a {@code Vector}.
     * @param vector The {@code Vector} to reflect.
     * @return A reflected copy of the {@code Vector}.
     */
    public static Vector getReflected(Vector vector) {
        Vector reflectedVector = vector.clone();
        reflectedVector.reflect();
        return reflectedVector;
    }
}
