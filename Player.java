import java.awt.Graphics;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public class Player extends Entity implements Moveable {
    private static final int WALK_SPEED = 3;

    private AnimationCycle activeCycle;
    private AnimationCycle idleCycle;
    private AnimationCycle attackCycle;
    private AnimationCycle hurtCycle;
    private AnimationCycle walkCycle;

    private Vector speed;

    public Player() {
        super(0, 0, "Leto");
        this.idleCycle = new AnimationCycle(this.getPos(), Const.playerIdleSpriteSheet, 4, true);
        this.walkCycle = new AnimationCycle(this.getPos(), Const.playerWalkSpriteSheet, 6, true);
        this.attackCycle = new AnimationCycle(this.getPos(), Const.playerAttackSpriteSheet, 6, false);
        this.hurtCycle = new AnimationCycle(this.getPos(), Const.playerHurtSpriteSheet, 2, false);

        this.idleCycle.setLooping(true);
        this.walkCycle.setLooping(true);

        this.speed = Vector.VECTOR_ZERO.clone();

        this.activeCycle = idleCycle;
    }

    @Override
    public void draw(Graphics graphics) {
        this.activeCycle.draw(graphics);
    }

    public void update() {
        Vector newPos = this.getPos();
        newPos.add(this.speed);
        this.setPos(newPos);
    }

    public void animate() {
        this.activeCycle.loadNextFrame();
        
        if (this.activeCycle.checkDone()) {
            this.activeCycle.reset();
            this.activeCycle = this.idleCycle;
            this.activeCycle.setPos(this.getPos());
        }
    }

    @Override
    public void drawDebugInfo(Graphics graphics) {
        this.activeCycle.drawDebugInfo(graphics);
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
    public void setX(double newX) {
        super.setX(newX);
        this.activeCycle.setPos(this.getPos());
    }

    @Override
    public void setY(double newY) {
        super.setY(newY);
        this.activeCycle.setPos(this.getPos());
    }

    @Override
    public void setPos(Vector newPos) {
        super.setPos(newPos);
        this.activeCycle.setPos(newPos);
    }

    public class PlayerKeyListener implements KeyListener {
        private Window window;

        public PlayerKeyListener(Window window) {
            this.window = window;
        }

        public void keyTyped(KeyEvent event) {}

        @Override
        public void keyPressed(KeyEvent event) {
            int keyCode = event.getKeyCode();
            
            switch (keyCode) {
                case Const.K_ESC:
                    window.switchToScreen(Const.PAUSE_SCREEN_NAME);
                    break;
                case Const.K_UP:
                    moveUp();
                    break;
                case Const.K_LEFT:
                    moveLeft();
                    break;
                case Const.K_DOWN:
                    moveDown();
                    break;
                case Const.K_RIGHT:
                    moveRight();
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent event) {
            int keyCode = event.getKeyCode();
            
            switch (keyCode) {
                case Const.K_UP:
                    stopMoving();
                    break;
                case Const.K_LEFT:
                    stopMoving();
                    break;
                case Const.K_DOWN:
                    stopMoving();
                    break;
                case Const.K_RIGHT:
                    stopMoving();
                    break;
            }

        }
    };

    public class PlayerMouseListener implements MouseListener {
        public void mousePressed(MouseEvent event) {
            if (event.isShiftDown()) {
                activeCycle = hurtCycle;
            } else {
                activeCycle = attackCycle;
            }
            activeCycle.setPos(getPos());
        }

        public void mouseReleased(MouseEvent event) {

        }
        
        public void mouseClicked(MouseEvent event) {}
        public void mouseEntered(MouseEvent event) {}
        public void mouseExited(MouseEvent event) {}
    }

    @Override
    public void moveUp() {
        this.activeCycle = walkCycle;
        this.speed.setY(-WALK_SPEED);
    }

    @Override
    public void moveLeft() {
        this.activeCycle = walkCycle;
        this.speed.setX(-WALK_SPEED);
    }

    @Override
    public void moveDown() {
        this.activeCycle = walkCycle;
        this.speed.setY(WALK_SPEED);
    }

    @Override
    public void moveRight() {
        this.activeCycle = walkCycle;
        this.speed.setX(WALK_SPEED);
    }
    
    public void stopMoving() {
        this.activeCycle = idleCycle;
        this.speed.setX(0);
        this.speed.setY(0);
        this.activeCycle.setPos(this.getPos());
    }
}
