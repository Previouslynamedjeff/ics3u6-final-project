import java.awt.Graphics;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

import java.util.Arrays;
import java.util.ArrayList;

public class Player extends Entity implements Moveable, Collidable {
    private static final int WALK_SPEED = 16;

    private ArrayList<AnimationCycle> cycles;
    private AnimationCycle activeCycle;
    private AnimationCycle idleCycle;
    private AnimationCycle attackCycle;
    private AnimationCycle hurtCycle;
    private AnimationCycle walkCycle;

    private int direction;
    private Vector moveSpeed;
    private Vector realSpeed;
    private Map map;
    private Sword sword;
    private HealthBar healthBar;

    public Player(int maxHealthPoints, int swordDamagePoints) {
        super(0, 0, "Player");

        this.idleCycle = new AnimationCycle(this.getPos(), Const.PLAYER_IDLE_SPRITE_SHEET, Const.PLAYER_IDLE_FILE_NAME);
        this.walkCycle = new AnimationCycle(this.getPos(), Const.PLAYER_WALK_SPRITE_SHEET, Const.PLAYER_WALK_FILE_NAME);
        this.attackCycle = new AnimationCycle(this.getPos(), Const.PLAYER_ATTACK_SPRITE_SHEET, Const.PLAYER_ATTACK_FILE_NAME);
        this.hurtCycle = new AnimationCycle(this.getPos(), Const.PLAYER_HURT_SPRITE_SHEET, Const.PLAYER_HURT_FILE_NAME);
        
        this.cycles = new ArrayList<AnimationCycle>();
        this.cycles.add(this.idleCycle);
        this.cycles.add(this.walkCycle);
        this.cycles.add(this.attackCycle);
        this.cycles.add(this.hurtCycle);
        
        this.activeCycle = this.idleCycle;

        this.direction = Const.LEFT;
        this.moveSpeed = Vector.VECTOR_ZERO.clone();
        this.map = null;
        this.sword = new Sword(this.getPos(), swordDamagePoints, this.getName() + "'s Sword");
        this.healthBar = new HealthBar(Vector.sum(this.getCenter(), new Vector(-this.getWidth() / 2, -60)), 
                maxHealthPoints, this.getWidth(), 10);
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
        
        // Draw the coordinates of the player.
        String info = "(" + (Math.round(this.getCenterX() * 10) / 10.0) + 
                ", " + (Math.round(this.getY() * 10) / 10.0) + ")";
        Text text = new Text(info, Const.DEBUG_FONT, (int) this.getCenterX(), (int) this.getY());
        text.draw(graphics);
    }

    public void update() {
        // Handle collisions.
        this.realSpeed = this.moveSpeed.clone();
        this.handleTileCollisions();

        // Update the position.
        Vector newPos = this.getPos();
        newPos.add(this.realSpeed);
        this.setPos(newPos);
    }

    public void animate() {
        this.activeCycle.loadNextFrame();
        
        if (this.activeCycle.checkDone()) {
            this.activeCycle.reset();
            this.attackCycle.reset();
            if (this.moveSpeed.equals(Vector.VECTOR_ZERO)) {
                this.activeCycle = this.idleCycle;
            } else {
                this.activeCycle = this.walkCycle;
            }
            this.activeCycle.setPos(this.getPos());
        }

        this.sword.animate();
    }

    private void handleTileCollisions() {
        RelativeHitbox shiftedHitbox = (RelativeHitbox) this.getGeneralHitbox();

        Vector newRealSpeed = this.realSpeed.getVectorX();
        double speedPercentage = 1.0;

        // Reduce the speed until it can move horizontally.
        shiftedHitbox.setAnchorPos(Vector.sum(this.getPos(), newRealSpeed));
        while (this.map.intersectsWithActiveSolid(shiftedHitbox) && 
                Double.compare(speedPercentage, 0.1) >= 0) {
            speedPercentage -= 0.1;
            speedPercentage = Math.round(speedPercentage * 10) / 10.0;
            newRealSpeed.setX(this.realSpeed.getX() * speedPercentage);
            shiftedHitbox.setAnchorPos(Vector.sum(this.getPos(), newRealSpeed));
        }

        // Reduce the speed until it can move vertically.
        newRealSpeed.setY(this.realSpeed.getY());
        speedPercentage = 1.0;
        shiftedHitbox.setAnchorPos(Vector.sum(this.getPos(), newRealSpeed));
        while (this.map.intersectsWithActiveSolid(shiftedHitbox) && 
                Double.compare(speedPercentage, 0.1) >= 0) {
            speedPercentage -= 0.1;
            speedPercentage = Math.round(speedPercentage * 10) / 10;
            newRealSpeed.setY(this.realSpeed.getY() * speedPercentage);
            shiftedHitbox.setAnchorPos(Vector.sum(this.getPos(), newRealSpeed));
        }

        this.realSpeed = newRealSpeed;
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
        this.healthBar.setPos(Vector.sum(this.getCenter(), new Vector(-this.getWidth() / 2, -60)));
        this.sword.setPos(newPos);
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public void setMaxHealthPoints(int newMaxHealthPoints) {
        this.healthBar.setMaxPoints(newMaxHealthPoints);
        this.healthBar.setHealth(newMaxHealthPoints);
    }

    public void setSwordDamage(int newSwordDamage) {
        this.sword.setDamage(newSwordDamage);
    }

    public boolean checkAttacking() {
        return this.sword.checkAttacking();
    }

    public boolean checkAlive() {
        return this.healthBar.getHealth() > 0;
    }

    public class PlayerKeyListener implements KeyListener {
        private boolean[] pressedKeys;

        public PlayerKeyListener() {
            this.pressedKeys = new boolean[KeyEvent.KEY_LAST + 1];
            Arrays.fill(this.pressedKeys, false);
        }

        private boolean checkKeyValid(int keyCode) {
            return 0 <= keyCode && keyCode < this.pressedKeys.length;
        }

        public void keyTyped(KeyEvent event) {}

        @Override
        public void keyPressed(KeyEvent event) {
            int keyCode = event.getKeyCode();

            if (!checkKeyValid(keyCode) || this.pressedKeys[keyCode]) {
                return;
            }

            this.pressedKeys[keyCode] = true;
            
            // Handle movement input.
            if (keyCode == Const.K_UP) {
                moveUp();
            } 
            if (keyCode == Const.K_LEFT) {
                moveLeft();
            } 
            if (keyCode == Const.K_DOWN) {
                moveDown();
            } 
            if (keyCode == Const.K_RIGHT) {
                moveRight();
            }
        }

        @Override
        public void keyReleased(KeyEvent event) {
            int keyCode = event.getKeyCode();

            if (!checkKeyValid(keyCode)) {
                return;
            }

            this.pressedKeys[keyCode] = false;
            
            if (keyCode == Const.K_UP) {
                if (!this.pressedKeys[Const.K_DOWN]) {
                    moveSpeed.setY(0);
                } else {
                    moveDown();
                }
            } 
            if (keyCode == Const.K_LEFT) {
                if (!this.pressedKeys[Const.K_RIGHT]) {
                    moveSpeed.setX(0);
                } else {
                    moveRight();
                }
            } 
            if (keyCode == Const.K_DOWN) {
                if (!this.pressedKeys[Const.K_UP]) {
                    moveSpeed.setY(0);
                } else {
                    moveUp();
                }
            } 
            if (keyCode == Const.K_RIGHT) {
                if (!this.pressedKeys[Const.K_LEFT]) {
                    moveSpeed.setX(0);
                } else {
                    moveLeft();
                }
            }

            if (moveSpeed.equals(Vector.VECTOR_ZERO)) {
                activeCycle = idleCycle;
                activeCycle.setPos(getPos());
            } else {
                moveSpeed.setLength(WALK_SPEED);
            }
        }
    };

    public class PlayerMouseListener implements MouseListener {
        public void mousePressed(MouseEvent event) {
            int x = event.getX() + (int) getCenterX() - Const.WIDTH / 2;
            int y = event.getY() + (int) getCenterY() - Const.HEIGHT / 2;

            if (contains(x, y)) {
                takeDamage(250);
            } else {
                if (!checkAttacking()) {
                    attack();
                }
            }
        }

        public void mouseReleased(MouseEvent event) {}
        public void mouseClicked(MouseEvent event) {}
        public void mouseEntered(MouseEvent event) {}
        public void mouseExited(MouseEvent event) {}
    }

    public class PlayerMouseMotionListener implements MouseMotionListener {
        public void mouseDragged(MouseEvent event) {}

        @Override
        public void mouseMoved(MouseEvent event) {
            if (checkAttacking()) {
                return;
            }

            int x = event.getX() + (int) getCenterX() - Const.WIDTH / 2;

            if (x < getCenterX()) {
                turnLeft();
            } else {
                turnRight();
            }
        }
    }

    @Override
    public void moveUp() {
        this.activeCycle = walkCycle;
        this.moveSpeed.setY(-WALK_SPEED);
        this.moveSpeed.setLength(WALK_SPEED);
    }

    @Override
    public void moveLeft() {
        this.activeCycle = walkCycle;
        this.moveSpeed.setX(-WALK_SPEED);
        this.moveSpeed.setLength(WALK_SPEED);
    }

    @Override
    public void moveDown() {
        this.activeCycle = walkCycle;
        this.moveSpeed.setY(WALK_SPEED);
        this.moveSpeed.setLength(WALK_SPEED);
    }

    @Override
    public void moveRight() {
        this.activeCycle = walkCycle;
        this.moveSpeed.setX(WALK_SPEED);
        this.moveSpeed.setLength(WALK_SPEED);
    }

    public void attack() {
        this.activeCycle = this.attackCycle;
        this.sword.attack();
        this.activeCycle.setPos(getPos());
    }

    public void takeDamage(int damagePoints) {
        this.healthBar.takeDamage(damagePoints);
        this.activeCycle = this.hurtCycle;
    }

    @Override
    public boolean contains(int x, int y) {
        return this.activeCycle.contains(x, y);
    }

    @Override
    public boolean intersects(Hitbox other) {
        return this.activeCycle.intersects(other);
    }

    public boolean intersects(AnimationCycle otherCycle) {
        return this.activeCycle.intersects(otherCycle);
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
