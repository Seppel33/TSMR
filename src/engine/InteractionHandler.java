package engine;
import gui.Main;
import javafx.event.*;
import javafx.event.Event;
import javafx.scene.input.*;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class InteractionHandler implements EventHandler<Event>{

    private float angleXaxis = 0f;
    private float angleYaxis = 0f;
    private float angleXaxisInc = 1f;
    private float angleYaxisInc = 1f;

    private boolean leftMouseButtonPressed = false;
    private boolean rightMouseButtonPressed = false;
    private Point lastMouseLocation = new Point(0,0);
    // Taking care of the screen size (mapping of mouse coordinates to angle/translation)
    private final float mouseRotationFactor = 0.5f;
    private final float mouseTranslationFactor = 0.1f;
    private final float mouseWheelScrollFactor = 10f;
    private float cameraSpeed = 0.1f;

    private boolean wKeyPressed = false;
    private boolean aKeyPressed = false;
    private boolean sKeyPressed = false;
    private boolean dKeyPressed = false;
    private static boolean escKeyPressed = false;
    private boolean kKeyPressed = false;
    private final Set<String> pressed = new HashSet<String>();

    public InteractionHandler() {
        escKeyPressed= false;
    }

    @Override
    public void handle(Event event) {
        //System.out.println(event.getEventType().toString());
        switch (event.getEventType().toString()){
            case "MOUSE_MOVED":
                mouseMoved((MouseEvent)event);
                break;
            case "MOUSE_CLICKED":
                mouseClicked((MouseEvent)event);
                break;
            case "MOUSE_DRAGGED":
                mouseDragged((MouseEvent) event);
                break;
            case "MOUSE_ENTERED_TARGET":
                mouseEntered((MouseEvent)event);
                break;
            case "MOUSE_EXITED_TARGET":
                mouseExited((MouseEvent)event);
                break;
            case "MOUSE_EXITED":
                break;
            case "MOUSE_PRESSED":
                mousePressed((MouseEvent) event);
                break;
            case "MOUSE_RELEASED":
                mouseReleased((MouseEvent)event);
                break;
            case "CONTEXTMENUREQUESTED":
                break;
            case "KEY_PRESSED":
                keyPressed((KeyEvent)event);
                break;
            case "KEY_TYPED":
                keyTyped((KeyEvent)event);
                break;
            case "KEY_RELEASED":
                keyReleased((KeyEvent)event);
                break;
            case "SCROLL":
                break;
        }
    }

    public void keyPressed(KeyEvent event) {
        pressed.add(event.getCharacter());

        if(event.getCode().toString().equals("ESCAPE")){
            Main.getM().setMainMenu();
            Main.inGameUI.gameWindow.setOnEsc(true);
            Main.inGameUI.gameWindow.destroy();
        }

    }

    public void keyReleased(KeyEvent e) {
/*
        pressed.remove(e.getKeyChar());
        switch(e.getKeyChar()){
            case 'w':
                wKeyPressed =false;
                break;
            case 'a':
                aKeyPressed =false;
                break;
            case 's':
                sKeyPressed =false;
                break;
            case 'd':
                dKeyPressed =false;
                break;
        }
 */
    }


    public void keyTyped(KeyEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {

        MouseButton releasedButton = e.getButton();
        switch (releasedButton.toString()) {
            case "PRIMARY":
                leftMouseButtonPressed = true;
                lastMouseLocation.x = (int)e.getSceneX();
                lastMouseLocation.y = (int)e.getSceneY();
                break;
            case "SECONDARY":
                rightMouseButtonPressed = true;
                lastMouseLocation.x = (int)e.getSceneX();
                lastMouseLocation.y = (int)e.getSceneY();
                break;
        }

    }

    public void mouseReleased(MouseEvent e) {
        MouseButton releasedButton = e.getButton();
        //System.out.println(releasedButton);

        switch (releasedButton.toString()) {
            case "PRIMARY":
                leftMouseButtonPressed = false;
                break;
            case "SECONDARY":
                rightMouseButtonPressed = false;
                break;
        }

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseDragged(MouseEvent e) {
        if(!escKeyPressed) {
            Point currentMouseLocation = new Point((int)e.getSceneX(),(int)e.getSceneY());
            double deltaX = currentMouseLocation.getX() - lastMouseLocation.getX();
            double deltaY = currentMouseLocation.getY() - lastMouseLocation.getY();
            angleYaxis += angleYaxisInc * mouseRotationFactor * deltaX;
            angleXaxis += angleXaxisInc * mouseRotationFactor * deltaY;
            // System.out.println("angleXAxis: " + angleXaxis);
            if (angleXaxis > 77) {
                angleXaxis = 77;
            } else if (angleXaxis < -13) {
                angleXaxis = -13;
            }
            lastMouseLocation.x = (int)e.getSceneX();
            lastMouseLocation.y = (int)e.getSceneY();
        }
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseWheelMoved(MouseEvent e) {}

    public boolean iswKeyPressed() {
        return wKeyPressed;
    }

    public boolean isaKeyPressed() {
        return aKeyPressed;
    }

    public boolean issKeyPressed() {
        return sKeyPressed;
    }

    public boolean isdKeyPressed() {
        return dKeyPressed;
    }

    public boolean iskKeyPressed() {
        return kKeyPressed;
    }

    public boolean isEscKeyPressed() {
        return escKeyPressed;
    }
    public static void setIsEscKeyPressed(boolean pressed){
        escKeyPressed = pressed;
    }
    public float getAngleXaxis() {
        return angleXaxis;
    }

    public float getAngleYaxis() {
        return angleYaxis;
    }

    public void setAngleYaxis(float angleYaxis) {
        this.angleYaxis = angleYaxis;
    }
}
