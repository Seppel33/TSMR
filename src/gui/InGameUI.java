package gui;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
import engine.ShapesRendererPP;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingNode;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import simulation.PhysicsThreadManager;
import java.io.File;



public class InGameUI {
    private static final int FPS = 60;
    public ShapesRendererPP gameWindow;
    private Main main;
    public InGameUI(Main m){
        main = m;
    }
    public void innitFX(){
        int width = Main.getScreenWidth();
        int height = Main.getScreenHeight();

        //starten der 3D View
        GLProfile profile = GLProfile.get(GLProfile.GL3);
        GLCapabilities capabilities = new GLCapabilities(profile);
        capabilities.setSampleBuffers(true);
        capabilities.setNumSamples(16);

        gameWindow = new ShapesRendererPP(capabilities, width, height); //ShapesRendererPP GLJPanel

        SwingNode swingNode = new SwingNode();
        swingNode.setContent(gameWindow);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(0, swingNode);

        FPSAnimator animator = new FPSAnimator(gameWindow, FPS, true);

        //2D Ansicht
        TwoDUI twoD = new TwoDUI(gameWindow.getPhysics());
        stackPane.getChildren().add(1,twoD);

        AnchorPane.setRightAnchor(twoD, (double)0);
        AnchorPane.setTopAnchor(twoD, (double)0);

        gameWindow.settDUI(twoD);


        File exit = new File("res\\return.png");
        Image image = new Image(exit.toURI().toString());
        Button bnt1 = new Button();
        bnt1.setPrefSize(40, 40);
        bnt1.setGraphic(new ImageView(image));
        bnt1.setId("btnInGame");
        twoD.getChildren().add(bnt1);

        AnchorPane.setBottomAnchor(bnt1, (double) height/30);
        AnchorPane.setLeftAnchor(bnt1, (double) width *0.05);

        bnt1.setOnAction(actionEvent -> {
            main.setMainMenu();
            Main.inGameUI.gameWindow.setOnEsc(true);
            Main.inGameUI.gameWindow.destroy();
        });

        Text mspeed = new Text("Speed:" + " " +  "m/s");
        mspeed.setFill(new Color(0.9,0.9,0.9,1));
        mspeed.setId("mspeed");
        twoD.getChildren().add(mspeed);

        AnchorPane.setBottomAnchor(mspeed, (double) height*0.04);
        AnchorPane.setLeftAnchor(mspeed, (double) width *0.25);


        File undo = new File("res\\undo.png");
        Image image1 = new Image(undo.toURI().toString());
        Button button5 = new Button();
        button5.setPrefSize(40, 40);
        button5.setGraphic(new ImageView(image1));
        button5.setId("btnInGame");
        twoD.getChildren().add(button5);

        AnchorPane.setBottomAnchor(button5, (double) height/30);
        AnchorPane.setLeftAnchor(button5, (double) width *0.45);


        File play = new File("res\\play.png");
        Image image2 = new Image(play.toURI().toString());
        Button button7 = new Button();
        button7.setPrefSize(40, 40);
        button7.setGraphic(new ImageView(image2));
        button7.setId("btnInGame");
        twoD.getChildren().add(button7);


        AnchorPane.setBottomAnchor(button7, (double) height/30);
        AnchorPane.setLeftAnchor(button7, (double) width*0.55);

        Text sliderText=new Text("Animationspeed");
        sliderText.setId("textStuff");
        sliderText.setFill(new Color(0.9,0.9,0.9,1));
        sliderText.setFont(new Font("Avenir",25));
        twoD.getChildren().add(sliderText);
        AnchorPane.setBottomAnchor(sliderText, (double) height*0.07);
        AnchorPane.setLeftAnchor(sliderText, (double) width*0.75);

        Slider speedSlider = new Slider();
        speedSlider.setId("speedSlider");
        speedSlider.setPrefSize(300,0);
        speedSlider.setMin(-3);
        speedSlider.setMax(3);
        speedSlider.setValue(1);
        speedSlider.setMajorTickUnit(1);
        speedSlider.setBlockIncrement(0.05);
        speedSlider.setSnapToTicks(true);
        speedSlider.setShowTickLabels(true);
        twoD.getChildren().add(speedSlider);

        AnchorPane.setBottomAnchor(speedSlider, (double) height*0.03);
        AnchorPane.setLeftAnchor(speedSlider, (double) width*0.75);

        Scene scene3 = new Scene(stackPane, width, height);
        File cssFile = new File("src\\gui\\inGameStyle.css");
        String cssFileString = cssFile.getAbsoluteFile().toURI().toString();
        scene3.getStylesheets().clear();
        scene3.getStylesheets().add(cssFileString);


        scene3.addEventHandler(Event.ANY, gameWindow.getInteractionHandler());

        Main.primaryStage.setScene(scene3);

        animator.start();

        //Slider für die Geschwindigkeit;
        speedSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            ShapesRendererPP.setSpeedSliderValue(newValue.doubleValue());
        });
        // Reset Button
        button5.setOnAction(actionEvent -> {
            ShapesRendererPP.setReset(true);
            Image imageChange = new Image(new File("res\\play.png").toURI().toString());
            button7.setGraphic(new ImageView(imageChange));
        });

        //Start/Pausen Button
        button7.setOnAction(actionEvent -> {

            if (ShapesRendererPP.isPause()) {
                Image imageChange = new Image(new File("res\\stop.png").toURI().toString());
                button7.setGraphic(new ImageView(imageChange));
                ShapesRendererPP.setPause(false);
            } else {
                Image imageChange = new Image(new File("res\\play.png").toURI().toString());
                button7.setGraphic(new ImageView(imageChange));
                ShapesRendererPP.setPause(true);
            }
        });

        //Anzeige der Geschwindigkeit der aktuellen Murmel
        Timeline speedUpdate = new Timeline(new KeyFrame(Duration.millis(16), event -> {
            int frameIndex = (int)ShapesRendererPP.getFrameIndex();
            if(frameIndex>=PhysicsThreadManager.getMarblePointList().size())frameIndex=PhysicsThreadManager.getMarblePointList().size()-1;
            if (frameIndex>1 && twoD.isOnMarble()) {
                double[] marblePos = {PhysicsThreadManager.getMarblePointList().get(frameIndex)[twoD.getWhichMarble()][0], PhysicsThreadManager.getMarblePointList().get(frameIndex)[twoD.getWhichMarble()][1], PhysicsThreadManager.getMarblePointList().get(frameIndex)[twoD.getWhichMarble()][2]};
                double[] marbleOldPos = {PhysicsThreadManager.getMarblePointList().get(frameIndex - 1)[twoD.getWhichMarble()][0], PhysicsThreadManager.getMarblePointList().get(frameIndex - 1)[twoD.getWhichMarble()][1], PhysicsThreadManager.getMarblePointList().get(frameIndex - 1)[twoD.getWhichMarble()][2]};

                double speed = Math.sqrt((marblePos[0] - marbleOldPos[0]) * (marblePos[0] - marbleOldPos[0]) + (marblePos[1]
                        - marbleOldPos[1]) * (marblePos[1] - marbleOldPos[1]) + (marblePos[2] - marbleOldPos[2]) * (marblePos[2] - marbleOldPos[2])) * 60 * 5;
                if (speed > 0.001) {
                    String speedText = "" + speed+"000000";

                    speedText = speedText.substring(0, 6);
                    mspeed.setText(speedText + " cm/s");
                } else {
                    mspeed.setText("0.0000 cm/s");
                }


            }else mspeed.setText("0.0000 cm/s");
        }));
        speedUpdate.setCycleCount(Timeline.INDEFINITE);
        speedUpdate.play();
    }
}

