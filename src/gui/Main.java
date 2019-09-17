package gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.awt.*;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**VM-Options:      --module-path "D:\Java Librarys\javafx-sdk-12\lib" --add-modules=javafx.controls,javafx.fxml,javafx.media,javafx.swing          **/
/**VM-Options:bela      --module-path "C:\Users\Bela Korb\Documents\javafx-sdk-12.0.1\lib" --add-modules=javafx.controls,javafx.fxml,javafx.media,javafx.swing          **/

public class Main extends Application {
    public static Stage primaryStage;
    public static int selectedLevel;
    private static int width;
    private static int height;
    public static Scene scene2;
    double t;
    private Timer timer;
public static InGameUI inGameUI;
    private Label[] drops;
    private double[][] dropPoints;
    private AnchorPane anchorPane1;

    public static Main getM() {
        return m;
    }

    private static Main m;

    @Override
    public void start(Stage primaryStageX) throws Exception{
        m=this;
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Screen screen = Screen.getPrimary();
        //Skalierung der Bildschirmgöße entsprechend der Windows System Skalierung
        double widthScale=screen.getOutputScaleX();
        double heightScale=screen.getOutputScaleY();
        width = (int)(gd.getDisplayMode().getWidth()*0.8/widthScale);
        height = (int)(gd.getDisplayMode().getHeight()*0.8/heightScale);
        primaryStageX.setResizable(false);
        primaryStage = primaryStageX;
        primaryStage.setTitle("T.S.M.R.");
        primaryStage.setOnCloseRequest(we -> {
            System.out.println("Stage is closing");
            if(!(timer==null)) {
                timer.purge();
                timer.cancel();
            }
            System.exit(0);
        });

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        Font.loadFont("res\\Cracked Code.ttf", 10);

        //"Tropfen" im Hauptmenu
        drops = new Label[13];
        Image[] images = new Image[13];
        for(int i =0; i<13;i++){
            drops[i]=new Label();
            String pathname=new File("res\\menuDrops\\"+(i+1)+".png").toURI().toString();
            images[i]=new Image(pathname);
            drops[i].setGraphic(new ImageView(new Image(pathname,(width/1536f)*widthScale*images[i].getWidth(),(width/1536f)*widthScale*images[i].getWidth(),false,false)));
        }

        dropPoints = new double[13][2];
        dropPoints[0][0]=35.0*0.8/widthScale;
        dropPoints[0][1]=140.0*0.8/heightScale;

        dropPoints[1][0]=197.0*0.8/widthScale;
        dropPoints[1][1]=730.0*0.8/heightScale;

        dropPoints[2][0]=305.0*0.8/widthScale;
        dropPoints[2][1]=495.0*0.8/heightScale;

        dropPoints[3][0]=552.0*0.8/widthScale;
        dropPoints[3][1]=896.0*0.8/heightScale;

        dropPoints[4][0]=618.0*0.8/widthScale;
        dropPoints[4][1]=930.0*0.8/heightScale;

        dropPoints[5][0]=584.0*0.8/widthScale;
        dropPoints[5][1]=246.0*0.8/heightScale;

        dropPoints[6][0]=861.0*0.8/widthScale;
        dropPoints[6][1]=103.0*0.8/heightScale;

        dropPoints[7][0]=1112.0*0.8/widthScale;
        dropPoints[7][1]=598.0*0.8/heightScale;

        dropPoints[8][0]=1389.0*0.8/widthScale;
        dropPoints[8][1]=838.0*0.8/heightScale;

        dropPoints[9][0]=1458.0*0.8/widthScale;
        dropPoints[9][1]=128.0*0.8/heightScale;

        dropPoints[10][0]=1498.0*0.8/widthScale;
        dropPoints[10][1]=838.0*0.8/heightScale;

        dropPoints[11][0]=1612.0*0.8/widthScale;
        dropPoints[11][1]=569.0*0.8/heightScale;

        dropPoints[12][0]=1673.0*0.8/widthScale;
        dropPoints[12][1]=795.0*0.8/heightScale;


        Scene scene = new Scene(grid,width,height);

        primaryStage.setScene(scene);

        File f = new File("res\\intro.mp4");
        Media media = new Media(f.toURI().toString());
        // Create the player and set to play automatically.
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);

        // Create the view and add it to the Scene.
        MediaView mediaView = new MediaView(mediaPlayer);
        ((GridPane) scene.getRoot()).getChildren().add(mediaView);
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaView.setVisible(false);
            mediaPlayer.stop();
            mediaPlayer.dispose();
            setMainMenu();
        });

        primaryStage.addEventFilter(MouseEvent.MOUSE_CLICKED, e-> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.setStopTime(mediaPlayer.getCurrentTime());
                }
            }
        });
        primaryStage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }

    //Timer für die Positionierung der "Tropfen"
    private void startTimer(){
        for(int x =0; x<13;x++){
            AnchorPane.setLeftAnchor(drops[x],dropPoints[x][0]);
            AnchorPane.setTopAnchor(drops[x],dropPoints[x][1]);
        }

        double[] randomValue1 = new double[13];
        double[] randomValue2 = new double[13];

        for(int i = 0;i<13;i++){
            randomValue1[i]=Math.random();
            randomValue2[i]=Math.random();
        }
        t =0;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                for(int i = 0;i<13;i++){
                    AnchorPane.setTopAnchor(drops[i],dropPoints[i][1]+Math.sin(t*randomValue1[i]*4)*(randomValue2[i]+0.3)*100);
                }
                t+=0.002;
            }
        };
        timer = new Timer();
        timer.schedule(timerTask,0,16);
    }
    private void stopTimer(){
        timer.cancel();
        timer.purge();
        timer = null;
    }

    public void setMainMenu(){
        anchorPane1 = new AnchorPane();

        Label background = new Label();
        background.setPrefSize(width,height);
        background.setGraphic(new ImageView(new Image(new File("res\\background.png").toURI().toString(),width,height,false, false)));

        Label background2 = new Label();
        background2.setPrefSize(width,height);
        background2.setGraphic(new ImageView(new Image(new File("res\\background2.png").toURI().toString(),width,height,false, false)));

        anchorPane1.getChildren().add(background);
        //übertragung der "Tropfen" auf aktuelle Scene
        for(int x =0; x<13;x++){
            anchorPane1.getChildren().add(drops[x]);
        }
        anchorPane1.getChildren().add(background2);

        if(timer == null){
            startTimer();
        }

        Text btn1Text = new Text("Choose Level");
        btn1Text.setId("menuButtonsText");
        Button btn1 = new Button("",btn1Text);
        btn1.setId("menuButtons");
        btn1.setPrefSize(width/5.6058f, height/18.6206f);
        VBox vBtn = new VBox(height/20);

        vBtn.setAlignment(Pos.CENTER);
        vBtn.getChildren().add(btn1);


        Text btn2Text = new Text("About Us");
        btn2Text.setId("menuButtonsText");
        Button btn2 = new Button("",btn2Text);
        btn2.setId("menuButtons");
        btn2.setPrefSize(width/5.6058f, height/18.6206f);
        vBtn.getChildren().add(btn2);

        Text btn3Text = new Text("Exit");
        btn3Text.setId("menuButtonsText");
        Button btn3 = new Button("",btn3Text);
        btn3.setId("menuButtons");
        btn3.setPrefSize(width/5.6058f, height/18.6206f);
        vBtn.getChildren().add(btn3);

        //relative Positionierung der Buttons
        AnchorPane.setTopAnchor(vBtn, ((double) height/2-((height/18.6206f)*3+(height/20)*2)/2));
        AnchorPane.setLeftAnchor(vBtn, ((double) width/2-(width/5.6058f)/2));
        System.out.println(vBtn.getHeight());
        anchorPane1.getChildren().add(vBtn);

        btn1.setOnAction(actionEvent -> setLevelLoadMenu());

        btn2.setOnAction(actionEvent -> setAboutUsMenu());

        btn3.setOnAction(actionEvent -> System.exit(0));

        scene2 = new Scene(anchorPane1,width,height);
        File fl = new File("src\\gui\\stylesheet.css");
        String f2 =fl.getAbsoluteFile().toURI().toString();
        scene2.getStylesheets().clear();
        scene2.getStylesheets().add(f2);
        primaryStage.setScene(scene2);
    }
    private void setLevelLoadMenu(){
        AnchorPane anchorPane = new AnchorPane();

        Label background = new Label();
        background.setPrefSize(width,height);
        background.setGraphic(new ImageView(new Image(new File("res\\background.png").toURI().toString(),width,height,false, false)));

        Label background2 = new Label();
        background2.setPrefSize(width,height);
        background2.setGraphic(new ImageView(new Image(new File("res\\background2.png").toURI().toString(),width,height,false, false)));

        anchorPane.getChildren().add(background);

        //übertragung der "Tropfen" auf aktuelle Scene
        for(int x =0; x<13;x++){
            anchorPane.getChildren().add(drops[x]);
        }

        anchorPane.getChildren().add(background2);

        Text btn1Text = new Text("Level 1");
        btn1Text.setId("menuButtonsText");
        Button btn1 = new Button("",btn1Text);
        btn1.setId("menuButtons");
        btn1.setPrefSize(width/5.6058f, height/18.6206f);
        VBox vBtn = new VBox(height/20);

        vBtn.setAlignment(Pos.CENTER);
        vBtn.getChildren().add(btn1);

        Text btn2Text = new Text("Level 2");
        btn2Text.setId("menuButtonsText");
        Button btn2 = new Button("",btn2Text);
        btn2.setId("menuButtons");
        btn2.setPrefSize(width/5.6058f, height/18.6206f);
        vBtn.getChildren().add(btn2);

        Text btn3Text = new Text("Level 3");
        btn3Text.setId("menuButtonsText");
        Button btn3 = new Button("",btn3Text);
        btn3.setId("menuButtons");
        btn3.setPrefSize(width/5.6058f, height/18.6206f);
        vBtn.getChildren().add(btn3);


        File exit = new File("res\\return.png");
        Image image = new Image(exit.toURI().toString());
        Button bnt4 = new Button();
        bnt4.setPrefSize(40, 40);
        bnt4.setGraphic(new ImageView(image));
        bnt4.setId("exit");
        anchorPane.getChildren().add(bnt4);



        btn1.setOnAction(actionEvent -> {
            Main.selectedLevel = 1;
            inGameUI = new InGameUI(m);
            inGameUI.innitFX();
            stopTimer();
        });

        btn2.setOnAction(actionEvent -> {
            Main.selectedLevel = 2;
            inGameUI = new InGameUI(m);
            inGameUI.innitFX();
            stopTimer();
        });

        btn3.setOnAction(actionEvent -> {
            Main.selectedLevel = 3;
            inGameUI = new InGameUI(m);
            inGameUI.innitFX();
            stopTimer();
        });

        bnt4.setOnAction(actionEvent -> setMainMenu());

        AnchorPane.setBottomAnchor(bnt4, (double) height/30);
        AnchorPane.setLeftAnchor(bnt4, (double) width * 0.05);

        AnchorPane.setTopAnchor(vBtn, ((double) height/2-((height/18.6206f)*3+(height/20)*2)/2));
        AnchorPane.setLeftAnchor(vBtn, ((double) width/2-(width/5.6058f)/2)); 
        System.out.println(vBtn.getHeight());
        anchorPane.getChildren().add(vBtn);

        Scene scene4 = new Scene(anchorPane,width,height);
        File fl = new File("src\\gui\\stylesheet.css");
        String f2 =fl.getAbsoluteFile().toURI().toString();
        scene4.getStylesheets().clear();
        scene4.getStylesheets().add(f2);

        primaryStage.setScene(scene4);

    }
    private void setAboutUsMenu(){
        AnchorPane anchorPane = new AnchorPane();

        Label background = new Label();
        background.setPrefSize(width,height);
        background.setGraphic(new ImageView(new Image(new File("res\\background.png").toURI().toString(),width,height,false, false)));

        Label background2 = new Label();
        background2.setPrefSize(width,height);
        background2.setGraphic(new ImageView(new Image(new File("res\\background2.png").toURI().toString(),width,height,false, false)));

        anchorPane.getChildren().add(background);

        //übertragung der "Tropfen" auf aktuelle Scene
        for(int x =0; x<13;x++){
            anchorPane.getChildren().add(drops[x]);
        }

        anchorPane.getChildren().add(background2);


        Text text1 = new Text("Bela Korb");
        text1.setFont(new Font("Avenir",18));
        text1.setId("aboutUsText");
        text1.prefWidth(58);
        text1.setTextAlignment(TextAlignment.CENTER);
        VBox vBtn = new VBox(50);
        vBtn.setAlignment(Pos.CENTER);
        vBtn.getChildren().add(text1);

        Text text2 = new Text("Sebastian Schmidt");
        text2.setFont(new Font("Avenir",18));
        text2.setId("aboutUsText");
        text2.prefWidth(58);
        text2.setTextAlignment(TextAlignment.CENTER);
        vBtn.getChildren().add(text2);

        Text text3 = new Text("Michelle Wetscheck");
        text3.setFont(new Font("Avenir",18));
        text3.setId("aboutUsText");
        text3.prefWidth(58);
        text3.setTextAlignment(TextAlignment.CENTER);
        vBtn.getChildren().add(text3);


        File exit = new File("res\\return.png");
        Image image = new Image(exit.toURI().toString());
        Button bnt1 = new Button();
        bnt1.setPrefSize(40, 40);
        bnt1.setGraphic(new ImageView(image));
        bnt1.setId("exit");
        anchorPane.getChildren().add(bnt1);

        bnt1.setOnAction(actionEvent -> setMainMenu());

        AnchorPane.setBottomAnchor(bnt1, (double) height/30);
        AnchorPane.setLeftAnchor(bnt1, (double) width * 0.05);

        AnchorPane.setTopAnchor(vBtn, ((double) height/2-200/2));
        AnchorPane.setLeftAnchor(vBtn, ((double) width/2-200/2));
        System.out.println(vBtn.getHeight());
        anchorPane.getChildren().add(vBtn);

        Scene scene5 = new Scene(anchorPane,width,height);
        File fl = new File("src\\gui\\stylesheet.css");
        String f3 =fl.getAbsoluteFile().toURI().toString();
        scene5.getStylesheets().clear();
        scene5.getStylesheets().add(f3);


        primaryStage.setScene(scene5);

    }

    public static int getScreenWidth(){
        return width;
    }

    public static int getScreenHeight(){
        return height;
    }

}



