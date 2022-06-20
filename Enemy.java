import java.awt.Graphics;

import java.util.ArrayList;

public class Enemy extends Entity implements Moveable, Collidable {
    private static final int WALK_SPEED = 3;
    private static int numEnemies = 0;

    private ArrayList<AnimationCycle> cycles;
    private AnimationCycle activeCycle;
    private AnimationCycle idleCycle;
    private AnimationCycle attackCycle;
    private AnimationCycle hurtCycle;
    private AnimationCycle walkCycle;
    private AnimationCycle deathCycle;

    private int direction;
    private Vector speed;
    private Vector targetPos;
    private Player player;
    private Sword sword;
    private HealthBar healthBar;

    public Enemy(Vector position, Player player, int maxHealthPoints, int swordDamagePoints) {
        super(position, "Unnamed Enemy " + numEnemies);
        numEnemies++;

        this.idleCycle = new AnimationCycle(this.getPos(), Const.ENEMY_IDLE_SPRITE_SHEET, Const.ENEMY_IDLE_FILE_NAME);
        this.walkCycle = new AnimationCycle(this.getPos(), Const.ENEMY_WALK_SPRITE_SHEET, Const.ENEMY_WALK_FILE_NAME);
        this.attackCycle = new AnimationCycle(this.getPos(), Const.ENEMY_ATTACK_SPRITE_SHEET, Const.ENEMY_ATTACK_FILE_NAME);
        this.hurtCycle = new AnimationCycle(this.getPos(), Const.ENEMY_HURT_SPRITE_SHEET, Const.ENEMY_HURT_FILE_NAME);
        this.deathCycle = new AnimationCycle(this.getPos(), Const.ENEMY_DEATH_SPRITE_SHEET, Const.ENEMY_DEATH_FILE_NAME);
        
        this.cycles = new ArrayList<AnimationCycle>();
        this.cycles.add(this.idleCycle);
        this.cycles.add(this.walkCycle);
        this.cycles.add(this.attackCycle);
        this.cycles.add(this.hurtCycle);
        this.cycles.add(this.deathCycle);
        
        this.activeCycle = this.idleCycle;

        this.direction = Const.LEFT;
        this.speed = Vector.VECTOR_ZERO.clone();
        this.targetPos = position.clone();
        this.player = player;

        this.sword = new Sword(position, swordDamagePoints);
        this.healthBar = new HealthBar(Vector.sum(this.getCenter(), new Vector(-this.getWidth() / 2 + 20, -53)), 
                maxHealthPoints, this.getWidth() - 40, 7);
    }

    @Override
    public void draw(Graphics graphics) {
        this.activeCycle.draw(graphics);
        this.healthBar.draw(graphics);
        this.sword.draw(graphics);
    }

    @Override
    public void drawDebugInfo(Graphics graphics) {
        this.activeCycle.drawDebugInfo(graphics);
        this.sword.drawDebugInfo(graphics);
        
        // Draw the coordinates of the enemy.
        String info = this.getName() + "(" + (Math.round(this.getCenterX() * 10) / 10.0) + 
                ", " + (Math.round(this.getY() * 10) / 10.0) + ")";
        Text text = new Text(info, Const.DEBUG_FONT, (int) this.getCenterX(), (int) this.getY());
        text.draw(graphics);

        // Draw the enemy target.
        graphics.setColor(Const.GRAY);
        graphics.fillOval((int) this.targetPos.getX() - 3, (int) this.targetPos.getY() - 3, 6, 6);

    }

    public void update() {
        // Update the speed.
        this.speed = Vector.difference(this.targetPos, this.getCenter());
        this.speed.setLength(Math.min(WALK_SPEED, 
                (int) (Vector.getEuclideanDistanceFrom(this.getPos(), this.targetPos))));

        if (this.intersects(this.player.getGeneralHitbox())) {
            this.speed = Vector.VECTOR_ZERO.clone();
        }

        // Update the position.
        Vector newPos = this.getPos();
        newPos.add(this.speed);
        this.setPos(newPos);

        // Attack or set a new target position.
        if (Vector.compareDistance(this.getCenter(), player.getCenter(), 50) <= 0) {
            this.attack();
        } else if (this.checkAtTarget()) {
            this.setTargetPos(this.player.getCenter());
        }
    }

    public void animate() {
        this.activeCycle.loadNextFrame();
        
        if (this.activeCycle.checkDone()) {
            this.activeCycle.reset();
            this.attackCycle.reset();
            this.activeCycle.setPos(this.getPos());
        }
        
        if (this.checkAtTarget()) {
            this.attack();
        } else {
            this.activeCycle = this.walkCycle;
        }

        this.sword.animate();
    }

    public boolean checkAlive() {
        return this.healthBar.getHealth() > 0;
    }

    public boolean checkAtTarget() {
        return Vector.compareDistance(this.getCenter(), this.targetPos, 100) <= 0;
    }

    public boolean checkAttacking() {
        return this.sword.checkAttacking();
    }

    @Override
    public void moveUp() {
        this.activeCycle = walkCycle;
        this.speed.add(0, -WALK_SPEED);
        this.speed.setLength(WALK_SPEED);
    }

    @Override
    public void moveLeft() {
        this.activeCycle = walkCycle;
        this.speed.add(-WALK_SPEED, 0);
        this.speed.setLength(WALK_SPEED);
    }

    @Override
    public void moveDown() {
        this.activeCycle = walkCycle;
        this.speed.add(0, WALK_SPEED);
        this.speed.setLength(WALK_SPEED);
    }

    @Override
    public void moveRight() {
        this.activeCycle = walkCycle;
        this.speed.add(WALK_SPEED, 0);
        this.speed.setLength(WALK_SPEED);
    }

    @Override
    public int getWidth() {
        return this.activeCycle.getFrameWidth();
    }

    @Override
    public int getHeight() {
        return this.activeCycle.getFrameHeight();
    }

    @Override
    public int getCenterX() {
        RelativeHitbox generalHitbox = (RelativeHitbox) this.getGeneralHitbox();
        return generalHitbox.getX() + generalHitbox.getWidth() / 2;
    }

    @Override
    public int getCenterY() {
        RelativeHitbox generalHitbox = (RelativeHitbox) this.getGeneralHitbox();
        return generalHitbox.getY() + generalHitbox.getHeight() / 2;
    }

    @Override
    public Vector getCenter() {
        return new Vector(this.getCenterX(), this.getCenterY());
    }

    @Override
    public Hitbox getGeneralHitbox() {
        return this.activeCycle.getGeneralHitbox().clone();
    }

    public Vector getSpeed() {
        return this.speed;
    }

    public void setSpeed(Vector newspeed) {
        this.speed = newspeed;
    }

    public void setTargetPos(Vector targetPos) {
        this.targetPos = targetPos;

        if (Double.compare(this.targetPos.getX(), this.getCenterX()) <= 0) {
            this.turnLeft();
        } else {
            this.turnRight();
        }
    }

    @Override
    public void setX(double newX) {
        super.setX(newX);
        this.setPos(this.getPos());
    }

    @Override
    public void setY(double newY) {
        super.setY(newY);
        this.setPos(this.getPos());
    }

    @Override
    public void setPos(Vector newPos) {
        super.setPos(newPos);
        this.activeCycle.setPos(newPos);
        this.healthBar.setPos(Vector.sum(this.getCenter(), new Vector(-this.getWidth() / 2 + 20, -53)));
        this.sword.setPos(newPos);
    }

    public void setMaxHealthPoints(int newMaxHealthPoints) {
        this.healthBar.setMaxPoints(newMaxHealthPoints);
        this.healthBar.setHealth(newMaxHealthPoints);
    }

    @Override
    public boolean contains(int x, int y) {
        return this.activeCycle.contains(x, y);
    }

    @Override
    public boolean intersects(Hitbox other) {
        return this.activeCycle.intersects(other);
    }

    public void attack() {
        if (!this.checkAttacking()) {
            this.activeCycle = this.attackCycle;
            this.sword.attack();
        }
    }

    private void turnLeft() {
        if (this.direction == Const.RIGHT) {
            for (AnimationCycle cycle: this.cycles) {
                cycle.reflectHorizontally();
            }
            this.sword.turnLeft();
        }
        this.direction = Const.LEFT;
    }

    private void turnRight() {
        if (this.direction == Const.LEFT) {
            for (AnimationCycle cycle: this.cycles) {
                cycle.reflectHorizontally();
            }
            this.sword.turnRight();
        }
        this.direction = Const.RIGHT;
    }
}
